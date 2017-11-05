package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class EntityKilledScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // entity killed
    //
    // @Updated 2017/10/14
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity is killed.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
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
        if (!evt.willCauseDeath()) {
            return;
        }
        EntityKilledScriptEvent event = (EntityKilledScriptEvent) clone();
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
