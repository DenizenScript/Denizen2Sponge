package com.denizenscript.denizen2sponge.events.server;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.CommandBlockMinecart;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;

import java.util.HashMap;

public class CommandSentScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // command sent
    //
    // @Updated 2017/10/04
    //
    // @Cancellable true
    //
    // @Group Server
    //
    // @Triggers when a command is used by any source.
    //
    // @Context
    // command (TextTag) returns the main command that was used.
    // args (ListTag<TextTag>) returns a list of the arguments used.
    // raw_args (TextTag) returns the arguments as a single text tag.
    // source (TextTag) returns whether the command was sent by the console, a player or a command block.
    // player (PlayerTag) returns the player that sent the command, if the source was in fact one.
    // location (LocationTag) returns the location of the command block that sent the command, if the source is in fact one.
    // entity (EntityTag) returns the command block minecart that sent the command, if the source is in fact one.
    //
    // @Determinations
    // command (TextTag) to set the main command that will be used.
    // args (ListTag<TextTag>) to set a list of the arguments that will be used.
    // raw_args (TextTag) to set the arguments as a single text tag.
    // -->

    @Override
    public String getName() {
        return "CommandSent";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("command sent");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return true;
    }

    public TextTag command;

    public ListTag args;

    public TextTag raw_args;

    public TextTag source;

    public PlayerTag player;

    public LocationTag location;

    public EntityTag entity;

    public SendCommandEvent internal;

    // TODO: Thread safety!

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("command", command);
        defs.put("args", args);
        defs.put("raw_args", raw_args);
        defs.put("source", source);
        if (player != null) {
            defs.put("player", player);
        }
        if (location != null) {
            defs.put("location", location);
        }
        if (entity != null) {
            defs.put("entity", entity);
        }
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
    public void onCommandSent(SendCommandEvent evt) {
        CommandSentScriptEvent event = (CommandSentScriptEvent) clone();
        event.internal = evt;
        event.command = new TextTag(evt.getCommand());
        // TODO: Improve splitting (quoted arguments)
        ListTag list = new ListTag();
        for (String arg : evt.getArguments().split(" ")) {
            list.getInternal().add(new TextTag(arg));
        }
        event.args = list;
        event.raw_args = new TextTag(evt.getArguments());
        CommandSource source = (CommandSource) evt.getSource();
        if (source instanceof Player) {
            event.source = new TextTag("player");
            event.player = new PlayerTag((Player) source);
        }
        else if (source instanceof CommandBlock) {
            event.source = new TextTag("block");
            event.location = new LocationTag(((CommandBlock) source).getLocation());
        }
        else if (source instanceof CommandBlockMinecart) {
            event.source = new TextTag("minecart");
            event.entity = new EntityTag((CommandBlockMinecart) source);
        }
        else {
            event.source = new TextTag("server");
        }
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("command")) {
            TextTag tt = TextTag.getFor(this::error, value);
            command = tt;
            internal.setCommand(tt.getInternal());
        }
        else if (determination.equals("args")) {
            ListTag lt = ListTag.getFor(this::error, value);
            args = lt;
            String string = "";
            for (AbstractTagObject arg : lt.getInternal()) {
                string += " " + ((TextTag) arg).getInternal();
            }
            raw_args = new TextTag(string);
            internal.setArguments(string);
        }
        else if (determination.equals("raw_args")) {
            TextTag tt = TextTag.getFor(this::error, value);
            raw_args = tt;
            // TODO: Improve splitting (quoted arguments)
            ListTag list = new ListTag();
            for (String arg : tt.getInternal().split(" ")) {
                list.getInternal().add(new TextTag(arg));
            }
            args = list;
            internal.setArguments(tt.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
