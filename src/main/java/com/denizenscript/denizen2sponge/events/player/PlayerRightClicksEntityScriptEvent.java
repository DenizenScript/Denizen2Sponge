package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
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
    // @Group Player
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    // @Switch hand (TextTag) checks the hand type.
    //
    // @Triggers when a player right clicks an entity. Note that this may fire twice per triggering.
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
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error)
                && D2SpongeEventHelper.checkHandType(hand.getInternal(), data, this::error);
    }

    public PlayerTag player;

    public EntityTag entity;

    public TextTag hand;

    public InteractEntityEvent.Secondary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("entity", entity);
        defs.put("hand", hand);
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
        event.hand = new TextTag(evt.getHandType().toString());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
