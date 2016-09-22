package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class PlayerBreaksBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player breaks block
    //
    // @Updated 2016/08/26
    //
    // @Cancellable true
    //
    // @Triggers when a player breaks a block.
    //
    // @Context
    // player (PlayerTag) returns the player that broke the block.
    // material (BLockTypeTag) returns the name of the broken material.
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
        return true;
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
