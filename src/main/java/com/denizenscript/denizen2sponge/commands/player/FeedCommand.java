package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.data.key.Keys;

public class FeedCommand extends AbstractCommand {

    // <--[command]
    // @Name feed
    // @Arguments <player> <amount>
    // @Short changes the food level of the player.
    // @Updated 2017/04/19
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Named type (TextTag) Sets of what type the food level will be.
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Changes the food level of the player. Optionally specify a type ('hunger', 'saturation'
    // or 'exhaustion') to adjust the specified food level type. Defaults to 'hunger'. Also
    // specify whether the command will 'add' or 'set' the value. Defaults to 'add'.
    // @Example
    // # This example completely fills the hunger bar of the player
    // - feed <player> 20 --operation set
    // -->

    @Override
    public String getName() {
        return "feed";
    }

    @Override
    public String getArguments() {
        return "<player> <amount>";
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
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
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
        if (entry.namedArgs.containsKey("type")) {
            type = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "type").toString());
            switch (type) {
                case "hunger":
                    int food_level = player.getOnline(queue.error).foodLevel().transform(operation.equals("add") ?
                            x -> x + (int) amount.getInternal() : x -> (int) amount.getInternal()).get();
                    player.getInternal().offer(Keys.FOOD_LEVEL, food_level);
                    break;
                case "saturation":
                    double saturation = player.getOnline(queue.error).saturation().transform(operation.equals("add") ?
                            x -> x + amount.getInternal() : x -> amount.getInternal()).get();
                    player.getInternal().offer(Keys.SATURATION, saturation);
                    break;
                case "exhaustion":
                    double exhaustion = player.getOnline(queue.error).exhaustion().transform(operation.equals("add") ?
                            x -> x + amount.getInternal() : x -> amount.getInternal()).get();
                    player.getInternal().offer(Keys.EXHAUSTION, exhaustion);
                    break;
                default:
                    queue.handleError(entry, "Invalid food level type: '" + type + "'!");
                    return;
            }
        }
        else {
            type = "hunger";
            int food_level = player.getOnline(queue.error).foodLevel().transform(operation.equals("add") ?
                    x -> x + (int) amount.getInternal() : x -> (int) amount.getInternal()).get();
            player.getInternal().offer(Keys.FOOD_LEVEL, food_level);
        }
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (operation.equals("add") ? "Increasing" : "Setting") +
                    ColorSet.good + " the " + ColorSet.emphasis + type + ColorSet.good + " level of " +
                    ColorSet.emphasis + player.debug() + ColorSet.good + (operation.equals("add") ? " by " : " to ") +
                    ColorSet.emphasis + amount.debug() + ColorSet.good + "!");
        }
    }
}
