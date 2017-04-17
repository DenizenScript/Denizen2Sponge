package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

import java.util.Optional;

public class AirCommand extends AbstractCommand {

    // <--[command]
    // @Name air
    // @Arguments <entity> <duration>
    // @Short sets the air level of the entity.
    // @Updated 2017/04/17
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Named type (TextTag) Sets of what type the air level will be.
    // @Description
    // Sets the air level of the entity. Optionally specify a type ('remaining' or 'maximum')
    // to adjust the specified air level type. Defaults to 'remaining'.
    // @Example
    // # This example completely fills the air bar of the player
    // - air <player> <player.max_air>
    // -->

    @Override
    public String getName() {
        return "air";
    }

    @Override
    public String getArguments() {
        return "<entity> <duration>";
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
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        DurationTag dur = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (!ent.getInternal().supports(Keys.MAX_AIR)) {
            queue.handleError(entry, "This entity does not support air levels!");
            return;
        }
        String type;
        if (entry.namedArgs.containsKey("type")) {
            type = entry.getNamedArgumentObject(queue, "type").toString();
            switch (type) {
                case "remaining":
                    ent.getInternal().offer(Keys.REMAINING_AIR, (int) (dur.getInternal() * 20));
                    break;
                case "maximum":
                    break;
                default:
                    queue.handleError(entry, "Invalid air level type: '" + type + "'!");
                    return;
            }
        }
        else {
            type = "remaining";
            ent.getInternal().offer(Keys.REMAINING_AIR, (int) (dur.getInternal() * 20));
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Setting the " + ColorSet.emphasis + type + ColorSet.good + " air level of "
                    + ColorSet.emphasis + ent.debug() + ColorSet.good + " to "
                    + ColorSet.emphasis + dur.debug() + ColorSet.good + " seconds!");
        }
    }
}
