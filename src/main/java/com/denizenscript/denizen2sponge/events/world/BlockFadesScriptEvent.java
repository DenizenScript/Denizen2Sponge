package com.denizenscript.denizen2sponge.events.world;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.HashMap;

public class BlockFadesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // block fades
    //
    // @Updated 2017/10/14
    //
    // @Group World
    //
    // @Cancellable true
    //
    // @Triggers when a block fades (like leaves).
    //
    // @Switch type (BlockTypeTag) checks the block type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // location (LocationTag) returns the location of the faded block.
    // material (BlockTypeTag) returns the type of the block.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "BlockFades";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("block fades");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkBlockType(material.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWorld(location.getInternal().world, data, this::error)
                && D2SpongeEventHelper.checkCuboid(location.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        location.getInternal().world.getWeather().getId()), data, this::error);
    }

    public LocationTag location;

    public BlockTypeTag material;

    public ChangeBlockEvent.Decay internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("location", location);
        defs.put("material", material);
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
    public void onBlockFades(ChangeBlockEvent.Decay evt) {
        for (Transaction<BlockSnapshot> block : evt.getTransactions()) {
            BlockFadesScriptEvent event = (BlockFadesScriptEvent) clone();
            event.internal = evt;
            event.location = new LocationTag(block.getFinal().getLocation().get());
            event.material = new BlockTypeTag(block.getOriginal().getState().getType());
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

