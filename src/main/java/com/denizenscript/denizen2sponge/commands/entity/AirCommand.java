package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;

public class AirCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name air
    // @Arguments <entity> <duration>
    // @Short changes the air level of the entity.
    // @Updated 2017/04/19
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named type (TextTag) Sets of what type the air level will be.
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Changes the air level of the entity. Optionally specify a type ('remaining' or 'maximum')
    // to adjust the specified air level type. Defaults to 'remaining'. Also specify whether
    // the command will 'add' or 'set' the value. Defaults to 'add'.
    // @Example
    // # This example completely fills the air bar of the player
    // - air <player> <player.max_air> --operation set
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
        DurationTag dt = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (!ent.getInternal().supports(Keys.MAX_AIR)) {
            queue.handleError(entry, "This entity does not support air levels!");
            return;
        }
        String operation;
        if (entry.namedArgs.containsKey("operation")) {
            operation = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "operation").toString());
            if (!(operation.equals("add") || operation.equals("set"))) {
                queue.handleError(entry, "Invalid operation: '" + operation + "'!");
                return;
            }
        }
        else {
            operation = "add";
        }
        String type;
        int ticks;
        if (entry.namedArgs.containsKey("type")) {
            type = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "type").toString());
            switch (type) {
                case "remaining":
                    ticks = operation.equals("add") ? (int) (dt.getInternal() * 20) +
                            ent.getInternal().get(Keys.REMAINING_AIR).orElseGet(() -> ent.getInternal().get(Keys.MAX_AIR).get()) :
                            (int) (dt.getInternal() * 20);
                    ent.getInternal().offer(Keys.REMAINING_AIR, ticks);
                    break;
                case "maximum":
                    ticks = operation.equals("add") ? (int) (dt.getInternal() * 20) +
                            ent.getInternal().get(Keys.MAX_AIR).get() : (int) (dt.getInternal() * 20);
                    ent.getInternal().offer(Keys.MAX_AIR, ticks);
                    break;
                default:
                    queue.handleError(entry, "Invalid air level type: '" + type + "'!");
                    return;
            }
        }
        else {
            type = "remaining";
            ticks = operation.equals("add") ? (int) (dt.getInternal() * 20) +
                    ent.getInternal().get(Keys.REMAINING_AIR).orElseGet(() -> ent.getInternal().get(Keys.MAX_AIR).get()) :
                    (int) (dt.getInternal() * 20);
            ent.getInternal().offer(Keys.REMAINING_AIR, ticks);
        }
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (operation.equals("add") ? "Increasing" : "Setting") +
                    ColorSet.good + " the " + ColorSet.emphasis + type + ColorSet.good + " air level of " +
                    ColorSet.emphasis + ent.debug() + ColorSet.good + (operation.equals("add") ? " by " : " to ") +
                    ColorSet.emphasis + dt.debug() + ColorSet.good + " seconds!");
        }
    }
}
