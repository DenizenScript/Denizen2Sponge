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
    // @Since 0.3.0
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
        Boolean set;
        if (entry.namedArgs.containsKey("operation")) {
            String operation = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "operation").toString());
            if (operation.equals("add")) {
                set = false;
            }
            else if (operation.equals("set")) {
                set = true;
            }
            else {
                queue.handleError(entry, "Invalid operation: '" + operation + "'!");
                return;
            }
        }
        else {
            set = false;
        }
        String type;
        if (entry.namedArgs.containsKey("type")) {
            type = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "type").toString());
            switch (type) {
                case "remaining":
                    if (set) {
                        if (amount.getInternal() < 0) {
                            queue.handleError(entry, "You can't set health to negative values!");
                            return;
                        }
                        ent.offer(Keys.HEALTH, Math.min(amount.getInternal(), ent.maxHealth().get()));
                    }
                    else {
                        ent.offer(Keys.HEALTH, ent.health().transform(
                                x -> Math.max(Math.min(x + amount.getInternal(), ent.maxHealth().get()), 0)).get());
                    }
                    break;
                case "maximum":
                    if (set) {
                        if (amount.getInternal() < 0) {
                            queue.handleError(entry, "You can't set max health to negative values!");
                            return;
                        }
                        ent.offer(Keys.MAX_HEALTH, amount.getInternal());
                    }
                    else {
                        ent.offer(Keys.MAX_HEALTH, ent.maxHealth().transform(
                                x -> Math.max(x + amount.getInternal(), 0)).get());
                    }
                    break;
                default:
                    queue.handleError(entry, "Invalid health type: '" + type + "'!");
                    return;
            }
        }
        else {
            type = "remaining";
            if (set) {
                if (amount.getInternal() < 0) {
                    queue.handleError(entry, "You can't set health to negative values!");
                    return;
                }
                ent.offer(Keys.HEALTH, Math.min(amount.getInternal(), ent.maxHealth().get()));
            }
            else {
                ent.offer(Keys.HEALTH, ent.health().transform(
                        x -> Math.max(Math.min(x + amount.getInternal(), ent.maxHealth().get()), 0)).get());
            }
        }
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (set ? "Setting" : "Increasing") +
                    ColorSet.good + " the " + ColorSet.emphasis + type + ColorSet.good + " health of " +
                    ColorSet.emphasis + entityTag.debug() + ColorSet.good + (set ? " to " : " by ") +
                    ColorSet.emphasis + amount.debug() + ColorSet.good + "!");
        }
    }
}
