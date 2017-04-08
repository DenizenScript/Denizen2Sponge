package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.HashMap;

public class PlayerLeftClicksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player left clicks block
    //
    // @Updated 2017/04/05
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player left clicks a block.
    //
    // @Switch type (BlockTypeTag) checks the entity type.
    //
    // @Context
    // player (PlayerTag) returns the player that did the left clicking.
    // location (LocationTag) returns the location of the block that was left clicked.
    // precise_location (LocationTag) returns the exact point that was left clicked in the world.
    // intersection_point (LocationTag) returns the exact point of intersection relative to the block (When available).
    // impact_normal (LocationTag) returns the normal vector from the side of the block that was left clicked.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerLeftClicksBlock";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player left clicks block");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkBlockType(location.getInternal().toLocation().getBlock().getType(), data, this::error);
    }

    public PlayerTag player;

    public LocationTag location;

    public LocationTag precise_location;

    public LocationTag intersection_point;

    public LocationTag impact_normal;

    public InteractBlockEvent.Primary internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("location", location);
        defs.put("precise_location", precise_location);
        defs.put("intersection_point", intersection_point);
        defs.put("impact_normal", impact_normal);
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
    public void onLeftClickBlock(InteractBlockEvent.Primary evt, @Root Player player) {
        PlayerLeftClicksBlockScriptEvent event = (PlayerLeftClicksBlockScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        if (evt.getTargetBlock().getLocation().isPresent()) {
            event.location = new LocationTag(evt.getTargetBlock().getLocation().get());
            BlockRayHit<World> brh = BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).end().get();
            event.precise_location = new LocationTag(brh.getPosition());
            event.precise_location.getInternal().world = event.location.getInternal().world;
            event.intersection_point = new LocationTag(brh.getPosition().sub(brh.getBlockPosition().toDouble()));
            // event.precise_location = new LocationTag(evt.getInteractionPoint().get().add(evt.getTargetBlock().getPosition().toDouble()));
            // event.precise_location.getInternal().world = event.location.getInternal().world;
            // event.intersection_point = new LocationTag(evt.getInteractionPoint().get());
            // TODO: Switch back to these ^ once Sponge fixes the Interaction Point.
            event.impact_normal = new LocationTag(evt.getTargetSide().asOffset());
        }
        else {
            BlockRayHit<World> brh = BlockRay.from(player).distanceLimit(Utilities.getHandReach(player)).build().end().get();
            event.location = new LocationTag(brh.getLocation());
            event.precise_location = new LocationTag(brh.getPosition());
            event.precise_location.getInternal().world = event.location.getInternal().world;
            event.intersection_point = new LocationTag(brh.getPosition().sub(brh.getBlockPosition().toDouble()));
            event.impact_normal = new LocationTag(0, 0, 0);
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
