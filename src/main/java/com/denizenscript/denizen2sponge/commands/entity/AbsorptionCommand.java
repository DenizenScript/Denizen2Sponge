package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;

public class AbsorptionCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.5.5
    // @Name absorption
    // @Arguments <entity> <value>
    // @Short changes the absorption points of an entity.
    // @Updated 2018/06/15
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Changes the absorption points of an entity. You can specify whether
    // the command will 'add' or 'set' the points. Defaults to 'add'.
    // @Example
    // # This example increases the player's absorption points by 10 (5 golden hearts).
    // - absorption <player> 10
    // -->

    @Override
    public String getName() {
        return "absorption";
    }

    @Override
    public String getArguments() {
        return "<entity> <value>";
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
        NumberTag nt = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        boolean set = false;
        if (entry.namedArgs.containsKey("operation")) {
            String operation = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "operation").toString());
            if (operation.equals("set")) {
                set = true;
            }
            else if (!operation.equals("add")) {
                queue.handleError(entry, "Invalid operation: '" + operation + "'!");
            }
        }
        ent.getInternal().offer(Keys.ABSORPTION,
                set ? nt.getInternal() : ent.getInternal().get(Keys.ABSORPTION).orElse(0.0) + nt.getInternal());
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (set ? "Setting" : "Increasing") + ColorSet.good
                    + " the absorption points of entity '" + ColorSet.emphasis + ent.debug()
                    + ColorSet.good + "' " + (set ? "to" : "by") + " '" + ColorSet.emphasis
                    + nt.debug() + ColorSet.good + "'!");
        }
    }
}
