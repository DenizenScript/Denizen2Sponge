package com.denizenscript.denizen2sponge.events;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;

public class D2SpongeEventHelper {

    public static boolean checkBlockType(BlockType btype, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkBlockType(btype, data, error, "type");
    }

    public static boolean checkBlockType(BlockType btype, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if ((BlockTypeTag.getFor(error, ato)).getInternal().equals(btype)) {
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
            if ((EntityTypeTag.getFor(error, ato)).getInternal().equals(etype)) {
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
            if ((WorldTag.getFor(error, ato)).getInternal().equals(world)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkHandType(String htype, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkHandType(htype, data, error, "hand");
    }

    public static boolean checkHandType(String htype, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        if (!data.switches.containsKey(tname)) {
            return true;
        }
        for (AbstractTagObject ato : ListTag.getFor(error, data.switches.get(tname)).getInternal()) {
            if ((TextTag.getFor(error, ato)).getInternal().toUpperCase().equals(htype)) {
                return true;
            }
        }
        return false;
    }
}
