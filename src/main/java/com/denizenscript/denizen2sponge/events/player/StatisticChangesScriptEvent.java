package com.denizenscript.denizen2sponge.events.player;

import com.denizenscript.denizen2core.events.ScriptEvent;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.events.D2SpongeEventHelper;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.statistic.ChangeStatisticEvent;

import java.util.HashMap;

public class StatisticChangesScriptEvent extends ScriptEvent {

    // <--[event]
    // @Since 0.3.0
    // @Events
    // statistic changes
    //
    // @Updated 2017/10/13
    //
    // @Cancellable true
    //
    // @Group Player
    //
    // @Triggers when a player statistic changes.
    //
    // @Switch statistic (TextTag) checks the statistic.
    //
    // @Context
    // player (PlayerTag) returns the player that owns the statistic.
    // statistic (TextTag) returns the changed statistic.
    // old_value (IntegerTag) returns the old statistic value.
    // new_value (IntegerTag) returns the new statistic value.
    //
    // @Determinations
    // value (IntegerTag) sets the new statistic value.
    // -->

    @Override
    public String getName() {
        return "StatisticChanges";
    }

    @Override
    public boolean couldMatch(ScriptEventData data) {
        return data.eventPath.startsWith("statistic changes");
    }

    @Override
    public boolean matches(ScriptEventData data) {
        return D2SpongeEventHelper.checkString(statistic.getInternal(), data, this::error, "statistic");
    }

    public PlayerTag player;

    public TextTag statistic;

    public IntegerTag old_value;

    public IntegerTag new_value;

    public ChangeStatisticEvent.TargetPlayer internal;

    @Override
    public HashMap<String, AbstractTagObject> getDefinitions(ScriptEventData data) {
        HashMap<String, AbstractTagObject> defs = super.getDefinitions(data);
        defs.put("player", player);
        defs.put("statistic", statistic);
        defs.put("old_value", old_value);
        defs.put("new_value", new_value);
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
    public void onStatisticChanges(ChangeStatisticEvent.TargetPlayer evt, @Root Player player) {
        StatisticChangesScriptEvent event = (StatisticChangesScriptEvent) clone();
        event.internal = evt;
        event.player = new PlayerTag(player);
        event.statistic = new TextTag(evt.getStatistic().getId());
        event.old_value = new IntegerTag(evt.getOriginalValue());
        event.new_value = new IntegerTag(evt.getValue());
        event.cancelled = evt.isCancelled();
        event.run();
        evt.setCancelled(event.cancelled);
    }

    @Override
    public void applyDetermination(boolean errors, String determination, AbstractTagObject value) {
        if (determination.equals("value")) {
            IntegerTag it = IntegerTag.getFor(this::error, value);
            new_value = it;
            internal.setValue(it.getInternal());
        }
        else {
            super.applyDetermination(errors, determination, value);
        }
    }
}
