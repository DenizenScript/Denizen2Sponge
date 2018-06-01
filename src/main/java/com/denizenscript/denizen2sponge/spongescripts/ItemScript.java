package com.denizenscript.denizen2sponge.spongescripts;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.arguments.Argument;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.scripts.CommandScript;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.ScriptTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2core.utilities.Tuple;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2core.utilities.yaml.StringHolder;
import com.denizenscript.denizen2core.utilities.yaml.YAMLConfiguration;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.*;

public class ItemScript extends CommandScript {

    // <--[explanation]
    // @Since 0.5.0
    // @Name Item Scripts
    // @Group Script Types
    // @Description
    // An item script is a type of script that fully defines a specific in-game item stack.
    // Keys in an item script define the various properties of an item stack.
    //
    // An item script can be used in place of any normal item, by putting the name of the item script
    // where a script expects an item type. A script may block this from user input by
    // requiring a valid ItemTypeTag input, which will not recognize an item script.
    //
    // The item script name may not be the same as an existing item type name.
    //
    // ItemStacks generated from an item script will remember their type using flag "_d2_script".
    // To stop this from occurring, set key "plain" to "true".
    //
    // Set key "static" to "true" on the item script to make it load once at startup and simply be duplicated on all usages.
    // If static is false or unspecified, the item script will be loaded from data at each call requesting it.
    // This is likely preferred if any tags are used within the script.
    //
    // Quantity values should be specified in the script requesting the item (For example, in a give command)
    // as that is likely very localized. The item stack given directly by the item script system will have a quantity of 1.
    //
    // All options listed below are used to define the item's specific details. They all support tags on input.
    // All options other than "material" may use the automatically included definition tag <[material]> to get the base material.
    //
    // Set key "material" directly to an ItemTag of the basic item type to use. You may list an item type, or an existing item.
    // Be careful to not list the item script within itself, even indirectly, as this can cause recursion errors.
    //
    // Set key "display name" directly to a TextTag or FormattedTextTag value of the name the item should have.
    //
    // Set key "lore" as a list key of lines the item should have.
    // If you wish to dynamically structure the list, see the "other values" option for specifying that.
    //
    // Set key "flags" as a section and within it put all flags keyed by name and with the value that each flag should hold.
    // If you wish to dynamically structure the mapping, see the "keys" option for specifying that.
    //
    // To specify other values, create a section labeled "keys" and within it put any valid item keys.
    // TODO: Create and reference an explanation of basic item keys.
    // -->

    public ItemScript(String name, YAMLConfiguration section) {
        super(name, section);
        itemScriptName = CoreUtilities.toLowerCase(name);
    }

    public final String itemScriptName;

    private static final CommandQueue FORCE_TO_STATIC; // Special case recursive static generation helper

    static {
        (FORCE_TO_STATIC = new CommandQueue()).error = (es) -> {
            throw new ErrorInducedException(es);
        };
    }

    @Override
    public boolean init() {
        if (super.init()) {
            try {
                prepValues();
                if (contents.contains("static") && BooleanTag.getFor(FORCE_TO_STATIC.error, contents.getString("static")).getInternal()) {
                    staticItem = getItemCopy(FORCE_TO_STATIC);
                }
            }
            catch (ErrorInducedException ex) {
                Debug.error("Item generation for " + ColorSet.emphasis + title + ColorSet.warning + ": " + ex.getMessage());
                return false;
            }
            Denizen2Sponge.itemScripts.put(itemScriptName, this);
            return true;
        }
        return false;
    }

    public ItemStack staticItem = null;

    public ItemStack getItemCopy(CommandQueue queue) {
        if (staticItem != null) {
            return staticItem.copy();
        }
        return generateItem(queue);
    }

    public Argument displayName, plain, material;

    public List<Argument> lore;

    public List<Tuple<String, Argument>> otherValues, flags;

