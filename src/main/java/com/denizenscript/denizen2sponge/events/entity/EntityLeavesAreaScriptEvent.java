package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.CuboidTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;

import java.util.HashMap;

public class EntityLeavesAreaScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // entity leaves area
    //
    // @Updated 2017/10/06
    //
    // @Group Entity
    //
    // @Cancellable true
    //
    // @Triggers when an entity leaves an area.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch cuboid (CuboidTag) checks the cuboid that was left.
    //
    // @Context
    // entity (EntityTag) returns the entity that left the area.
    // to_position (LocationTag) returns the position the entity moved to.
    // from_position (LocationTag) returns the position the entity moved from.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "EntityLeavesArea";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity leaves area");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        Boolean fromArea = false;
        if (data.switches.containsKey("cuboid")) {
            for (AbstractTagObject ato : ListTag.getFor(this::error, data.switches.get("cuboid")).getInternal()) {
                CuboidTag cu = CuboidTag.getFor(this::error, ato);
                if (cu.contains(fromPosition.getInternal()) && !cu.contains(toPosition.getInternal())) {
                    fromArea = true;
                }
            }
        }
        /*else if (data.switches.containsKey("ellipsoid")) {
            for (AbstractTagObject ato : ListTag.getFor(this::error, data.switches.get("ellipsoid")).getInternal()) {
                EllipsoidTag el = EllipsoidTag.getFor(this::error, ato);
                if (el.contains(fromPosition.getInternal()) && !el.contains(toPosition.getInternal())) {
                    fromArea = true;
                }
            }
        }*/
        return fromArea && D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error);
    }

    public EntityTag entity;

    public LocationTag toPosition;

    public LocationTag fromPosition;

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
        defs.put("from_position", fromPosition);
        return defs;
    }

    @Listener
    public void onEntityLeavesArea(MoveEntityEvent evt) {
        EntityLeavesAreaScriptEvent event = (EntityLeavesAreaScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag(evt.getTargetEntity());
        event.toPosition = new LocationTag(evt.getToTransform().getLocation());
        event.fromPosition = new LocationTag(evt.getFromTransform().getLocation());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
