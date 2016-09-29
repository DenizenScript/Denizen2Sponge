package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class PlayerRightClicksEntityScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player right clicks entity
    //
    // @Updated 2016/09/28
    //
    // @Cancellable true
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    //
    // @Triggers when a player right clicks an entity.
    //
    // @Context
    // player (PlayerTag) returns the player that did the right clicking.
    // entity (EntityTag) returns the entity that was right clicked.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerRightClicksEntity";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player right clicks entity");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        if (data.switches.containsKey("type")) {
            if (!EntityTypeTag.getFor(this::error, data.switches.get("type"))
                    .getInternal().equals(entity.getInternal().getType())) {
                return false;
            }
        }
        return true;
    }

    public PlayerTag player;

    public EntityTag entity;

    public InteractEntityEvent.Secondary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("entity", entity);
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
    public void onRightClickEntity(InteractEntityEvent.Secondary evt, @Root Player player) {
        PlayerRightClicksEntityScriptEvent event = (PlayerRightClicksEntityScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.entity = new EntityTag(evt.getTargetEntity());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