    public void prepValues() {
        Action<String> error = (es) -> {
            throw new ErrorInducedException(es);
        };
        if (Sponge.getRegistry().getType(ItemType.class, title).isPresent()) {
            Debug.error("Item script may be unusable: a base item type exists with the same name!");
        }
        if (contents.contains("display name")) {
            displayName = Denizen2Core.splitToArgument(contents.getString("display name"), true, true, error);
        }
        if (contents.contains("plain")) {
            plain = Denizen2Core.splitToArgument(contents.getString("plain"), true, true, error);
        }
        if (contents.contains("material")) {
            material = Denizen2Core.splitToArgument(contents.getString("material"), true, true, error);
        }
        else {
            throw new ErrorInducedException("Material key is missing. Cannot generate!");
        }
        if (contents.contains("lore")) {
            List<String> listLore = contents.getStringList("lore");
            lore = new ArrayList<>();
            for (String line : listLore) {
                lore.add(Denizen2Core.splitToArgument(line, true, true, error));
            }
        }
        if (contents.contains("flags")) {
            flags = new ArrayList<>();
            YAMLConfiguration sec = contents.getConfigurationSection("flags");
            for (StringHolder key : sec.getKeys(false)) {
                Argument arg = Denizen2Core.splitToArgument(sec.getString(key.str), true, true, error);
                flags.add(new Tuple<>(CoreUtilities.toUpperCase(key.low), arg));
            }
        }
        if (contents.contains("keys")) {
            otherValues = new ArrayList<>();
            YAMLConfiguration sec = contents.getConfigurationSection("keys");
            for (StringHolder key : sec.getKeys(false)) {
                Argument arg = Denizen2Core.splitToArgument(sec.getString(key.str), true, true, error);
                otherValues.add(new Tuple<>(CoreUtilities.toUpperCase(key.low), arg));
            }
        }
    }

    public AbstractTagObject parseVal(CommandQueue queue, Argument arg, HashMap<String, AbstractTagObject> varBack) {
        return arg.parse(queue, varBack, getDebugMode(), queue.error);
    }

    public ItemStack generateItem(CommandQueue queue) {
        HashMap<String, AbstractTagObject> varBack = new HashMap<>();
        ItemTag baseMat = ItemTag.getFor(queue.error, parseVal(queue, material, varBack), queue);
        varBack.put("material", baseMat);
        ItemStack.Builder its = ItemStack.builder().from(baseMat.getInternal().copy()).quantity(1);
        if (displayName != null) {
            AbstractTagObject ato = parseVal(queue, displayName, varBack);
            if (ato instanceof FormattedTextTag) {
                its = its.add(Keys.DISPLAY_NAME, ((FormattedTextTag) ato).getInternal());
            }
            else {
                its = its.add(Keys.DISPLAY_NAME, Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        if (lore != null) {
            List<Text> loreVal = new ArrayList<>();
            for (Argument arg : lore) {
                AbstractTagObject ato = parseVal(queue, arg, varBack);
                if (ato instanceof FormattedTextTag) {
                    loreVal.add(((FormattedTextTag) ato).getInternal());
                }
                else {
                    loreVal.add(Denizen2Sponge.parseColor(ato.toString()));
                }
            }
            its.add(Keys.ITEM_LORE, loreVal);
        }
        MapTag flagsMap;
        Optional<FlagMap> fm = baseMat.getInternal().get(FlagHelper.FLAGMAP);
        if (fm.isPresent()) {
            flagsMap = new MapTag(fm.get().flags.getInternal());
        }
        else {
            flagsMap = new MapTag();
        }
        if (flags != null) {
            for (Tuple<String, Argument> flagVal : flags) {
                flagsMap.getInternal().put(flagVal.one, parseVal(queue, flagVal.two, varBack));
            }
        }
        if (plain == null || !BooleanTag.getFor(queue.error, parseVal(queue, plain, varBack)).getInternal()) {
            flagsMap.getInternal().put("_d2_script", new ScriptTag(this));
        }
        ItemStack toRet = its.build();
        if (otherValues != null) {
            for (Tuple<String, Argument> input : otherValues) {
                Key k = DataKeys.getKeyForName(input.one);
                if (k == null) {
                    queue.error.run("Error handling item script '" + ColorSet.emphasis + title + ColorSet.warning
                            + "': key '" + ColorSet.emphasis + input.one + ColorSet.warning + "' does not seem to exist.");
                    return null;
                }
                DataKeys.tryApply(toRet, k, parseVal(queue, input.two, varBack), queue.error);
            }
        }
        if (!flagsMap.getInternal().isEmpty()) {
            toRet.offer(new FlagMapDataImpl(new FlagMap(flagsMap)));
        }
        if (queue == FORCE_TO_STATIC && contents.contains("static")
                && BooleanTag.getFor(queue.error, contents.getString("static")).getInternal()) {
            staticItem = toRet;
        }
        return toRet;
    }

    @Override
    public boolean isExecutable(String section) {
        return false;
    }
}
