package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import java.util.HashMap;

public class ProjectileImpactsEntityScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // projectile impacts entity
    //
    // @Updated 2017/10/27
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when a projectile impacts an entity.
    //
    // @Switch type (EntityTypeTag) checks the projectile type.
    // @Switch other_type (EntityTypeTag) checks the other entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entity (EntityTag) returns the projectile entity that impacted the other entity.
    // other_entity (EntityTag) returns the entity that was impacted.
    // impact_point (LocationTag) returns the precise impact location.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "ProjectileImpactsEntity";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("projectile impacts entity");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error, "type")
                && D2SpongeEventHelper.checkEntityType(other_entity.getInternal().getType(), data, this::error, "other_type")
                && D2SpongeEventHelper.checkWorld(impact_point.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(impact_point.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        impact_point.getInternal().world.getWeather().getId()), data, this::error);
    }

    public EntityTag entity;

    public EntityTag other_entity;

    public LocationTag impact_point;

    public CollideEntityEvent.Impact internal;

    @Override
    public void enable() {
        Sponge.getEventManager().registerListeners(Denizen2Sponge.instance, this);
    }

    @Override
    public void disable() {
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("entity", entity);
        defs.put("other_entity", other_entity);
        defs.put("impact_point", impact_point);
        return defs;
    }

    @Listener
    public void onEntityImpactsEntity(CollideEntityEvent.Impact evt) {
        ProjectileImpactsEntityScriptEvent event = (ProjectileImpactsEntityScriptEvent) clone();
        event.internal = evt;
        Entity projectile = (Entity) evt.getSource();
        event.entity = new EntityTag(projectile);
        evt.getEntities().remove(projectile);
        event.other_entity = new EntityTag(evt.getEntities().iterator().next());
        event.impact_point = new LocationTag(evt.getImpactPoint());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
