package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplosionDetonatesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // explosion detonates
    //
    // @Updated 2017/10/05
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when an explosion detonates and is calculating the affected locations and entities.
    //
    // @Context
    // locations (ListTag<LocationTag>) returns the locations affected by the explosion.
    // entities (ListTag<EntityTag>) returns the entities affected by the explosion.
    // explosion_data (MapTag) returns the data associated with the explosion, such as radius or whether it should cause fire.
    //
    // @Determinations
    // locations (ListTag<LocationTag>) to set the locations affected by the explosion.
    // entities (ListTag<EntityTag>) to set the entities affected by the explosion.
    // -->

    @Override
    public String getName() {
        return "ExplosionDetonates";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("explosion detonates");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error);
    }

    public LocationTag location;

    public ListTag blocks;

    public ListTag entities;

    public MapTag explosion_data;

    public ExplosionEvent.Detonate internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("location", location);
        defs.put("blocks", blocks);
        defs.put("entities", entities);
        defs.put("explosion_data", explosion_data);
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
    public void onExplosionDetonates(ExplosionEvent.Detonate evt) {
        ExplosionDetonatesScriptEvent event = (ExplosionDetonatesScriptEvent) clone();
        event.internal = evt;
        event.location = new LocationTag(evt.getExplosion().getLocation());
        ListTag locs = new ListTag();
        for (Location<World> loc : evt.getAffectedLocations()) {
            locs.getInternal().add(new LocationTag(loc));
        }
        event.blocks = locs;
        ListTag ents = new ListTag();
        for (Entity ent : evt.getEntities()) {
            locs.getInternal().add(new EntityTag(ent));
        }
        event.entities = ents;
        Explosion exp = evt.getExplosion();
        MapTag data = new MapTag();
        data.getInternal().put("radius", new NumberTag(exp.getRadius()));
        data.getInternal().put("fire", new BooleanTag(exp.canCauseFire()));
        data.getInternal().put("break_blocks", new BooleanTag(exp.shouldBreakBlocks()));
        data.getInternal().put("damage_entities", new BooleanTag(exp.shouldDamageEntities()));
        data.getInternal().put("smoke", new BooleanTag(exp.shouldPlaySmoke()));
        event.explosion_data = data;
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("blocks")) {
            ListTag lt = ListTag.getFor(this::error, value);
            blocks = lt;
            ArrayList<Location<World>> locs = new ArrayList<>();
            for (AbstractTagObject loc : lt.getInternal()) {
                locs.add(((LocationTag) loc).getInternal().toLocation());
            }
            internal.getAffectedLocations().clear();
            internal.getAffectedLocations().addAll(locs);
        }
        else if (determination.equals("entities")) {
            ListTag lt = ListTag.getFor(this::error, value);
            entities = lt;
            ArrayList<Entity> ents = new ArrayList<>();
            for (AbstractTagObject ent : lt.getInternal()) {
                ents.add(((EntityTag) ent).getInternal());
            }
            internal.getEntities().clear();
            internal.getEntities().addAll(ents);
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}

