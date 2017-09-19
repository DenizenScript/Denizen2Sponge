package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DeleteWorldCommand extends AbstractCommand {

    // <--[command]
    // @Name deleteworld
    // @Arguments <world name>
    // @Short deletes an existing world.
    // @Updated 2017/05/16
    // @Group World
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Deletes an existing world, unloading it first if it's loaded.
    // @Example
    // # Deletes the world 'Games', making sure it's unloaded.
    // - deleteworld Games
    // -->

    @Override
    public String getName() {
        return "deleteworld";
    }

    @Override
    public String getArguments() {
        return "<world name>";
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
        String worldName = entry.getArgumentObject(queue, 0).toString();
        Optional<World> world = Sponge.getServer().getWorld(worldName);
        if (world.isPresent()) {
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good +
                        "' is loaded, attempting to unload it before deletion...");
            }
            Boolean unloaded = Sponge.getServer().unloadWorld(world.get());
            if (!unloaded) {
                queue.handleError(entry, "World unloading failed!");
                return;
            }
            if (queue.shouldShowGood()) {
                queue.outGood("Unloaded world '" + ColorSet.emphasis + worldName + ColorSet.good + "'!");
            }
        }
        Optional<WorldProperties> properties = Sponge.getServer().getWorldProperties(worldName);
        if (queue.shouldShowGood()) {
            queue.outGood("Attempting to delete world '" + ColorSet.emphasis + worldName + ColorSet.good + "'...");
        }
        if (!properties.isPresent()) {
            queue.handleError(entry, "World '" + worldName + "' does not exist!");
            return;
        }
        CompletableFuture<Boolean> deleted = Sponge.getServer().deleteWorld(properties.get());
        try {
            if (!deleted.get()) {
                queue.handleError(entry, "World deletion failed!");
                return;
            }
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good + "' was deleted successfully!");
            }
        }
        catch (InterruptedException | ExecutionException e) {
            queue.handleError(entry, "World deletion failed!");
            Denizen2Core.getImplementation().outputException(e);
        }
    }
}
