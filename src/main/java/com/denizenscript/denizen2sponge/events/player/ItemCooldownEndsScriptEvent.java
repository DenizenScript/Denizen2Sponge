package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
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

public class ItemCooldownEndsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.4.0
    // @Events
    // item cooldown ends
    //
    // @Updated 2018/01/07
    //
    // @Cancellable false
    //
    // @Group Player
    //
    // @Triggers when a item type's cooldown ends for a player.
    //
    // @Switch type (ItemTypeTag) checks the item type.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // player (PlayerTag) returns the player that has the cooldown.
    // item_type (ItemTypeTag) returns the item type that went off cooldown.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "ItemCooldownEnds";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("item cooldown ends");
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

    public CooldownEvent.End internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("item_type", item_type);
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
    public void onItemCooldownEnds(CooldownEvent.End evt, @Root Player player) {
        ItemCooldownEndsScriptEvent event = (ItemCooldownEndsScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.item_type = new ItemTypeTag(evt.getItemType());
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
