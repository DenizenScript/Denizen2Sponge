package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.HashMap;

public class EntityDamagedScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // entity damaged
    //
    // @Updated 2016/09/28
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity is damaged.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    //
    // @Context
    // entity (EntityTag) returns the entity that was damaged.
    // damage (NumberTag) returns the amount of damage applied.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntityDamaged";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity damaged");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error);
    }

    public EntityTag entity;

    public NumberTag damage;

    public DamageEntityEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("entity", entity);
        defs.put("damage", damage);
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
    public void onEntityDamaged(DamageEntityEvent evt) {
        EntityDamagedScriptEvent event = (EntityDamagedScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag(evt.getTargetEntity());
        event.damage = new NumberTag(evt.getFinalDamage());
        event.cancelled = evt.isCancelled();
        // TODO: Cause viewing
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
