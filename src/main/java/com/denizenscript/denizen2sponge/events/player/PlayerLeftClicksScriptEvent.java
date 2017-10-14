package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

public class PlayerLeftClicksScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player left clicks
    //
    // @Updated 2017/05/05
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player left clicks.
    //
    // @Switch with_item (ItemTag) checks the item in hand.
    //
    // @Context
    // player (PlayerTag) returns the player that did the left clicking.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerLeftClicks";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.equals("player left clicks");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkItem(new ItemTag(player.getInternal().
                getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.empty())), data, this::error);
    }

    public PlayerTag player;

    public InteractItemEvent.Primary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
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
    public void onLeftClick(InteractItemEvent.Primary evt, @Root Player player) {
        PlayerLeftClicksScriptEvent event = (PlayerLeftClicksScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
