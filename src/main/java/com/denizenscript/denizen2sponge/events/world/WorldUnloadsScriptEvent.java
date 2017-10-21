package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.UnloadWorldEvent;

import java.util.HashMap;

public class WorldUnloadsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // world unloads
    //
    // @Updated 2017/03/24
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when a world is unloaded.
    //
    // @Switch world (WorldTag) checks the world.
    //
    // @Context
    // world (WorldTag) returns the world that was unloaded.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "WorldUnloads";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("world unloads");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(world.getInternal(), data, this::error);

    }

    public WorldTag world;

    public UnloadWorldEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("world", world);
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
    public void onWorldUnloads(UnloadWorldEvent evt) {
        WorldUnloadsScriptEvent event = (WorldUnloadsScriptEvent) clone();
        event.internal = evt;
        event.world = new WorldTag(evt.getTargetWorld());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}

