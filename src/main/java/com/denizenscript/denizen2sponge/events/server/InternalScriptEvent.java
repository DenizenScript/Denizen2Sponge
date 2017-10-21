package com.denizenscript.denizen2sponge.events.server;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;

import java.util.HashMap;

public class InternalScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
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
        return D2SpongeEventHelper.checkString(event_type.getInternal(), data, this::error, "event_type");
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
        // TODO: Decipher cause system, allow cause tracking here
        /*
        Map<String, Object> causes = evt.getCause().getContext().;
        event.cause = new MapTag();
        for (Map.Entry<String, Object> tc : causes.entrySet()) {
            event.cause.getInternal().put(tc.getKey(), new TextTag(tc.getValue().toString()));
        }
        */
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
