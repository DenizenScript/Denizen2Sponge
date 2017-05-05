package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

public class PlayerLeftClicksEntityScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player left clicks entity
    //
    // @Updated 2017/04/05
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Switch type (EntityTypeTag) checks the entity type.
    //
    // @Triggers when a player left clicks an entity.
    //
    // @Context
    // player (PlayerTag) returns the player that did the left clicking.
    // entity (EntityTag) returns the entity that was left clicked.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerLeftClicksEntity";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player left clicks entity");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkEntityType(entity.getInternal().getType(), data, this::error)
                && D2SpongeEventHelper.checkItem(new ItemTag(player.getInternal()
                .getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.of(ItemTypes.NONE, 1))), data, this::error);
    }

    public PlayerTag player;

    public EntityTag entity;

    public InteractEntityEvent.Primary internal;

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
    public void onLeftClickEntity(InteractEntityEvent.Primary evt, @Root Player player) {
        PlayerLeftClicksEntityScriptEvent event = (PlayerLeftClicksEntityScriptEvent) clone();
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
