package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;

public class InvisibleCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name invisible
    // @Arguments <entity> <state>
    // @Short changes the invisibility state of an entity.
    // @Updated 2018/02/11
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Changes the invisibility state of an entity. Invisible entities can't be seen,
    // but update packets are still sent to the server and clients.
    // Related commands: <@link command vanish>vanish<@/link>.
    // @Example
    // # Makes the player invisible.
    // - invisible <player> true
    // -->

    @Override
    public String getName() {
        return "invisible";
    }

    @Override
    public String getArguments() {
        return "<entity> <state>";
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
        EntityTag entity = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        BooleanTag state = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        entity.getInternal().offer(Keys.INVISIBLE, state.getInternal());
        if (queue.shouldShowGood()) {
            queue.outGood("Setting invisible state of entity '" + ColorSet.emphasis + entity.debug()
                    + ColorSet.good + "' to '" + ColorSet.emphasis + state.debug() + ColorSet.good + "'!");
        }
    }
}
