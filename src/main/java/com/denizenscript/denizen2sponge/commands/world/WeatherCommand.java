package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.weather.Weather;

import java.util.Optional;

public class WeatherCommand extends AbstractCommand {

    // <--[command]
    // @Name weather
    // @Arguments <world> clear/rain/thunder_storm [duration]
    // @Short Sets the weather of the world.
    // @Updated 2017/04/03
    // @Group World
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Sets the weather of the world. Optionally specify a duration.
    // @Example
    // # Changes the weather in the player's world to 'rain' for 5 minutes.
    // - weather <player.location.world> rain 5m
    // -->

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getArguments() {
        return "<world> clear/rain/thunder_storm [duration]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        WorldTag world = WorldTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String weather = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 1).toString());
        Optional<Weather> type = Sponge.getRegistry().getType(Weather.class, weather);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid weather type: '" + weather + "'!");
            return;
        }
        if (entry.arguments.size() > 2) {
            DurationTag duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            world.getInternal().setWeather(type.get(), (long) (duration.getInternal() * 20));
        }
        else {
            world.getInternal().setWeather(type.get());
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Changed weather to " + ColorSet.emphasis + weather + ColorSet.good
                    + " in world: " + ColorSet.emphasis + world.debug() + ColorSet.good + "!");
        }
    }
}
