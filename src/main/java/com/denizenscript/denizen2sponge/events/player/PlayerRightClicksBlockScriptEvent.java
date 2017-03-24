package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class PlayerRightClicksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player right clicks block
    //
    // @Updated 2016/09/28
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player right clicks a block. Note that this may fire twice per triggering.
    //
    // @Context
    // player (PlayerTag) returns the player that did the right clicking.
    // location (LocationTag) returns the location of the block that was right clicked.
    // intersection (LocationTag) returns the exact point of intersection on the block where it was clicked (When available).
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerRightClicksBlock";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player right clicks block");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkBlockType(location.getInternal().toLocation().getBlock().getType(), data, this::error);
    }

    public PlayerTag player;

    public LocationTag location;

    public LocationTag intersection;

    public InteractBlockEvent.Secondary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("location", location);
        defs.put("intersection", intersection);
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
    public void onRightClickBlock(InteractBlockEvent.Secondary evt, @Root Player player) {
        PlayerRightClicksBlockScriptEvent event = (PlayerRightClicksBlockScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.location = new LocationTag(evt.getTargetBlock().getLocation().get());
        if (evt.getInteractionPoint().isPresent()) {
            event.intersection = new LocationTag(evt.getInteractionPoint().get());
            event.intersection.getInternal().world = event.location.getInternal().world;
        }
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
