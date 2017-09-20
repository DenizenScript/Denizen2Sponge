package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.entity.Entity;

import java.util.Optional;

public class UnflagCommand extends AbstractCommand {

    // <--[command]
    // @Name unflag
    // @Arguments <entity>/'server' <list of flags to remove>
    // @Short removes a list of flags from an entity.
    // @Updated 2017/02/15
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Removes flags from an entity (including players, etc.).
    // See also the <@link command flag>flag command<@/link>.
    // @Example
    // # Mark the player as no longer VIP.
    // - unflag <player> vip
    // -->

    @Override
    public String getName() {
        return "unflag";
    }

    @Override
    public String getArguments() {
        return "<entity>/'server' <list of flags to remove>";
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
        ListTag toRemove = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Entity entity = null;
        AbstractTagObject ato = entry.getArgumentObject(queue, 0);
        MapTag basic;
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
        for (AbstractTagObject dat : toRemove.getInternal()) {
            basic.getInternal().remove(CoreUtilities.toLowerCase(dat.toString()));
        }
        if (entity != null) {
            entity.offer(new FlagMapDataImpl(new FlagMap(basic)));
            if (queue.shouldShowGood()) {
                queue.outGood("Removed from the entity "
                        + ColorSet.emphasis + new EntityTag(entity).debug() + ColorSet.good
                        + " the specified flags... (" + toRemove.debug() + ")");
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("Removed from the server the specified flags... (" + toRemove.debug() + ")");
            }
        }
    }
}
