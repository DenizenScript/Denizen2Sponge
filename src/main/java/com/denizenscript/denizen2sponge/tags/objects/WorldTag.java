package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.GameRules;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldTag extends AbstractTagObject {

    // <--[object]
    // @Type WorldTag
    // @SubType TextTag
    // @Group Areas
    // @Description Represents a world on the server. Identified by name.
    // -->

    private World internal;

    public WorldTag(World world) {
        internal = world;
    }

    public World getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name WorldTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the world.
        // @Example "world" .name returns "world".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((WorldTag) obj).internal.getName()));
        // <--[tag]
        // @Name WorldTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the world.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((WorldTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Name WorldTag.entities[<EntityTypeTag>]
        // @Updated 2017/01/19
        // @Group Server Lists
        // @ReturnType ListTag<EntityTag>
        // @Returns a list of all entities in the world, optionally with a specific type only.
        // -->
        handlers.put("entities", (dat, obj) -> {
            ListTag list = new ListTag();
            EntityTypeTag requiredTypeTag = null;
            if (dat.hasNextModifier()) {
                requiredTypeTag = EntityTypeTag.getFor(dat.error, dat.getNextModifier());
            }
            for (Entity entity : ((WorldTag) obj).internal.getEntities()) {
                if (requiredTypeTag == null || entity.getType().equals(requiredTypeTag.getInternal())) {
                    list.getInternal().add(new EntityTag(entity));
                }
            }
            return list;
        });
        // <--[tag]
        // @Name WorldTag.dimension
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the dimension of the world. This can be nether, overworld or the_end.
        // -->
        handlers.put("dimension", (dat, obj) -> new TextTag(CoreUtilities.toLowerCase(((WorldTag) obj).internal.getDimension().getType().toString())));
        // <--[tag]
        // @Name WorldTag.players
        // @Updated 2017/04/03
        // @Group Server Lists
        // @ReturnType ListTag<PlayerTag>
        // @Returns a list of all the players in the world.
        // -->
        handlers.put("players", (dat, obj) -> {
            ListTag list = new ListTag();
            for (Player player : ((WorldTag) obj).internal.getPlayers()) {
                list.getInternal().add(new PlayerTag(player));
            }
            return list;
        });
        // <--[tag]
        // @Name WorldTag.spawn
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType LocationTag
        // @Returns the default spawn location of this world.
        // -->
        handlers.put("spawn", (dat, obj) -> new LocationTag(((WorldTag) obj).internal.getSpawnLocation()));
        // <--[tag]
        // @Name WorldTag.list_gamerules
        // @Updated 2017/05/16
        // @Group Properties
        // @ReturnType MapTag<TextTag>
        // @Returns the gamerules of the world as a MapTag.
        // -->
        handlers.put("list_gamerules", (dat, obj) -> {
            MapTag map = new MapTag();
            for (Map.Entry<String, String> entry : ((WorldTag) obj).internal.getGameRules().entrySet()) {
                if (GameRules.MinecraftToSponge.containsKey(entry.getKey())) {
                    map.getInternal().put(GameRules.MinecraftToSponge.get(entry.getKey()),
                            TextTag.getFor(dat.error, entry.getValue()));
                }
                else {
                    map.getInternal().put(entry.getKey(), TextTag.getFor(dat.error, entry.getValue()));
                }
            }
            return map;
        });
        // <--[tag]
        // @Name WorldTag.gamerule[<TextTag>]
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the specified gamerule of the world. Note: rule names are case sensitive!
        // -->
        handlers.put("gamerule", (dat, obj) -> {
            String gamerule = dat.getNextModifier().toString();
            Optional<String> opt = ((WorldTag) obj).internal.getGameRule(gamerule);
            if (!opt.isPresent()) {
                dat.error.run("Gamerule '" + gamerule + "' does not exist!");
                return new NullTag();
            }
            return new TextTag(opt.get());
        });
        // <--[tag]
        // @Name WorldTag.generator
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the generator type of the world. An unmodified world returns 'default'.
        // @Example "world" .generator might return "default".
        // -->
        handlers.put("generator", (dat, obj) -> new TextTag(CoreUtilities.toLowerCase(((WorldTag) obj).internal.getProperties().getGeneratorType().getName())));
        // <--[tag]
        // @Name WorldTag.seed
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the seed of the world.
        // -->
        handlers.put("seed", (dat, obj) -> new IntegerTag(((WorldTag) obj).internal.getProperties().getSeed()));
        // <--[tag]
        // @Name WorldTag.time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the time of day of the world. This value is not necessarily within the time span of a single day.
        // -->
        handlers.put("time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getProperties().getWorldTime() * (1.0 / 20.0)));
        // <--[tag]
        // @Name WorldTag.total_time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the total time of the world.
        // -->
        handlers.put("total_time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getProperties().getTotalTime() * (1.0 / 20.0)));
        // <--[tag]
        // @Name WorldTag.difficulty
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the difficulty of the world. Difficulties include 'peaceful', 'easy', 'normal' and 'hard'.
        // @Example "world" .difficulty might return "easy".
        // -->
        handlers.put("difficulty", (dat, obj) -> new TextTag(Utilities.getIdWithoutDefaultPrefix(((WorldTag) obj).internal.getProperties().getDifficulty().getId())));
        // <--[tag]
        // @Name WorldTag.is_raining
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether it is raining in the world or not.
        // -->
        handlers.put("is_raining", (dat, obj) -> new BooleanTag(((WorldTag) obj).internal.getProperties().isRaining()));
        // <--[tag]
        // @Name WorldTag.rain_time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the remaining time before the rain state is toggled to a random value in this world.
        // -->
        handlers.put("rain_time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getProperties().getRainTime() * (1.0 / 20.0)));
        // <--[tag]
        // @Name WorldTag.is_thundering
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether it is thundering in the world or not.
        // -->
        handlers.put("is_thundering", (dat, obj) -> new BooleanTag(((WorldTag) obj).internal.getProperties().isThundering()));
        // <--[tag]
        // @Name WorldTag.thunder_time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the remaining time before the thunder state is toggled to a random value in this world.
        // -->
        handlers.put("thunder_time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getProperties().getThunderTime() * (1.0 / 20.0)));
        // <--[tag]
        // @Name WorldTag.weather
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the current weather of the world. Weathers include 'clear', 'rain' and 'thunder_storm'.
        // @Example "world" .weather might return "clear".
        // -->
        handlers.put("weather", (dat, obj) -> new TextTag(Utilities.getIdWithoutDefaultPrefix(((WorldTag) obj).internal.getWeather().getId())));
        // <--[tag]
        // @Name WorldTag.remaining_weather_time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the remaining duration of the current weather in the world.
        // -->
        handlers.put("remaining_weather_time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getRemainingDuration() * (1.0 / 20.0)));
        // <--[tag]
        // @Name WorldTag.running_weather_time
        // @Updated 2017/04/03
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the duration the current weather in the world has been running for.
        // -->
        handlers.put("running_weather_time", (dat, obj) -> new DurationTag(((WorldTag) obj).internal.getRunningDuration() * (1.0 / 20.0)));
    }

    public static WorldTag getFor(Action<String> error, String text) {
        Optional<World> optWorld = Sponge.getServer().getWorld(text);
        if (!optWorld.isPresent()) {
            error.run("Invalid WorldTag input!");
            return null;
        }
        return new WorldTag(optWorld.get());
    }

    public static WorldTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof WorldTag) ? (WorldTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString());
    }

    @Override
    public String getTagTypeName() {
        return "WorldTag";
    }

    @Override
    public String toString() {
        return internal.getName();
    }
}
