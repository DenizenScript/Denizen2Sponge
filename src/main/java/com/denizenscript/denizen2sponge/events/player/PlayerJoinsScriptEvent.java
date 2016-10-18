package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.HashMap;

public class PlayerJoinsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player joins
    //
    // @Updated 2016/10/18
    //
    // @Cancellable false
    //
    // @Triggers when a player successfully joins the server.
    //
    // @Context
    // player (PlayerTag) returns the player that joined.
    // message (FormattedTextTag) returns the message that will be broadcast to the server.
    //
    // @Determinations
    // None.
    // -->

    @Override
    public String getName() {
        return "PlayerJoins";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player joins");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public PlayerTag player;

    public FormattedTextTag message;

    public ClientConnectionEvent.Join internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("message", message);
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
    public void onJoin(ClientConnectionEvent.Join evt, @Root Player player) {
        PlayerJoinsScriptEvent event = (PlayerJoinsScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.message = new FormattedTextTag(evt.getMessage());
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
    }
}
