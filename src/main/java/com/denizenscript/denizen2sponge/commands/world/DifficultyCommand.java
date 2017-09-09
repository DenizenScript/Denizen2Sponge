package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class DifficultyCommand extends AbstractCommand {

    // <--[command]
    // @Name difficulty
    // @Arguments <world> <difficulty level>
    // @Short sets the difficulty of a world.
    // @Updated 2017/09/07
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Sets the difficulty of a world. The standard set of difficulty levels
    // is 'peaceful', 'easy', 'normal', and 'hard'.
    // @Example
    // # Sets the difficulty to 'hard' in world 'Survival'.
    // - difficulty Survival hard
    // -->

    @Override
    public String getName() {
        return "difficulty";
    }

    @Override
    public String getArguments() {
        return "<world> <difficulty level>";
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
        AbstractTagObject world = entry.getArgumentObject(queue, 0);
        WorldProperties properties;
        if (world instanceof WorldTag) {
            properties = ((WorldTag) world).getInternal().getProperties();
        }
        else {
            Optional<WorldProperties> opt = Sponge.getServer().getWorldProperties(world.toString());
            if (!opt.isPresent()) {
                queue.handleError(entry, "Invalid world specified!");
                return;
            }
            properties = opt.get();
        }
        String difficulty = entry.getArgumentObject(queue, 1).toString();
        Optional<Difficulty> type = Sponge.getRegistry().getType(Difficulty.class, difficulty);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid difficulty level: '" + difficulty + "'!");
            return;
        }
        properties.setDifficulty(type.get());
        if (queue.shouldShowGood()) {
            queue.outGood("Set difficulty of world '" + ColorSet.emphasis + properties.getWorldName()
                    + ColorSet.good + "' to: " + ColorSet.emphasis + type.get().getName() + ColorSet.good + "!");
        }
    }
}
