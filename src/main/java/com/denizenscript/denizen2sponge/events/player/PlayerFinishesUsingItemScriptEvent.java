package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

import java.util.HashMap;

public class PlayerFinishesUsingItemScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player finishes using item
    //
    // @Updated 2017/10/14
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player finishes using an item and it's consumed.
    //
    // @Switch item (ItemTag) checks the item used.
    //
    // @Context
    // player (PlayerTag) returns the player that finished using the item.
    // item (ItemTag) returns the used item.
    // duration (DurationTag) returns the maximum duration the item will be used for.
    //
    // @Determinations
    // duration (DurationTag) sets the maximum duration the item will be used for.
    // -->

    @Override
    public String getName() {
        return "PlayerFinishesUsingItem";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player finishes using item");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkItem(item, data, this::error, "item");
    }

    public PlayerTag player;

    public ItemTag item;

    public DurationTag duration;

    public UseItemStackEvent.Finish internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("item", item);
        defs.put("duration", duration);
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
    public void onPlayerFinishesUsingItem(UseItemStackEvent.Finish evt, @Root Player player) {
        PlayerFinishesUsingItemScriptEvent event = (PlayerFinishesUsingItemScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.item = new ItemTag(evt.getItemStackInUse().createStack());
        event.duration = new DurationTag(evt.getRemainingDuration() / 20.0);
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("duration")) {
            DurationTag dt = DurationTag.getFor(this::error, value);
            duration = dt;
            internal.setRemainingDuration((int) (dt.getInternal() * 20));
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
