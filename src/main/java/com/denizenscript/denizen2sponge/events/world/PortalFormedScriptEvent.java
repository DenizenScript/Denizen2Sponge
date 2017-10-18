package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ConstructPortalEvent;

import java.util.HashMap;

public class PortalFormedScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // portal formed
    //
    // @Updated 2017/10/03
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when a portal is created, usually after a block change.
    //
    // @Warning This event does not trigger in Sponge during last testing.
    //
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // location (LocationTag) returns the location of the created portal.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PortalFormed";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("portal formed");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(location.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        location.getInternal().world.getWeather().getId()), data, this::error);
    }

    public LocationTag location;

    public ConstructPortalEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("location", location);
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
    public void onPortalFormed(ConstructPortalEvent evt) {
        PortalFormedScriptEvent event = (PortalFormedScriptEvent) clone();
        event.internal = evt;
        event.location = new LocationTag(evt.getPortalLocation());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}

