package com.denizenscript.denizen2sponge.events.server;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;

import java.util.HashMap;

public class ServerStopsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // server stops
    //
    // @Updated 2017/03/22
    //
    // @Cancellable false
    //
    // @Group Server
    //
    // @Triggers when the server stops.
    //
    // @Context
    // None.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "ServerStops";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("server stops");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public GameStoppingServerEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        return super.getDefinitions(data);
    }

    @Override
    public void enable() {
        Sponge.getEventManager().registerListeners(Denizen2Sponge.instance, this);
    }

    @Override
    public void disable() {
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Listener(order = Order.EARLY)
    public void onServerStop(GameStoppingServerEvent evt) {
        ServerStopsScriptEvent event = (ServerStopsScriptEvent) clone();
        event.internal = evt;
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
