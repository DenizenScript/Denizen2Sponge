package org.mcmonkey.denizen2sponge.events.player;

import org.mcmonkey.denizen2core.events.ScriptEvent;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2sponge.Denizen2Sponge;
import org.mcmonkey.denizen2sponge.tags.objects.LocationTag;
import org.mcmonkey.denizen2sponge.tags.objects.PlayerTag;
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
    // @Cancellable true
    //
    // @Triggers when a player breaks a block.
    //
    // @Context
    // player PlayerTag returns the player that broke the block.
    // material TextTag returns the name of the broken material. // TODO: MaterialTag.
    // location LocationTag returns the location of the broken block.
    //
    // @Determinations
    // None. // TODO: Change drops, etc.
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

    public TextTag material;

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
            event.material = new TextTag(block.getOriginal().getState().getType().getName());
            event.location = new LocationTag(block.getOriginal().getLocation().get()); // TODO: mayyybe possibly handle world-less positions?
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
