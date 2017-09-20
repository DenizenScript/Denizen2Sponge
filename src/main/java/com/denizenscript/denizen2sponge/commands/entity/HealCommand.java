package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

public class HealCommand extends AbstractCommand {

    // <--[command]
    // @Name heal
    // @Arguments <entity> <amount>
    // @Short changes the health of the entity.
    // @Updated 2017/04/19
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named type (TextTag) Sets of what type the health will be.
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Changes the health of the entity. Optionally specify a type ('remaining' or 'maximum')
    // to adjust the specified health type. Defaults to 'remaining'. Also specify whether
    // the command will 'add' or 'set' the value. Defaults to 'add'.
    // @Example
    // # This example heals the player for 4 points (2 hearts)
    // - heal <player> 4
    // -->

    @Override
    public String getName() {
        return "heal";
    }

    @Override
    public String getArguments() {
        return "<entity> <amount>";
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
        EntityTag entityTag = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Living ent = (Living) entityTag.getInternal();
        NumberTag amount = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
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
        double value;
        if (entry.namedArgs.containsKey("type")) {
            type = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "type").toString());
            switch (type) {
                case "remaining":
                    value = ent.health().transform(operation.equals("add") ?
                            x -> x + amount.getInternal() : x -> amount.getInternal()).get();
                    ent.offer(Keys.HEALTH, value);
                    break;
                case "maximum":
                    value = ent.maxHealth().transform(operation.equals("add") ?
                            x -> x + amount.getInternal() : x -> amount.getInternal()).get();
                    ent.offer(Keys.MAX_HEALTH, value);
                    break;
                default:
                    queue.handleError(entry, "Invalid health type: '" + type + "'!");
                    return;
            }
        }
        else {
            type = "remaining";
            value = ent.health().transform(operation.equals("add") ?
                    x -> x + amount.getInternal() : x -> amount.getInternal()).get();
            ent.offer(Keys.HEALTH, value);
        }
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (operation.equals("add") ? "Increasing" : "Setting") +
                    ColorSet.good + " the " + ColorSet.emphasis + type + ColorSet.good + " health of " +
                    ColorSet.emphasis + entityTag.debug() + ColorSet.good + (operation.equals("add") ? " by " : " to ") +
                    ColorSet.emphasis + amount.debug() + ColorSet.good + "!");
        }
    }
}
