package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
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
    // @Arguments <entity>/'server' <map of flags to set>
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
        return "<entity>/'server' <map of flags to set>";
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
        AbstractTagObject ato = entry.getArgumentObject(queue, 0);
        MapTag basic;
        Entity entity = null;
        if (CoreUtilities.toLowerCase(ato.toString()).equals("server")) {
            basic = Denizen2Sponge.instance.serverFlagMap;
        }
        else {
            EntityTag entityTag = EntityTag.getFor(queue.error, ato);
            entity = entityTag.getInternal();
            Optional<FlagMap> fm = entity.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                basic = fm.get().flags;
            }
            else {
                basic = new MapTag();
            }
        }
        MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> dat : propertyMap.getInternal().entrySet()) {
            basic.getInternal().put(CoreUtilities.toLowerCase(dat.getKey()), dat.getValue());
        }
        if (entity != null) {
            entity.offer(new FlagMapDataImpl(new FlagMap(basic)));
            if (queue.shouldShowGood()) {
                queue.outGood("Flagged the entity "
                        + ColorSet.emphasis + new EntityTag(entity).debug() + ColorSet.good
                        + " with the specified data... (" + propertyMap.debug() + ")");
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("Flagged the server with the specified data... (" + propertyMap.debug() + ")");
            }
        }
    }
}
