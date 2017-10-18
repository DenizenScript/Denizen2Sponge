package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.world.weather.Weather;

import java.util.HashMap;
import java.util.Optional;

public class WeatherChangesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // weather changes
    //
    // @Updated 2017/10/16
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when the weather changes in a world.
    //
    // @Warning This event's determinations do not work in Sponge during last testing.
    //
    // @Switch new_weather (TextTag) checks the new weather.
    // @Switch old_weather (TextTag) checks the old weather.
    // @Switch world (WorldTag) checks the world.
    //
    // @Context
    // world (WorldTag) returns the world.
    // duration (DurationTag) returns how long the new weather will last.
    // new_weather (TextTag) returns the new weather.
    // old_weather (TextTag) returns the old weather.
    //
    // @Determinations
    // duration (DurationTag) sets how long the new weather will last.
    // new_weather (TextTag) sets the new weather.
    // -->

    @Override
    public String getName() {
        return "WeatherChanges";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("weather changes");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWeather(new_weather.getInternal(), data, this::error, "new_weather")
                && D2SpongeEventHelper.checkWeather(old_weather.getInternal(), data, this::error, "old_weather")
                && D2SpongeEventHelper.checkWorld(world.getInternal(), data, this::error);
    }

    public WorldTag world;

    public DurationTag duration;

    public TextTag new_weather;

    public TextTag old_weather;

    public ChangeWorldWeatherEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("world", world);
        defs.put("duration", duration);
        defs.put("new_weather", new_weather);
        defs.put("old_weather", old_weather);
        return defs;
    }

    @Override
    public void enable() {
        Sponge.getEventManager().registerListeners(Denizen2Sponge.instance, this);
    }

    @Override
    public void disable() {
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Listener
    public void onWeatherChanges(ChangeWorldWeatherEvent evt) {
        WeatherChangesScriptEvent event = (WeatherChangesScriptEvent) clone();
        event.internal = evt;
        event.world = new WorldTag(evt.getTargetWorld());
        event.duration = new DurationTag(evt.getDuration() / 20.0);
        event.new_weather = new TextTag(evt.getWeather().getId());
        event.old_weather = new TextTag(evt.getInitialWeather().getId());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("duration")) {
            DurationTag dt = DurationTag.getFor(this::error, value);
            duration = dt;
            internal.setDuration((int) (dt.getInternal() * 20));
        }
        else if (determination.equals("new_weather")) {
            TextTag tt = TextTag.getFor(this::error, value);
            Optional<Weather> type = Sponge.getRegistry().getType(Weather.class, tt.getInternal());
            if (!type.isPresent()) {
                this.error("Invalid weather type: '" + tt.debug() + "'!");
                return;
            }
            new_weather = new TextTag(type.get().getId());
            internal.setWeather(type.get());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}

