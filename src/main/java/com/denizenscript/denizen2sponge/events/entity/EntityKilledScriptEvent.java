package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.util.Tuple;

import java.util.HashMap;
import java.util.function.Function;

public class EntityKilledScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // entity killed
    //
    // @Updated 2017/03/24
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity is killed.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    //
    // @Context
    // entity (EntityTag) returns the entity that was killed.
    // damage (NumberTag) returns the amount of damage applied.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntityKilled";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity killed");
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
    public void onEntityKilled(DamageEntityEvent evt) {
        EntityKilledScriptEvent event = (EntityKilledScriptEvent) clone();
        event.internal = evt;
        if (!evt.willCauseDeath()) {
            return;
        }
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
