package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;

public class BurnCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name burn
    // @Arguments <entity> <duration>
    // @Short sets an entity on fire.
    // @Updated 2018/02/17
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Sets an entity on fire for the specified duration. You can also specify whether
    // the command will 'add' or 'set' the duration. Defaults to 'add'.
    // @Example
    // # This example sets the player on fire for additional 5 seconds.
    // - burn <player> 5s
    // -->

    @Override
    public String getName() {
        return "burn";
    }

    @Override
    public String getArguments() {
        return "<entity> <duration>";
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
        DurationTag dt = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        int ticks = (int) dt.getInternal() * 20;
        boolean set;
        if (entry.namedArgs.containsKey("operation")) {
            String operation = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "operation").toString());
            switch (operation) {
                case "add":
                    set = false;
                    break;
                case "set":
                    set = true;
                    break;
                default:
                    queue.handleError(entry, "Invalid operation: '" + operation + "'!");
                    return;
            }
        }
        else {
            set = false;
        }
        ent.getInternal().offer(Keys.FIRE_TICKS,
                set ? ticks : ent.getInternal().get(Keys.FIRE_TICKS).orElse(0) + ticks);
        if (queue.shouldShowGood()) {
            queue.outGood("Setting entity '" + ColorSet.emphasis + ent.debug() + ColorSet.good + "' on fire for '"
                    + ColorSet.emphasis + dt.debug() + ColorSet.good + "' " + ColorSet.emphasis
                    + (set ? "total" : "additional") + ColorSet.good + " seconds!");
        }
    }
}
