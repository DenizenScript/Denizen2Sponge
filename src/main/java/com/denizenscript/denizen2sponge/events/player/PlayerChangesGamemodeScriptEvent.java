package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.ChangeGameModeEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class PlayerChangesGamemodeScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.4.0
    // @Events
    // player changes gamemode
    //
    // @Updated 2018/02/12
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player changes gamemode.
    //
    // @Switch old_gamemode (TextTag) checks the old gamemode.
    // @Switch new_gamemode (TextTag) checks the new gamemode.
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // player (PlayerTag) returns the player that changed gamemode.
    // old_gamemode (TextTag) returns the old gamemode.
    // new_gamemode (TextTag) returns the new gamemode.
    //
    // @Determinations
    // gamemode (TextTag) sets the new gamemode.
    // -->

    @Override
    public String getName() {
        return "PlayerChangesGamemode";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player changes gamemode");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        Player playerInternal = player.getOnline(this::error);
        World world = playerInternal.getWorld();
        return D2SpongeEventHelper.checkGamemode(old_gamemode.getInternal(), data, this::error, "old_gamemode")
                && D2SpongeEventHelper.checkGamemode(new_gamemode.getInternal(), data, this::error, "new_gamemode")
                && D2SpongeEventHelper.checkWorld(world, data, this::error) && D2SpongeEventHelper.checkCuboid(
                new LocationTag(playerInternal.getLocation()).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                world.getWeather().getId()), data, this::error);
    }

    public PlayerTag player;

    public TextTag old_gamemode;

    public TextTag new_gamemode;

    public ChangeGameModeEvent.TargetPlayer internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("old_gamemode", old_gamemode);
        defs.put("new_gamemode", new_gamemode);
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
    public void onPlayerChangesGamemode(ChangeGameModeEvent.TargetPlayer evt, @Root Player player) {
        PlayerChangesGamemodeScriptEvent event = (PlayerChangesGamemodeScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.old_gamemode = new TextTag(Utilities.getIdWithoutDefaultPrefix(evt.getOriginalGameMode().getId()));
        event.new_gamemode = new TextTag(Utilities.getIdWithoutDefaultPrefix(evt.getGameMode().getId()));
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("gamemode")) {
            TextTag tt = TextTag.getFor(this::error, value);
            GameMode mode = (GameMode) Utilities.getTypeWithDefaultPrefix(GameMode.class, tt.getInternal());
            if (mode == null) {
                if (errors) {
                    error("The gamemode specified is invalid!");
                }
                return;
            }
            new_gamemode = tt;
            internal.setGameMode(mode);
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
