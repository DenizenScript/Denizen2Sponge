package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class EntitySpawnsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // entity spawns
    //
    // @Updated 2018/06/03
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    // @Switch cause (TextTag) checks the spawn cause.
    //
    // @Triggers when an entity spawns in the world (non players).
    // Possible spawn causes can be found at <@link explanation Spawn Causes>spawn causes<@/link>.
    //
    // @Context
    // entity (EntityTag) returns the entity that is attempting to spawn.
    // cause (TextTag) returns the type of spawn that fired this event.
    //
    // @Determinations
    // None.
    // -->

    // <--[explanation]
    // @Since 0.4.0
    // @Name Spawn Causes
    // @Group Useful Lists
    // @Description
    // A list of all default spawn causes can be found here:
    // <@link url https://jd.spongepowered.org/7.1.0-SNAPSHOT/org/spongepowered/api/event/cause/entity/spawn/SpawnTypes.html>spawn types list<@/link>
    // This information can be useful to understand the cause context in spawn events,
    // and cause named argument in spawn commands.
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
        Entity ent = entity.getInternal();
        Location<World> loc = ent.getLocation();
        World world = loc.getExtent();
        return D2SpongeEventHelper.checkEntityType(ent.getType(), data, this::error)
                && D2SpongeEventHelper.checkWorld(world, data, this::error)
                && D2SpongeEventHelper.checkCuboid((new LocationTag(loc)).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        world.getWeather().getId()), data, this::error)
                && D2SpongeEventHelper.checkCatalogType(
                        SpawnType.class, cause.toString(), data, this::error, "cause");
    }

    public EntityTag entity;

    public TextTag cause;

    public SpawnEntityEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEvent.ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("entity", entity);
        defs.put("cause", cause);
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
            EntitySpawnsScriptEvent event = (EntitySpawnsScriptEvent) clone();
            event.internal = evt;
            event.entity = new EntityTag(ent);
            event.cause = new TextTag(Utilities.getIdWithoutDefaultPrefix(
                    evt.getContext().get(EventContextKeys.SPAWN_TYPE).get().getId()));
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

