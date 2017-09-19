package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.HashMap;

public class EntitySpawnScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // entity spawns
    //
    // @Updated 2016/09/23
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    //
    // @Triggers when an entity spawns in the world (non players).
    //
    // @Context
    // entity (EntityTag) returns the entity that is attempting to spawn.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntitySpawns";
    }

    @Override
    public boolean couldMatch(ScriptEvent.ScriptEventData data) {
        return data.eventPath.startsWith("entity spawns");
    }

    @Override
    public boolean matches(ScriptEvent.ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error);
    }

    public EntityTag entity;

    public SpawnEntityEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEvent.ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("entity", entity);
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
    public void onEntiySpawns(SpawnEntityEvent evt) {
        for (Entity ent : evt.getEntities()) {
            EntitySpawnScriptEvent event = (EntitySpawnScriptEvent) clone();
            event.internal = evt;
            event.entity = new EntityTag(ent);
            event.cancelled = evt.isCancelled();
            event.run();
            evt.setCancelled(event.cancelled);
        }
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}

