package com.denizenscript.denizen2sponge.events;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.EntityType;

public class D2SpongeEventHelper {

    public static boolean checkBlockType(BlockType btype, ScriptEvent.ScriptEventData data, Action<String> error) {
        // TODO: type_list as well?
        return !data.switches.containsKey("type")
                || BlockTypeTag.getFor(error, data.switches.get("type")).getInternal()
                .equals(btype);
    }

    public static boolean checkEntityType(EntityType etype, ScriptEvent.ScriptEventData data, Action<String> error) {
        // TODO: type_list as well?
        return !data.switches.containsKey("type")
                || EntityTypeTag.getFor(error, data.switches.get("type")).getInternal()
                .equals(etype);
    }
}
