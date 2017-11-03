package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import java.util.HashMap;

public class EntityCollidesWithEntityScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.2
    // @Events
    // entity collides with entity
    //
    // @Updated 2017/11/03
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity collides with another entity. Note: this event may fire very rapidly.
    //
    // @Switch type (EntityTypeTag) checks the first entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entities (EntityTag) returns the entities that collided.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntityCollidesWithEntity";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity collides with entity");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(((EntityTag) entities.getInternal().get(0)).getInternal().getType(), data, this::error, "type")
                && D2SpongeEventHelper.checkWorld(((EntityTag) entities.getInternal().get(0)).getInternal().getLocation().getExtent(), data, this::error)
                && D2SpongeEventHelper.checkCuboid(new LocationTag(((EntityTag) entities.getInternal().get(0)).getInternal().getLocation()).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        ((EntityTag) entities.getInternal().get(0)).getInternal().getLocation().getExtent().getWeather().getId()), data, this::error);
    }

    public ListTag entities;

    public CollideEntityEvent internal;

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
        defs.put("entities", entities);
        return defs;
    }

    @Listener
    public void onEntityCollidesWithEntity(CollideEntityEvent evt) {
        EntityCollidesWithEntityScriptEvent event = (EntityCollidesWithEntityScriptEvent) clone();
        event.internal = evt;
        ListTag list = new ListTag();
        for (Entity ent : evt.getEntities()) {
            list.getInternal().add(new EntityTag(ent));
        }
        event.entities = list;
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
