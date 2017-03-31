package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class PlayerRightClicksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player right clicks block
    //
    // @Updated 2017/03/30
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player right clicks a block. Note that this may fire twice per triggering.
    //
    // @Switch type (BlockTypeTag) checks the entity type.
    // @Switch hand (TextTag) checks the hand type.
    //
    // @Context
    // player (PlayerTag) returns the player that did the right clicking.
    // location (LocationTag) returns the location of the block that was right clicked.
    // intersection (LocationTag) returns the exact point of intersection on the block where it was clicked (When available).
    // hand (TextTag) returns the hand type that triggered the event.
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
        return D2SpongeEventHelper.checkBlockType(location.getInternal().toLocation().getBlock().getType(), data, this::error)
                && D2SpongeEventHelper.checkHandType(hand.getInternal(), data, this::error);
    }

    public PlayerTag player;

    public LocationTag location;

    public LocationTag intersection;

    public TextTag hand;

    public InteractBlockEvent.Secondary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("location", location);
        defs.put("intersection", intersection);
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
    public void onRightClickBlock(InteractBlockEvent.Secondary evt, @Root Player player) {
        PlayerRightClicksBlockScriptEvent event = (PlayerRightClicksBlockScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        if (evt.getTargetBlock().getLocation().isPresent()) {
            event.location = new LocationTag(evt.getTargetBlock().getLocation().get());
            event.intersection = new LocationTag(evt.getInteractionPoint().get().add(evt.getTargetBlock().getPosition().toDouble()));
            event.intersection.getInternal().world = event.location.getInternal().world;
        }
        else {
            BlockRayHit<World> brh = BlockRay.from(player).distanceLimit(5.0).build().end().get();
            event.location = new LocationTag(brh.getLocation());
            event.intersection = new LocationTag(brh.getPosition());
            event.intersection.getInternal().world = event.location.getInternal().world;
        }
        event.hand = new TextTag(CoreUtilities.toLowerCase(evt.getHandType().toString()));
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
