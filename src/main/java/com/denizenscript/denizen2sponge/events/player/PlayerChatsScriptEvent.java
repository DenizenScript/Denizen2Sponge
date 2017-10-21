package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.HashMap;

public class PlayerChatsScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // player chats
    //
    // @Updated 2017/10/14
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player sends a chat message.
    //
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    //
    // @Context
    // player (PlayerTag) returns the player that sent the message.
    // message (FormattedTextTag) returns the chat message.
    //
    // @Determinations
    // message (FormattedTextTag) to set the message that will be displayed instead.
    // -->

    @Override
    public String getName() {
        return "PlayerChats";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("player chats");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(player.getOnline(this::error).getLocation().getExtent(), data, this::error)
                && D2SpongeEventHelper.checkCuboid((new LocationTag(player.getOnline(this::error)
                .getLocation())).getInternal(), data, this::error);
    }

    public PlayerTag player;

    public FormattedTextTag message;

    public MessageChannelEvent.Chat internal;

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
    public void onChat(MessageChannelEvent.Chat evt, @Root Player player) {
        PlayerChatsScriptEvent event = (PlayerChatsScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.message = new FormattedTextTag(evt.getRawMessage());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("message")) {
            FormattedTextTag ftt = FormattedTextTag.getFor(this::error, value);
            message = ftt;
            internal.setMessage(ftt.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
