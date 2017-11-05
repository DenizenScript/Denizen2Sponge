package com.denizenscript.denizen2sponge.events.entity;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class EntityDiesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // entity dies
    //
    // @Updated 2017/10/14
    //
    // @Group Entity
    //
    // @Cancellable false
    //
    // @Triggers when an entity dies.
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // entity (EntityTag) returns the entity that died.
    // message (FormattedTextTag) returns the message that will be broadcast to the server.
    //
    // @Determinations
    // message (FormattedTextTag) to set the message displayed when the entity dies.
    // cancel_message (BooleanTag) to set whether the death message is cancelled.
    // -->

    @Override
    public String getName() {
        return "EntityDies";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("entity dies");
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

    public FormattedTextTag message;

    public DestructEntityEvent.Death internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("entity", entity);
        defs.put("message", message);
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
    public void onEntityDies(DestructEntityEvent.Death evt) {
        EntityDiesScriptEvent event = (EntityDiesScriptEvent) clone();
        event.internal = evt;
        event.entity = new EntityTag(evt.getTargetEntity());
        event.message = new FormattedTextTag(evt.getMessage());
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("message")) {
            FormattedTextTag ftt = FormattedTextTag.getFor(this::error, value);
            message = ftt;
            internal.setMessage(ftt.getInternal());
        }
        else if (determination.equals("cancel_message")) {
            // TODO: Context for this?
            BooleanTag bt = BooleanTag.getFor(this::error, value);
            internal.setMessageCancelled(bt.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
