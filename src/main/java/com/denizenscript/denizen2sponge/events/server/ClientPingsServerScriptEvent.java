package com.denizenscript.denizen2sponge.events.server;

import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusClient;

import java.util.HashMap;
import java.util.Optional;

public class ClientPingsServerScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // client pings server
    //
    // @Updated 2016/08/26
    //
    // @Cancellable false
    //
    // @Group Server
    //
    // @Triggers when a client pings the server for information.
    //
    // @Context
    // address (TextTag) returns the address of the client.
    // version (TextTag) returns the Minecraft version the client is using to ping.
    // motd (TextTag) returns the Message Of The Day that will display on the client.
    // num_players (IntegerTag) returns the number of online players that will display on the client.
    // max_players (IntegerTag) returns the number of maximum players that will display on the client.
    //
    // @Determinations
    // motd (TextTag) set the description/Message-of-the-Day text visible to the client ping.
    // max_players (IntegerTag) set the maximum number of players visible to the client ping.
    // num_players (IntegerTag) set the number of players visible to the client ping. NOTE: CANNOT BE GREATER THAN EXISTING VALUE!
    // -->

    @Override
    public String getName() {
        return "ClientPingsServer";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("client pings server");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public TextTag address;

    public TextTag version;

    public FormattedTextTag motd;

    public IntegerTag num_players;

    public IntegerTag max_players;

    public ClientPingServerEvent internal;

    // TODO: Thread safety!

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("address", address);
        defs.put("version", version);
        defs.put("motd", motd);
        defs.put("num_players", num_players);
        defs.put("max_players", max_players);
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
    public void onClientPingServer(ClientPingServerEvent evt) {
        ClientPingsServerScriptEvent event = (ClientPingsServerScriptEvent) clone();
        event.internal = evt;
        StatusClient client = evt.getClient();
        ClientPingServerEvent.Response response = evt.getResponse();
        event.address = new TextTag(client.getAddress().toString());
        event.version = new TextTag(client.getVersion().getName());
        event.motd = new FormattedTextTag(response.getDescription());
        Optional<ClientPingServerEvent.Response.Players> optPlayers = response.getPlayers();
        int numPlayers = 0;
        int maxPlayers = 0;
        if (optPlayers.isPresent()) {
            ClientPingServerEvent.Response.Players players = optPlayers.get();
            numPlayers = players.getOnline();
            maxPlayers = players.getMax();
        }
        event.num_players = new IntegerTag(numPlayers);
        event.max_players = new IntegerTag(maxPlayers);
        // TODO: Read and control the player name list! (Profiles)
        event.run();
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("motd")) {
            motd = FormattedTextTag.getFor(this::error, value);
            internal.getResponse().setDescription(motd.getInternal());
        }
        else if (determination.equals("max_players")) {
            max_players = IntegerTag.getFor(this::error, value);
            Optional<ClientPingServerEvent.Response.Players> optPlayers = internal.getResponse().getPlayers();
            if (optPlayers.isPresent()) {
                optPlayers.get().setMax((int) max_players.getInternal());
            }
        }
        else if (determination.equals("num_players")) {
            IntegerTag temp = IntegerTag.getFor(this::error, value);
            if (temp.getInternal() > num_players.getInternal()) {
                if (errors) {
                    Debug.error("Invalid num_players! Value too high!");
                }
                return;
            }
            num_players = temp;
            Optional<ClientPingServerEvent.Response.Players> optPlayers = internal.getResponse().getPlayers();
            if (optPlayers.isPresent()) {
                optPlayers.get().setOnline((int) num_players.getInternal());
            }
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
