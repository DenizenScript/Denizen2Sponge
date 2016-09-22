package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class PlayerPlacesBlockScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player places block
    //
    // @Updated 2016/09/22
    //
    // @Cancellable true
    //
    // @Triggers when a player places a block.
    //
    // @Context
    // player (PlayerTag) returns the player that broke the block.
    // material (BlockTypeTag) returns the placed material.
    // old_material (BlockTypeTag) returns the material that the block was placed onto.
    // location (LocationTag) returns the location of the broken block.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerPlacesBlock";
    }

    @Override
    public boolean couldMatch(ScriptEvent.ScriptEventData data) {
        return data.eventPath.startsWith("player places block");
    }

    @Override
    public boolean matches(ScriptEvent.ScriptEventData data) {
        return true;
    }

    public PlayerTag player;

    public BlockTypeTag material;

    public BlockTypeTag old_material;

    public LocationTag location;

    public ChangeBlockEvent.Place internal;

    public Transaction<BlockSnapshot> block;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEvent.ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("material", material);
        defs.put("old_material", old_material);
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
    public void onBlockBroken(ChangeBlockEvent.Place evt, @Root Player player) {
        for (Transaction<BlockSnapshot> block : evt.getTransactions()) {
            PlayerPlacesBlockScriptEvent event = (PlayerPlacesBlockScriptEvent) clone();
            event.internal = evt;
            event.block = block;
            event.player = new PlayerTag(player);
            event.material = new BlockTypeTag(block.getFinal().getState().getType());
            event.old_material = new BlockTypeTag(block.getOriginal().getState().getType());
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
