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
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import com.denizenscript.denizen2sponge.utilities.EntityTemplate;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityScript extends CommandScript {

    // <--[explanation]
    // @Since 0.5.0
    // @Name Entity Scripts
    // @Group Script Types
    // @Description
    // An entity script is a type of script that fully defines specific entity template, which can
    // be used to spawn entities afterwards. Keys in an entity script define which properties the
    // final entity will have.
    //
    // An entity script can be used in place of any normal entity type, by putting the name of the
    // entity script where a script expects an entity type. A script may block this from user input by
    // requiring a valid EntityTypeTag input, which will not recognize an item script.
    //
    // The entity script name may not be the same as an existing entity type name.
    //
    // Entities generated from an entity script will remember their type using flag "_d2_script".
    // To stop this from occurring, set key "plain" to "true".
    //
    // Set key "static" to "true" on the entity script to make it load once at startup and simply be duplicated on
    // all usages. If static is false or unspecified, the entity script will be loaded from data at each call
    // requesting it. This is likely preferred if any tags are used within the script.
    //
    // All options listed below are used to define the entity's specific details.
    // They all support tags on input. All options other than "base" may use the automatically
    // included definition tag <[base]> to get a map of the base entity's properties.
    //
    // Set key "base" directly to an EntityTypeTag of the basic entity type to use. You may also
    // use an existing entity script to inherit its properties. Be careful to not list the
    // entity script within itself, even indirectly, as this can cause recursion errors.
    //
    // Set key "display name" directly to a TextTag or FormattedTextTag value of the name the entity should have.
    //
    // Set key "flags" as a section and within it put all flags keyed by name and with the value that each flag should hold.
    // If you wish to dynamically structure the mapping, see the "keys" option for specifying that.
    //
    // To specify other values, create a section labeled "keys" and within it put any valid item keys.
    // TODO: Create and reference an explanation of basic entity keys.
    // -->

    public EntityScript(String name, YAMLConfiguration section) {
        super(name, section);
        entityScriptName = CoreUtilities.toLowerCase(name);
    }

    public final String entityScriptName;

    private static final CommandQueue FORCE_TO_STATIC = new CommandQueue(); // Special case recursive static generation helper

    @Override
    public boolean init() {
        if (super.init()) {
            try {
                prepValues();
                Action<String> error = (es) -> {
                    throw new ErrorInducedException(es);
                };
                if (contents.contains("static") && BooleanTag.getFor(error, contents.getString("static")).getInternal()) {
                    staticEntity = getEntityCopy(FORCE_TO_STATIC);
                }
            }
            catch (ErrorInducedException ex) {
                Debug.error("Entity generation for " + ColorSet.emphasis + title + ColorSet.warning + ": " + ex.getMessage());
                return false;
            }
            Denizen2Sponge.entityScripts.put(entityScriptName, this);
            return true;
        }
        return false;
    }

    public EntityTemplate staticEntity = null;

    public EntityTemplate getEntityCopy(CommandQueue queue) {
        if (staticEntity != null) {
            return staticEntity.copy();
        }
        return generateEntity(queue);
    }

    public Argument displayName, plain, base;

    public List<Tuple<String, Argument>> otherValues, flags;

    public void prepValues() {
        Action<String> error = (es) -> {
            throw new ErrorInducedException(es);
        };
        if (Sponge.getRegistry().getType(ItemType.class, title).isPresent()) {
            Debug.error("Entity script " + title + " may be unusable: a base entity type exists with that name!");
        }
        if (contents.contains("display name")) {
            displayName = Denizen2Core.splitToArgument(contents.getString("display name"), true, true, error);
        }
        if (contents.contains("plain")) {
            plain = Denizen2Core.splitToArgument(contents.getString("plain"), true, true, error);
        }
        if (contents.contains("base")) {
            base = Denizen2Core.splitToArgument(contents.getString("base"), true, true, error);
        }
        else {
            throw new ErrorInducedException("Base key is missing. Cannot generate!");
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
        Action<String> error = (es) -> {
            throw new ErrorInducedException(es);
        };
        return arg.parse(queue, varBack, getDebugMode(), error);
    }

    public EntityTemplate generateEntity(CommandQueue queue) {
        Action<String> error = (es) -> {
            throw new ErrorInducedException(es);
        };
        EntityTemplate ent;
        HashMap<String, AbstractTagObject> varBack = new HashMap<>();
        String baseStr = parseVal(queue, base, varBack).toString();
        EntityType entType = (EntityType) Utilities.getTypeWithDefaultPrefix(EntityType.class, baseStr);
        if (entType != null) {
            ent = new EntityTemplate(entType);
        }
        else {
            String baseLow = CoreUtilities.toLowerCase(baseStr);
            if (Denizen2Sponge.entityScripts.containsKey(baseLow)) {
                EntityTemplate baseEnt = Denizen2Sponge.entityScripts.get(baseLow).getEntityCopy(queue);
                ent = new EntityTemplate(baseEnt);
                MapTag baseProperties = new MapTag(baseEnt.properties.getInternal());
                baseProperties.getInternal().put("type", new EntityTypeTag(ent.type));
                varBack.put("base", baseProperties);
            }
            else {
                throw new ErrorInducedException("No entity types or scripts found for id '" + baseStr + "'.");
            }
        }
        MapTag properties = new MapTag();
        if (displayName != null) {
            properties.getInternal().put("display_name", parseVal(queue, displayName, varBack));
        }
        if (otherValues != null) {
            for (Tuple<String, Argument> input : otherValues) {
                properties.getInternal().put(input.one, parseVal(queue, input.two, varBack));
            }
        }
        MapTag flagsMap;
        AbstractTagObject ato = ent.properties.getInternal().get("flagmap");
        if (ato != null) {
            flagsMap = new MapTag(((MapTag) ato).getInternal());
        }
        else {
            flagsMap = new MapTag();
        }
        if (flags != null) {
            for (Tuple<String, Argument> flagVal : flags) {
                flagsMap.getInternal().put(flagVal.one, parseVal(queue, flagVal.two, varBack));
            }
        }
        if (plain == null || !BooleanTag.getFor(error, parseVal(queue, plain, varBack)).getInternal()) {
            flagsMap.getInternal().put("_d2_script", new ScriptTag(this));
        }
        if (!flagsMap.getInternal().isEmpty()) {
            properties.getInternal().put("flagmap", flagsMap);
        }
        ent.addProperties(properties);
        if (queue == FORCE_TO_STATIC && contents.contains("static")
                && BooleanTag.getFor(error, contents.getString("static")).getInternal()) {
            staticEntity = ent;
        }
        return ent;
    }

    @Override
    public boolean isExecutable(String section) {
        return false;
    }
}
