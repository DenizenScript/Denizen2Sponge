package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.HashMap;

public class PlayerDisconnectsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player disconnects
    //
    // @Updated 2017/03/22
    //
    // @Cancellable false
    //
    // @Group Player
    //
    // @Triggers when a player disconnects from the server.
    //
    // @Context
    // player (PlayerTag) returns the player that disconnected.
    // message (FormattedTextTag) returns the message that will be broadcast to the server.
    //
    // @Determinations
    // message (FormattedTextTag) to set the message displayed when a player disconnects.
    // cancel_message (BooleanTag) to set whether the disconnect message is cancelled.
    // -->

    @Override
    public String getName() {
        return "PlayerDisconnects";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player disconnects");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public PlayerTag player;

    public FormattedTextTag message;

    public ClientConnectionEvent.Disconnect internal;

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
    public void onDisconnect(ClientConnectionEvent.Disconnect evt, @Root Player player) {
        PlayerDisconnectsScriptEvent event = (PlayerDisconnectsScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.message = new FormattedTextTag(evt.getMessage());
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("message")) {
            FormattedTextTag ftt = FormattedTextTag.getFor(this::error, value);
            message = ftt;
            internal.setMessage(ftt.getInternal());
        }
        else if (determination.equals("cancel_message")) {
            // TODO: Context for this?
            BooleanTag bt = BooleanTag.getFor(this::error, value);
            internal.setMessageCancelled(bt.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
