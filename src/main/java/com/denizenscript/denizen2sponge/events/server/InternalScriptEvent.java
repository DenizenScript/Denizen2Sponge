package com.denizenscript.denizen2sponge.events.server;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class InternalScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // internal event
    //
    // @Updated 2016/12/11
    //
    // @Cancellable true
    //
    // @Group Server
    //
    // @Note Cancel-ability depends on internal event.
    //
    // @Triggers when any internal event occurs.
    //
    // @Switch event_type (TextTag) checks the event type.
    //
    // @Warning This event depends on the internal system (Sponge) being consistent and trustworthy. Prefer D2-standard events over this!
    //
    // @Context
    // event_type (TextTag) returns the internal name of the event type.
    // cause (MapTag) returns a simple Text:TextTag map of all named causes in the event.
    //
    // @Determinations
    // None
    // -->

    @Override
    public String getName() {
        return "InternalEvent";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("internal event");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        if (data.switches.containsKey("type")) {
            if (!data.switches.get("event_type").equalsIgnoreCase(event_type.getInternal())) {
                return false;
            }
        }
        return true;
    }

    public TextTag event_type;

    public MapTag cause;

    public Event internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("event_type", event_type);
        defs.put("cause", cause);
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
    public void onInternalEvent(Event evt) {
        InternalScriptEvent event = (InternalScriptEvent) clone();
        event.internal = evt;
        event.event_type = new TextTag(evt.getClass().getTypeName());
        if (evt instanceof Cancellable) {
            event.cancelled = ((Cancellable) evt).isCancelled();
        }
        else {
            event.cancelled = false;
        }
        Map<String, Object> causes = evt.getCause().getNamedCauses();
        event.cause = new MapTag();
        for (Map.Entry<String, Object> tc : causes.entrySet()) {
            event.cause.getInternal().put(tc.getKey(), new TextTag(tc.getValue().toString()));
        }
        event.run();
        if (evt instanceof Cancellable) {
            ((Cancellable) evt).setCancelled(event.cancelled);
        }
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
