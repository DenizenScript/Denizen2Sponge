package com.denizenscript.denizen2sponge.events;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.tags.objects.*;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;

import java.util.List;
import java.util.Optional;

public class D2SpongeEventHelper {

    public static boolean checkBlockType(BlockType btype, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkBlockType(btype, data, error, "type");
    }

    public static boolean checkBlockType(BlockType btype, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if (BlockTypeTag.getFor(error, ato).getInternal().equals(btype)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkEntityType(EntityType etype, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkEntityType(etype, data, error, "type");
    }

    public static boolean checkEntityType(EntityType etype, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if (EntityTypeTag.getFor(error, ato).getInternal().equals(etype)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkWorld(World world, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkWorld(world, data, error, "world");
    }

    public static boolean checkWorld(World world, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if (WorldTag.getFor(error, ato).getInternal().equals(world)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkString(String inpStr, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if (CoreUtilities.toLowerCase(ato.toString()).equals(inpStr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCuboid(UtilLocation location, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkCuboid(location, data, error, "cuboid");
    }

    public static boolean checkCuboid(UtilLocation location, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if (CuboidTag.getFor(error, ato).contains(location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkWeather(String weather, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkWeather(weather, data, error, "weather");
    }

    public static boolean checkWeather(String weather, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            Optional<Weather> type = Sponge.getRegistry().getType(Weather.class, ato.toString());
            if (!type.isPresent()) {
                error.run("Invalid weather type: '" + ato.debug() + "'!");
                return false;
            }
            else if (type.get().getId().equals(weather)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkHandType(String hand, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkHandType(hand, data, error, "hand");
    }

    public static boolean checkHandType(String hand, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            Optional<HandType> type = Sponge.getRegistry().getType(HandType.class, ato.toString());
            if (!type.isPresent()) {
                error.run("Invalid hand type: '" + ato.debug() + "'!");
                return false;
            }
            else if (type.get().getId().equals(hand)) {
                return true;
            }
        }
        return false;
    }

    // <--[explanation]
    // @Name With Item Switch For Events
    // @Group Events
    // @Description
    // Some events contain an "with_item:" switch.
    // This is a special-case-switch with sub-options!
    //
    // For example, you would write "with_item:type:stick" to check if the item is of TYPE stick!
    //
    // Note that this syntax additionally allows for multiple options, as well as sub-lists for specific potential options.
    // "with_item:type:stick&pipestone|quantity:5|flagged:best_stick_or_stone"
    //
    // Options:
    // type: (ItemTypeTag) checks if the item type matches.
    // Quantity: (IntegerTag) checks if the quantity is at least a value.
    // Flagged: (TextTag) checks if the item has a flag.
    // -->

    public static boolean checkItem(ItemTag itm, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkItem(itm, data, error, "with_item");
    }

    public static boolean checkItem(ItemTag itm, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            String val = ato.toString();
            List<String> vals = CoreUtilities.split(val, ':', 1);
            if (vals.size() < 2) {
                continue;
            }
            String t = CoreUtilities.toLowerCase(vals.get(0));
            String v = vals.get(1);
            if (t.equals("type")) {
                boolean poss = false;
                for (AbstractTagObject ato_sub : ListTag.getFor(error, v).getInternal()) {
                    if (itm.getInternal().getType().equals(ItemTypeTag.getFor(error, ato_sub).getInternal())) {
                        poss = true;
                    }
                }
                if (!poss) {
                    return false;
                }
            }
            if (t.equals("quantity")) {
                if (itm.getInternal().getQuantity() < IntegerTag.getFor(error, v).getInternal()) {
                    return false;
                }
            }
            else if (t.equals("flagged")) {
                boolean poss = false;
                MapTag flags;
                Optional<FlagMap> fm = itm.getInternal().get(FlagHelper.FLAGMAP);
                if (fm.isPresent()) {
                    flags = fm.get().flags;
                }
                else {
                    flags = new MapTag();
                }
                for (AbstractTagObject ato_sub : ListTag.getFor(error, v).getInternal()) {
                    if (flags.getInternal().containsKey(CoreUtilities.toLowerCase(ato_sub.toString()))) {
                        poss = true;
                    }
                }
                if (!poss) {
                    return false;
                }
            }
        }
        return true;
    }
}
