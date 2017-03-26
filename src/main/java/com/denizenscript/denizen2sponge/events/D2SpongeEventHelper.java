package com.denizenscript.denizen2sponge.events;

import com.denizenscript.denizen2core.events.ScriptEvent;
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
        // TODO: type_list as well?
        return !data.switches.containsKey(tname)
                || BlockTypeTag.getFor(error, data.switches.get(tname)).getInternal()
                .equals(btype);
    }

    public static boolean checkEntityType(EntityType etype, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkEntityType(etype, data, error, "type");
    }

    public static boolean checkEntityType(EntityType etype, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        // TODO: type_list as well?
        return !data.switches.containsKey(tname)
                || EntityTypeTag.getFor(error, data.switches.get(tname)).getInternal()
                .equals(etype);
    }

    public static boolean checkWorld(World world, ScriptEvent.ScriptEventData data, Action<String> error) {
        return checkWorld(world, data, error, "world");
    }

    public static boolean checkWorld(World world, ScriptEvent.ScriptEventData data, Action<String> error, String tname) {
        // TODO: type_list as well?
        return !data.switches.containsKey(tname)
                || WorldTag.getFor(error, data.switches.get(tname)).getInternal()
                .equals(world);
    }
}
