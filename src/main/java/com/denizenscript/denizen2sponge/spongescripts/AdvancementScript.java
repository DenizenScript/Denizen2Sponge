package com.denizenscript.denizen2sponge.spongescripts;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.scripts.CommandScript;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2core.utilities.yaml.StringHolder;
import com.denizenscript.denizen2core.utilities.yaml.YAMLConfiguration;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTypeTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import com.flowpowered.math.vector.Vector2d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.*;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.advancement.AdvancementTreeEvent;
import org.spongepowered.api.event.game.GameRegistryEvent;

import java.util.HashMap;
import java.util.Optional;

public class AdvancementScript extends CommandScript {

    public static HashMap<String, AdvancementScript> currentAdvancementScripts = new HashMap<>();

    // <--[explanation]
    // @Since 0.4.0
    // @Name Advancement Scripts
    // @Group Script Types
    // @Description
    // An advancement script is a script that adds new advancements to the server.
    // It is identified with the type "advancement".
    // Expected keys: id (string), name (string, optional), criteria (map), display
    // (advancement type), title (FormattedTextTag), description (FormattedTextTag),
    // announce (boolean), show_toast (boolean), hidden (boolean), icon (item type),
    // parent (id, only for children advancements), tree_id (string, only for root
    // advancements), tree_name (string, only for root advancements, optional),
    // position_x (number), and position_y (number).
    // TODO: Add "Related information" such as criteria formatting, advancement types and examples.
    // -->

    public AdvancementScript(String name, YAMLConfiguration section) {
        super(name, section);
    }

    @Override
    public boolean isExecutable(String section) {
        return false;
    }

    @Override
    public boolean init() {
        if (super.init()) {
            register();
            return true;
        }
        return false;
    }

    public Advancement advancement;

    public AdvancementTree tree = null;

    public Vector2d position;

    public void register() {
        Sponge.getEventManager().registerListeners(Denizen2Sponge.instance, this);
        String id = contents.getString("id");
        if (Denizen2Core.getImplementation().generalDebug()) {
            Debug.good("Registering advancement script " + ColorSet.emphasis + id
                    + ColorSet.good + " to Denizen2Sponge...");
        }
        Advancement.Builder builder = Advancement.builder().id(id);
        if (contents.contains("name")) {
            builder.name(contents.getString("name"));
        }
        AdvancementCriterion criterion = AdvancementCriterion.EMPTY;
        YAMLConfiguration criteria = contents.getConfigurationSection("criteria");
        for (StringHolder strh : criteria.getKeys(false)) {
            String critNumber = strh.low;
            String critName = criteria.getString(critNumber + ".name");
            AdvancementCriterion.BaseBuilder<?, ?> toAdd = AdvancementCriterion.builder().name(critName);
            if (criteria.contains(critName + ".score")) {
                boolean score = BooleanTag.getFor(Debug::error,
                        criteria.getString(critName + ".score")).getInternal();
                if (score) {
                    IntegerTag goal = IntegerTag.getFor(Debug::error,
                            criteria.getString(critName + ".goal"));
                    ((ScoreAdvancementCriterion.Builder) toAdd).goal((int) goal.getInternal());
                }
            }
            criterion = criterion.and(toAdd.build());
        }
        builder.criterion(criterion);
        String typeId = contents.getString("display");
        Optional<AdvancementType> type = Sponge.getRegistry().getType(AdvancementType.class, typeId);
        if (!type.isPresent()) {
            Debug.error("The display specified is not a valid advancement type!");
            return;
        }
        DisplayInfo.Builder info = DisplayInfo.builder().type(type.get());
        AbstractTagObject title = Denizen2Core.splitToArgument(
                contents.getString("title"), false, false, Debug::error)
                .parse(new CommandQueue(), new HashMap<>(), getDebugMode(), Debug::error);
        if (title instanceof FormattedTextTag) {
            info.title(((FormattedTextTag) title).getInternal());
        }
        else {
            info.title(Denizen2Sponge.parseColor(title.toString()));
        }
        AbstractTagObject desc = Denizen2Core.splitToArgument(
                contents.getString("description"), false, false, Debug::error)
                .parse(new CommandQueue(), new HashMap<>(), getDebugMode(), Debug::error);
        if (desc instanceof FormattedTextTag) {
            info.title(((FormattedTextTag) desc).getInternal());
        }
        else {
            info.title(Denizen2Sponge.parseColor(desc.toString()));
        }
        BooleanTag announce = BooleanTag.getFor(Debug::error, contents.getString("announce"));
        info.announceToChat(announce.getInternal());
        BooleanTag show = BooleanTag.getFor(Debug::error, contents.getString("show_toast"));
        info.showToast(show.getInternal());
        BooleanTag hidden = BooleanTag.getFor(Debug::error, contents.getString("hidden"));
        info.hidden(hidden.getInternal());
        ItemTypeTag icon = ItemTypeTag.getFor(Debug::error, contents.getString("icon"));
        info.icon(icon.getInternal());
        builder.displayInfo(info.build());
        if (criteria.contains("parent")) {
            String parentId = contents.getString("parent");
            Advancement parent = (Advancement) Utilities.getTypeWithDefaultPrefix(Advancement.class, parentId);
            if (parent == null) {
                Debug.error("There's no registered advancement for the parent id specified!");
                return;
            }
            builder.parent(parent);
            advancement = builder.build();
        }
        else {
            advancement = builder.build();
            String treeId = contents.getString("tree_id");
            AdvancementTree.Builder treeBuilder = AdvancementTree.builder().id(treeId);
            if (contents.contains("tree_name")) {
                treeBuilder.name(contents.getString("tree_name"));
            }
            if (contents.contains("tree_background")) {
                treeBuilder.background(contents.getString("tree_background"));
            }
            treeBuilder.rootAdvancement(advancement);
            tree = treeBuilder.build();
        }
        currentAdvancementScripts.put(id, this);
        NumberTag x = NumberTag.getFor(Debug::error, contents.getString("x_position"));
        NumberTag y = NumberTag.getFor(Debug::error, contents.getString("y_position"));
        position = new Vector2d(x.getInternal(), y.getInternal());
    }

    public boolean isTreeRoot() {
        return tree != null;
    }

    @Listener
    public void onRegisterAdvancementTrees(GameRegistryEvent.Register<AdvancementTree> event) {
        if (isTreeRoot()) {
            event.register(tree);
            Debug.good("Registering advancement tree '" + tree.getId() + "' to Sponge...");
        }
    }

    @Listener
    public void onRegisterAdvancements(GameRegistryEvent.Register<Advancement> event) {
        event.register(advancement);
        Debug.good("Registering advancement '" + advancement.getId() + "' to Sponge...");
    }

    @Listener
    public void onGenerateTreeLayout(AdvancementTreeEvent.GenerateLayout event) {
        if (isTreeRoot() && event.getTree() == tree) {
            for (TreeLayoutElement element : event.getLayout().getElements()) {
                Vector2d pos = currentAdvancementScripts
                        .get(Utilities.getIdWithoutDefaultPrefix(element.getAdvancement().getId())).position;
                element.setPosition(pos);
            }
            Debug.good("Updating layout for tree '" + tree.getId() + "'...");
        }
    }
}
