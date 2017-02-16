package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.entity.Entity;

import java.util.Map;
import java.util.Optional;

public class FlagCommand extends AbstractCommand {

    // <--[command]
    // @Name flag
    // @Arguments <entity> <map of flags to set>
    // @Short flags an entity with some data.
    // @Updated 2017/02/15
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Adds or edits flags on an entity (including players, etc.).
    // See also the <@link command unflag>unflag command<@/link>.
    // @Example
    // # Mark the player as a VIP.
    // - flag <player> vip:true
    // -->

    @Override
    public String getName() {
        return "flag";
    }

    @Override
    public String getArguments() {
        return "<entity> <map of flags to set>";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        EntityTag entityTag = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Entity entity = entityTag.getInternal();
        MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        MapTag basic;
        Optional<FlagMap> fm = entity.get(FlagHelper.FLAGMAP);
        if (fm.isPresent()) {
            basic = fm.get().flags;
        }
        else {
            basic = new MapTag();
        }
        for (Map.Entry<String, AbstractTagObject> dat : propertyMap.getInternal().entrySet()) {
            basic.getInternal().put(CoreUtilities.toLowerCase(dat.getKey()), dat.getValue());
        }
        entity.offer(new FlagMapDataImpl(new FlagMap(basic)));
        if (queue.shouldShowGood()) {
            queue.outGood("Flagged the entity "
                    + ColorSet.emphasis + entityTag.friendlyName() + ColorSet.good
                    + " with the specified data... (" + propertyMap + ")");
        }
    }
}
