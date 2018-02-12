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
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class ProjectileLaunchedScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.4.0
    // @Events
    // projectile launched
    //
    // @Updated 2018/02/12
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when a projectile is launched.
    //
    // @Warning This event does not trigger in Sponge during last testing.
    //
    // @Switch entity_type (EntityTypeTag) checks the projectile entity type.
    // @Switch shooter_type (EntityTypeTag) checks the shooter entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entity (EntityTag) returns the projectile entity that was launched.
    // shooter (EntityTag) returns the shooter entity that launched the projectile.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "ProjectileLaunched";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("projectile launched");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        Entity shooterInternal = shooter.getInternal();
        World world = shooterInternal.getWorld();
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error, "entity_type")
                && D2SpongeEventHelper.checkEntityType(shooterInternal.getType(), data, this::error, "shooter_type")
                && D2SpongeEventHelper.checkWorld(world, data, this::error) && D2SpongeEventHelper.checkCuboid(
                        new LocationTag(shooterInternal.getLocation()).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                world.getWeather().getId()), data, this::error);
    }

    public EntityTag entity;

    public EntityTag shooter;

    public LaunchProjectileEvent internal;

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
        defs.put("shooter", shooter);
        return defs;
    }

    @Listener
    public void onProjectileLaunched(LaunchProjectileEvent evt) {
        ProjectileLaunchedScriptEvent event = (ProjectileLaunchedScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag(evt.getTargetEntity());
        event.shooter = new EntityTag((Entity) evt.getSource());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
