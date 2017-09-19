package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;

public class UnloadWorldCommand extends AbstractCommand {

    // <--[command]
    // @Name unloadworld
    // @Arguments <world>
    // @Short unloads a world.
    // @Updated 2017/05/16
    // @Group World
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Unloads a world. If this world has any connected players, no operation will occur.
    // @Example
    // # Unloads the world 'Games'.
    // - unloadworld Games
    // -->

    @Override
    public String getName() {
        return "unloadworld";
    }

    @Override
    public String getArguments() {
        return "<world>";
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
        WorldTag world = WorldTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        if (queue.shouldShowGood()) {
            queue.outGood("Attempting to unload world '" + ColorSet.emphasis + world.debug() + ColorSet.good + "'...");
        }
        Boolean unloaded = Sponge.getServer().unloadWorld(world.getInternal());
        if (!unloaded) {
            queue.handleError(entry, "World unloading failed!");
            return;
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Unloaded world '" + ColorSet.emphasis + world.debug() + ColorSet.good + "'!");
        }
    }
}
