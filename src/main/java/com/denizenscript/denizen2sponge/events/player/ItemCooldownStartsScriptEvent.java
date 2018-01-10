package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.ItemTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.CooldownEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class ItemCooldownStartsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.4.0
    // @Events
    // item cooldown starts
    //
    // @Updated 2018/01/07
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a item type's cooldown starts for a player.
    //
    // @Switch type (ItemTypeTag) checks the item type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // player (PlayerTag) returns the player that has the cooldown.
    // item_type (ItemTypeTag) returns the item type that went on cooldown.
    // new_cooldown (DurationTag) returns the new cooldown duration.
    // old_cooldown (DurationTag) returns the cooldown before the event.
    //
    // @Determinations
    // cooldown (DurationTag) sets the new cooldown duration.
    // -->

    @Override
    public String getName() {
        return "ItemCooldownStarts";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("item cooldown starts");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkItemType(item_type.getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWorld(player.getOnline(this::error).getLocation().getExtent(), data, this::error)
                && D2SpongeEventHelper.checkCuboid((new LocationTag(player.getOnline(this::error)
                .getLocation())).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        player.getOnline(this::error).getLocation().getExtent().getWeather().getId()), data, this::error);
    }

    public PlayerTag player;

    public ItemTypeTag item_type;

    public DurationTag new_cooldown;

    public DurationTag old_cooldown;

    public CooldownEvent.Set internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("item_type", item_type);
        defs.put("new_cooldown", new_cooldown);
        defs.put("old_cooldown", old_cooldown);
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
    public void onItemCooldownStarts(CooldownEvent.Set evt, @Root Player player) {
        ItemCooldownStartsScriptEvent event = (ItemCooldownStartsScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.item_type = new ItemTypeTag(evt.getItemType());
        event.new_cooldown = new DurationTag(evt.getNewCooldown() * (1.0 / 20.0));
        event.old_cooldown = new DurationTag(evt.getStartingCooldown().orElse(0) * (1.0 / 20.0));
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("cooldown")) {
            DurationTag dt = DurationTag.getFor(this::error, value);
            new_cooldown = dt;
            internal.setNewCooldown((int) (dt.getInternal() * 20));
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
