package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;

public class MountCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name mount
    // @Arguments <entity> <entity list>
    // @Short mounts all the entities in the list onto the given entity.
    // @Updated 2017/02/12
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Mounts all the entities in the list onto the given entity.
    // TODO: Explain more!
    // @Example
    // # This example mounts the player on the nearest minecart (or errors if there is none!)
    // - mount <player.location.nearby_entities[type:minecart|range:3].get[1]> <player>
    // -->

    @Override
    public String getName() {
        return "mount";
    }

    @Override
    public String getArguments() {
        return "<entity> <entity list>";
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
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        ListTag list = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (AbstractTagObject ato : list.getInternal()) {
            EntityTag tEnt = EntityTag.getFor(queue.error, ato);
            ent.getInternal().addPassenger(tEnt.getInternal());
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Mounted " + ColorSet.emphasis + list.debug() + ColorSet.good + " entities on entity: "
                    + ent.debug());
        }
    }
}
