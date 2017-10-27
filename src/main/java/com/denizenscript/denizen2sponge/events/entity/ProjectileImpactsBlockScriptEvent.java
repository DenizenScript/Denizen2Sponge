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
import org.spongepowered.api.event.block.CollideBlockEvent;

import java.util.HashMap;

public class ProjectileImpactsBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // projectile impacts block
    //
    // @Updated 2017/10/27
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when a projectile impacts a block.
    //
    // @Switch entity_type (EntityTypeTag) checks the entity type.
    // @Switch block_type (BlockTypeTag) checks the block type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entity (EntityTag) returns the projectile entity that impacted the block.
    // location (LocationTag) returns the location of the block impacted.
    // impact_point (LocationTag) returns the precise impact location.
    // impact_normal (LocationTag) returns the impact normal of the collision.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "ProjectileImpactsBlock";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("projectile impacts block");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error, "entity_type")
                && D2SpongeEventHelper.checkBlockType(material.getInternal(), data, this::error, "block_type")
                && D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(location.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                location.getInternal().world.getWeather().getId()), data, this::error);
    }

    public EntityTag entity;

    public BlockTypeTag material;

    public LocationTag location;

    public LocationTag impact_point;

    public LocationTag impact_normal;

    public CollideBlockEvent.Impact internal;

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
        defs.put("location", location);
        defs.put("impact_point", impact_point);
        defs.put("impact_normal", impact_normal);
        return defs;
    }

    @Listener
    public void onEntityImpactsBlock(CollideBlockEvent.Impact evt) {
        ProjectileImpactsBlockScriptEvent event = (ProjectileImpactsBlockScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag((Entity) evt.getSource());
        event.material = new BlockTypeTag(evt.getTargetBlock().getType());
        event.location = new LocationTag(evt.getTargetLocation());
        event.impact_point = new LocationTag(evt.getImpactPoint());
        event.impact_normal = new LocationTag(evt.getTargetSide().asOffset());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
