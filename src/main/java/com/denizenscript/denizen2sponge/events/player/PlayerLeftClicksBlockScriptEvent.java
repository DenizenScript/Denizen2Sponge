package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;

public class PlayerLeftClicksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // player left clicks block
    //
    // @Updated 2017/10/14
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player left clicks a block.
    //
    // @Switch type (BlockTypeTag) checks the block type.
    // @Switch with_item (ItemTag) checks the item in hand.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
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
        return D2SpongeEventHelper.checkBlockType(location.getInternal().toLocation().getBlock().getType(), data, this::error)
                && D2SpongeEventHelper.checkItem(new ItemTag(player.getInternal()
                .getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.empty())), data, this::error)
                && D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(location.getInternal(), data, this::error);
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
        World world = player.getWorld();
        Optional<Location<World>> opt = evt.getTargetBlock().getLocation();
        if (opt.isPresent()) {
            event.location = new LocationTag(opt.get());
            Vector3d point = evt.getInteractionPoint().get();
            event.precise_location = new LocationTag(point, world);
            event.intersection_point = new LocationTag(point.sub(opt.get().getPosition()));
            event.impact_normal = new LocationTag(evt.getTargetSide().asOffset());
        }
        else {
            BlockRayHit<World> brh = BlockRay.from(player)
                    .distanceLimit(Utilities.getHandReach(player)).build().end().get();
            event.location = new LocationTag(brh.getLocation());
            event.precise_location = new LocationTag(brh.getPosition(), world);
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
