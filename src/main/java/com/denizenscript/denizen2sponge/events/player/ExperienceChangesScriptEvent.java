package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ChangeEntityExperienceEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashMap;

public class ExperienceChangesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // experience changes
    //
    // @Updated 2017/10/16
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player's experience changes.
    //
    // @Warning This event does not trigger in Sponge during last testing.
    //
    // @Switch world (WorldTag) checks the world.
    // @Switch cuboid (CuboidTag) checks the cuboid area.
    // @Switch weather (TextTag) checks the weather.
    //
    // @Context
    // player (PlayerTag) returns the player that changed experience.
    // old_xp (IntegerTag) returns the old experience value.
    // new_xp (IntegerTag) returns the new experience value.
    //
    // @Determinations
    // level (IntegerTag) sets the new experience value.
    // -->

    @Override
    public String getName() {
        return "ExperienceChanges";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("experience changes");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkWorld(player.getOnline(this::error).getLocation().getExtent(), data, this::error)
                && D2SpongeEventHelper.checkCuboid((new LocationTag(player.getOnline(this::error)
                .getLocation())).getInternal(), data, this::error)
                && D2SpongeEventHelper.checkWeather(Utilities.getIdWithoutDefaultPrefix(
                        player.getOnline(this::error).getLocation().getExtent().getWeather().getId()), data, this::error);
    }

    public PlayerTag player;

    public IntegerTag old_xp;

    public IntegerTag new_xp;

    public ChangeEntityExperienceEvent internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("old_xp", old_xp);
        defs.put("new_xp", new_xp);
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
    public void onLevelChanges(ChangeEntityExperienceEvent evt, @Root Player player) {
        ExperienceChangesScriptEvent event = (ExperienceChangesScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.old_xp = new IntegerTag(evt.getOriginalExperience());
        event.new_xp = new IntegerTag(evt.getExperience());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("level")) {
            IntegerTag it = IntegerTag.getFor(this::error, value);
            new_xp = it;
            internal.setExperience((int) it.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
