package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;

public class ViewDistanceCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name viewdistance
    // @Arguments <world> [view distance]
    // @Short sets the view distance of a world.
    // @Updated 2018/01/01
    // @Group World
    // @Minimum 1
    // @Maximum 2
    // @Description
    // Sets the view distance of a world (in chunks). This value must be between 3 and
    // 32 inclusive. Don't specify a view distance to reset it to the world's default value.
    // Entities won't be visible for players further than the view distance from them.
    // @Example
    // # Sets the view distance to 3 chunks in world 'Nightmare'.
    // - viewdistance Nightmare 3
    // @Example
    // # Resets the view distance in world 'Survival'.
    // - viewdistance Survival
    // -->

    @Override
    public String getName() {
        return "viewdistance";
    }

    @Override
    public String getArguments() {
        return "<world> [view distance]";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        WorldTag world = WorldTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        if (entry.arguments.size() > 1) {
            IntegerTag distance = IntegerTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
            world.getInternal().setViewDistance((int) distance.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Set view distance in world '" + ColorSet.emphasis + world.debug()
                        + ColorSet.good + "' to: " + ColorSet.emphasis + distance.debug() + ColorSet.good + "!");
            }
        }
        else {
            world.getInternal().resetViewDistance();
            if (queue.shouldShowGood()) {
                queue.outGood("Reset view distance in world '" + ColorSet.emphasis + world.debug()
                        + ColorSet.good + "' to its default value!");
            }
        }
    }
}
