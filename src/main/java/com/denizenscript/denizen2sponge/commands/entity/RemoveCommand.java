package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;

public class RemoveCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name remove
    // @Arguments <entity list>
    // @Short removes a list of entities.
    // @Updated 2017/04/02
    // @Group Entity
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Removes a list of entities from the world.
    // Related commands: <@link command spawn>spawn<@/link>.
    // @Example
    // # This example removes all zombies within a 3 block range of the player
    // - remove <player.location.nearby_entities[type:zombie|range:3]>
    // -->

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getArguments() {
        return "<entity list>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        ListTag list = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        for (AbstractTagObject ato : list.getInternal()) {
            EntityTag ent = EntityTag.getFor(queue.error, ato);
            ent.getInternal().remove();
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Removing entities: " + ColorSet.emphasis + list.debug() + ColorSet.good + "!");
        }
    }
}
