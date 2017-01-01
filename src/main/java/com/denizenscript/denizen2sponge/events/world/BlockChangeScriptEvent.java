package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.HashMap;

public class BlockChangeScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // block changes
    //
    // @Updated 2016/09/23
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when a block changes for any given reason.
    //
    // @Context
    // location (LocationTag) returns the location of the changed block.
    // new_material (BlockTypeTag) returns the new type of the block.
    // old_material (BlockTypeTag) returns the old type of the block.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "BlockChanges";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("block changes");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public LocationTag location;

    public BlockTypeTag new_material;

    public BlockTypeTag old_material;

    public ChangeBlockEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("location", location);
        defs.put("new_material", new_material);
        defs.put("old_material", old_material);
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
    public void onBlockChanged(ChangeBlockEvent evt) {
        for (Transaction<BlockSnapshot> block : evt.getTransactions()) {
            BlockChangeScriptEvent event = (BlockChangeScriptEvent) clone();
            event.internal = evt;
            event.location = new LocationTag(block.getFinal().getLocation().get());
            event.new_material = new BlockTypeTag(block.getFinal().getState().getType());
            event.old_material = new BlockTypeTag(block.getOriginal().getState().getType());
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

