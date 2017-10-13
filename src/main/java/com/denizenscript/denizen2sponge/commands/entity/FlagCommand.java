package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TimeTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.entity.Entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.*;
import java.util.Map;
import java.util.Optional;

public class FlagCommand extends AbstractCommand {

    // <--[command]
    // @Name flag
    // @Arguments <entity>/'server' <map of flags to set>
    // @Short flags an entity with some data.
    // @Updated 2017/02/15
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named duration (DurationTag) Sets the duration to apply to the flags being set.
    // @Description
    // Adds or edits flags on an entity (including players, etc.).
    // See also the <@link command unflag>unflag command<@/link>.
    // @Example
    // # Mark the player as a VIP.
    // - flag <player> vip:true
    // @Example
    // # Increase the player's XP by 5, reverting to 0 (unset) after one minute.
    // - flag <player> xp:<player.flag[xp].add[5]||5> --duration 1m
    // -->

    @Override
    public String getName() {
        return "flag";
    }

    @Override
    public String getArguments() {
        return "<entity>/'server' <map of flags to set>";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        AbstractTagObject ato = entry.getArgumentObject(queue, 0);
        MapTag basic;
        Entity entity = null;
        TimeTag tt = null;
        if (entry.namedArgs.containsKey("duration")) {
            DurationTag duration = DurationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "duration"));
            LocalDateTime ldt = LocalDateTime.now(ZoneId.of("UTC")).plus((long)(duration.getInternal() * 1000), ChronoField.MILLI_OF_SECOND.getBaseUnit());
            tt = new TimeTag(ldt);
        }
        if (CoreUtilities.toLowerCase(ato.toString()).equals("server")) {
            basic = Denizen2Sponge.instance.serverFlagMap;
        }
        else {
            EntityTag entityTag = EntityTag.getFor(queue.error, ato);
            entity = entityTag.getInternal();
            Optional<FlagMap> fm = entity.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                basic = fm.get().flags;
            }
            else {
                basic = new MapTag();
            }
        }
        MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> dat : propertyMap.getInternal().entrySet()) {
            MapTag gen = new MapTag();
            gen.getInternal().put("value", dat.getValue());
            if (tt != null) {
                gen.getInternal().put("duration", tt);
            }
            basic.getInternal().put(CoreUtilities.toLowerCase(dat.getKey()), gen);
        }
        if (entity != null) {
            entity.offer(new FlagMapDataImpl(new FlagMap(basic)));
            if (queue.shouldShowGood()) {
                queue.outGood("Flagged the entity "
                        + ColorSet.emphasis + new EntityTag(entity).debug() + ColorSet.good
                        + " with the specified data... (" + propertyMap.debug() + ")"
                        + (tt == null ? " For unlimited time. " : " Until time: " + tt.debug()));
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("Flagged the server with the specified data... (" + propertyMap.debug() + ")"
                        + (tt == null ? " For unlimited time. " : " Until time: " + tt.debug()));
            }
        }
    }
}
