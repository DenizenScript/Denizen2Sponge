package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class EntityMovesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // entity moves
    //
    // @Updated 2016/10/25
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity moves.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entity (EntityTag) returns the entity that moved.
    // to_position (LocationTag) returns the position the entity moved to.
    // to_rotation (LocationTag) returns the rotation the entity moved to.
    // from_position (LocationTag) returns the position the entity moved from.
    // from_rotation (LocationTag) returns the rotation the entity moved from.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntityMoves";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity moves");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        Entity ent = entity.getInternal();
        Location<World> loc = ent.getLocation();
        World world = loc.getExtent();
        return D2SpongeEventHelper.checkEntityType(ent.getType(), data, this::error)
                && D2SpongeEventHelper.checkWorld(world, data, this::error)
                && D2SpongeEventHelper.checkCuboid((new LocationTag(loc)).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        world.getWeather().getId()), data, this::error);
    }

    public EntityTag entity;

    public LocationTag fromPosition;

    public LocationTag fromRotation;

    public LocationTag toPosition;

    public LocationTag toRotation;

    public MoveEntityEvent internal;

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
        defs.put("to_position", toPosition);
        defs.put("to_rotation", toRotation);
        defs.put("from_position", fromPosition);
        defs.put("from_rotation", fromRotation);
        return defs;
    }

    @Listener
    public void onEntityMoves(MoveEntityEvent evt) {
        EntityMovesScriptEvent event = (EntityMovesScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag(evt.getTargetEntity());
        event.toPosition = new LocationTag(evt.getToTransform().getLocation());
        event.toRotation = new LocationTag(evt.getToTransform().getRotation());
        event.fromPosition = new LocationTag(evt.getFromTransform().getLocation());
        event.fromRotation = new LocationTag(evt.getFromTransform().getRotation());
        event.cancelled = evt.isCancelled();
        // TODO: Cause viewing
        event.run();
        // TODO: Set To Transform determinations.
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
