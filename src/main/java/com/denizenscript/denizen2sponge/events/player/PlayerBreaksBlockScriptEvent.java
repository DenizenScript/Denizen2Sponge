package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

public class PlayerBreaksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // player breaks block
    //
    // @Updated 2017/10/14
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player breaks a block.
    //
    // @Switch type (BlockTypeTag) checks the block type.
    // @Switch with_item (ItemTag) checks the item in hand.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // player (PlayerTag) returns the player that broke the block.
    // material (BlockTypeTag) returns the broken material.
    // location (LocationTag) returns the location of the broken block.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerBreaksBlock";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player breaks block");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkBlockType(material.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkItem(new ItemTag(player.getInternal()
                .getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.empty())), data, this::error)
                && D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(location.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                location.getInternal().world.getWeather().getId()), data, this::error);
    }

    public PlayerTag player;

    public BlockTypeTag material;

    public LocationTag location;

    public ChangeBlockEvent.Break internal;

    public Transaction<BlockSnapshot> block;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("material", material);
        defs.put("location", location);
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
    public void onBlockBroken(ChangeBlockEvent.Break evt, @Root Player player) {
        for (Transaction<BlockSnapshot> block : evt.getTransactions()) {
            PlayerBreaksBlockScriptEvent event = (PlayerBreaksBlockScriptEvent) clone();
            event.internal = evt;
            event.block = block;
            event.player = new PlayerTag(player);
            event.material = new BlockTypeTag(block.getOriginal().getState().getType());
            event.location = new LocationTag(block.getOriginal().getLocation().get());
            event.cancelled = evt.isCancelled();
            event.run();
            evt.setCancelled(event.cancelled);
        }
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
