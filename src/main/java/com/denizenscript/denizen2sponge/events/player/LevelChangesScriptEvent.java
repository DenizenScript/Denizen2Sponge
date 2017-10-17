package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.ChangeLevelEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class LevelChangesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // level changes
    //
    // @Updated 2017/10/16
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player's level changes.
    //
    // @Warning This event does not trigger in Sponge during last testing.
    //
    // @Switch world (WorldTag) checks the world.
    //
    // @Context
    // player (PlayerTag) returns the player that changed level.
    // old_level (IntegerTag) returns the old level value.
    // new_level (IntegerTag) returns the new level value.
    //
    // @Determinations
    // level (IntegerTag) sets the new level value.
    // -->

    @Override
    public String getName() {
        return "LevelChanges";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("level changes");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(player.getOnline(this::error).getLocation().getExtent(), data, this::error);
    }

    public PlayerTag player;

    public IntegerTag old_level;

    public IntegerTag new_level;

    public ChangeLevelEvent.TargetPlayer internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("old_level", old_level);
        defs.put("new_level", new_level);
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
    public void onLevelChanges(ChangeLevelEvent.TargetPlayer evt, @Root Player player) {
        LevelChangesScriptEvent event = (LevelChangesScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.old_level = new IntegerTag(evt.getOriginalLevel());
        event.new_level = new IntegerTag(evt.getLevel());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("level")) {
            IntegerTag it = IntegerTag.getFor(this::error, value);
            new_level = it;
            internal.setLevel((int) it.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
