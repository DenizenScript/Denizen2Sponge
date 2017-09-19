package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class LoadWorldCommand extends AbstractCommand {

    // <--[command]
    // @Name loadworld
    // @Arguments <world name> <template>
    // @Short loads an existing world, or creates a new one.
    // @Updated 2017/05/16
    // @Group World
    // @Minimum 1
    // @Maximum 2
    // @Named difficulty (TextTag) Sets the difficulty of the world.
    // @Named gamemode (TextTag) Sets default gamemode of the world.
    // @Named modifiers (ListTag) Sets the generator modifiers for creating the world.
    // @Named generator (TextTag) Sets the generator type for creating the world.
    // @Named commands_allowed (BooleanTag) Sets whether commands are allowed in the world.
    // @Named enabled (BooleanTag) Sets whether the world will automatically load if an entity enters it.
    // @Named generate_spawn_on_load (BooleanTag) Sets whether the spawn will be generated automatically on world load.
    // @Named hardcore (BooleanTag) Sets whether the world is hardcore.
    // @Named keep_spawn_loaded (BooleanTag) Sets the world keeps the spawn loaded.
    // @Named load_on_startup (BooleanTag) Sets whether the world is loaded automatically when the server starts.
    // @Named map_features (BooleanTag) Sets whether map features (such as villages) will be generated in the world.
    // @Named pvp (BooleanTag) Sets whether pvp is enabled by default in the world.
    // @Named seed (IntegerTag) Sets the seed of the world.
    // @Named spawn (LocationTag) Sets the default spawn location of the world.
    // @Description
    // Loads an existing world, or creates it (and loads it) from a template if it doesn't exist yet.
    // Any world property specified will overwrite the default values of the template.
    // @Example
    // # Loads the world 'Games', or creates it from the template 'overworld' if it doesn't exist yet.
    // - loadworld Games overworld
    // -->

    @Override
    public String getName() {
        return "loadworld";
    }

    @Override
    public String getArguments() {
        return "<world name> <template>";
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
        String worldName = entry.getArgumentObject(queue, 0).toString();
        Optional<World> world = Sponge.getServer().getWorld(worldName);
        if (world.isPresent()) {
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good + "' is already loaded!");
            }
            return;
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Attempting to load world '" + ColorSet.emphasis + worldName + ColorSet.good + "'...");
        }
        Optional<WorldProperties> properties = Sponge.getServer().getWorldProperties(worldName);
        if (properties.isPresent()) {
            Sponge.getServer().loadWorld(properties.get());
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good + "' was loaded successfully!");
            }
            return;
        }
        if (queue.shouldShowGood()) {
            queue.outGood("World properties for world '" + ColorSet.emphasis + worldName + ColorSet.good +
                    "' are missing, creating the world from the specified template instead...");
        }
        if (entry.arguments.size() < 2) {
            queue.handleError(entry, "A template is needed in order to create a new world!");
            return;
        }
        String templateName = entry.getArgumentObject(queue, 1).toString();
        Optional<WorldArchetype> template = Sponge.getRegistry().getType(WorldArchetype.class, templateName);
        if (!template.isPresent()) {
            queue.handleError(entry, "Invalid world template: '" + templateName + "'!");
            return;
        }
        Difficulty difficulty = null;
        if (entry.namedArgs.containsKey("difficulty")) {
            String difficultyName = entry.getNamedArgumentObject(queue, "difficulty").toString();
            Optional<Difficulty> difficultyOpt = Sponge.getRegistry().getType(Difficulty.class, difficultyName);
            if (!difficultyOpt.isPresent()) {
                queue.handleError(entry, "Invalid difficulty: '" + difficultyName + "'!");
                return;
            }
            difficulty = difficultyOpt.get();
        }
        GameMode gameMode = null;
        if (entry.namedArgs.containsKey("gamemode")) {
            String gameModeName = entry.getNamedArgumentObject(queue, "gamemode").toString();
            Optional<GameMode> gameModeOpt = Sponge.getRegistry().getType(GameMode.class, gameModeName);
            if (!gameModeOpt.isPresent()) {
                queue.handleError(entry, "Invalid gamemode: '" + gameModeName + "'!");
                return;
            }
            gameMode = gameModeOpt.get();
        }
        Collection<WorldGeneratorModifier> modifiers = new ArrayList<>();
        if (entry.namedArgs.containsKey("modifiers")) {
            List<AbstractTagObject> modifiersList = ListTag.getFor(queue.error,
                    entry.getNamedArgumentObject(queue, "gamemode")).getInternal();
            for (AbstractTagObject ato : modifiersList) {
                String modifierName = ato.toString();
                Optional<WorldGeneratorModifier> modifierOpt
                        = Sponge.getRegistry().getType(WorldGeneratorModifier.class, modifierName);
                if (!modifierOpt.isPresent()) {
                    queue.handleError(entry, "Invalid generator modifier: '" + modifierName + "'!");
                    return;
                }
                modifiers.add(modifierOpt.get());
            }
        }
        GeneratorType generator = null;
        if (entry.namedArgs.containsKey("generator")) {
            String generatorName = entry.getNamedArgumentObject(queue, "generator").toString();
            Optional<GeneratorType> generatorOpt = Sponge.getRegistry().getType(GeneratorType.class, generatorName);
            if (!generatorOpt.isPresent()) {
                queue.handleError(entry, "Invalid generator type: '" + generatorName + "'!");
                return;
            }
            generator = generatorOpt.get();
        }
        try {
            WorldProperties newProperties = Sponge.getServer().createWorldProperties(worldName, template.get());
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good + "' created from template '" +
                        ColorSet.emphasis + template.get().getId() + ColorSet.good + "', applying properties....");
            }
            if (difficulty != null) {
                newProperties.setDifficulty(difficulty);
            }
            if (gameMode != null) {
                newProperties.setGameMode(gameMode);
            }
            if (!modifiers.isEmpty()) {
                newProperties.setGeneratorModifiers(modifiers);
            }
            if (generator != null) {
                newProperties.setGeneratorType(generator);
            }
            if (entry.namedArgs.containsKey("commands_allowed")) {
                newProperties.setCommandsAllowed(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "commands_allowed")).getInternal());
            }
            if (entry.namedArgs.containsKey("enabled")) {
                newProperties.setEnabled(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "enabled")).getInternal());
            }
            if (entry.namedArgs.containsKey("generate_spawn_on_load")) {
                newProperties.setGenerateSpawnOnLoad(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "generate_spawn_on_load")).getInternal());
            }
            if (entry.namedArgs.containsKey("hardcore")) {
                newProperties.setHardcore(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "hardcore")).getInternal());
            }
            if (entry.namedArgs.containsKey("keep_spawn_loaded")) {
                newProperties.setKeepSpawnLoaded(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "keep_spawn_loaded")).getInternal());
            }
            if (entry.namedArgs.containsKey("load_on_startup")) {
                newProperties.setLoadOnStartup(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "load_on_startup")).getInternal());
            }
            if (entry.namedArgs.containsKey("map_features")) {
                newProperties.setMapFeaturesEnabled(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "map_features")).getInternal());
            }
            if (entry.namedArgs.containsKey("pvp")) {
                newProperties.setPVPEnabled(BooleanTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "pvp")).getInternal());
            }
            if (entry.namedArgs.containsKey("seed")) {
                newProperties.setSeed(IntegerTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "seed")).getInternal());
            }
            if (entry.namedArgs.containsKey("spawn")) {
                newProperties.setSpawnPosition(LocationTag.getFor(queue.error,
                        entry.getNamedArgumentObject(queue, "spawn")).getInternal().toVector3i());
            }
            Sponge.getServer().loadWorld(newProperties);
            if (queue.shouldShowGood()) {
                queue.outGood("World '" + ColorSet.emphasis + worldName + ColorSet.good
                        + "' loaded successfully after applying properties!");
            }
        }
        catch (IOException e) {
            queue.handleError(entry, "World creation failed!");
            Denizen2Core.getImplementation().outputException(e);
        }
    }
}
