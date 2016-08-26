package org.mcmonkey.denizen2sponge.events.server;

import org.mcmonkey.denizen2core.events.ScriptEvent;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.objects.IntegerTag;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2sponge.Denizen2Sponge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusClient;

import java.util.HashMap;
import java.util.Optional;

public class ClientPingsServerScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // client pings server
    //
    // @Cancellable false
    //
    // @Triggers when a client pings the server for information.
    //
    // @Context
    // address TextTag returns the address of the client.
    // version TextTag returns the Minecraft version the client is using to ping.
    // motd TextTag returns the Message Of The Day that will display on the client.
    // num_players IntegerTag returns the number of online players that will display on the client.
    // max_players IntegerTag returns the number of maximum players that will display on the client.
    //
    // @Determinations
    // None. // TODO: Change motd, favicon, hide players (num/max players??)
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

    public TextTag motd;

    public IntegerTag num_players;

    public IntegerTag max_players;

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
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        super.applyDetermination(errors, determination, value);
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
        StatusClient client = evt.getClient();
        ClientPingServerEvent.Response response = evt.getResponse();
        ClientPingsServerScriptEvent event = (ClientPingsServerScriptEvent) clone();
        event.address = new TextTag(client.getAddress().toString());
        event.version = new TextTag(client.getVersion().getName());
        event.motd = new TextTag(response.getDescription().toPlain()); // TODO: some sort of objects for formatted Text?
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
        event.run();
    }
}
