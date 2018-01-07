package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.ItemTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;

public class CooldownCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name cooldown
    // @Arguments <player> <item type> [duration]
    // @Short sets an item type's cooldown for a player.
    // @Updated 2018/01/07
    // @Group Player
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Sets an item type's cooldown for a player. If no duration is specified,
    // the cooldown is reset to 0. This cooldown is visually represented in the
    // hotbar slot and must not be confused with the attack indicator.
    // Related information: <@link explanation Item Types>item types<@/link>.
    // @Example
    // # This example gives the player's diamond sword a 4 second cooldown.
    // - cooldown <player> diamond_sword 4
    // -->

    @Override
    public String getName() {
        return "cooldown";
    }

    @Override
    public String getArguments() {
        return "<player> <item type> [duration]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        ItemTypeTag item = ItemTypeTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        boolean passed;
        if (entry.arguments.size() > 2) {
            DurationTag duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            passed = player.getOnline(queue.error).getCooldownTracker().setCooldown(item.getInternal(),
                    (int) (duration.getInternal() * 20));
            if (queue.shouldShowGood()) {
                queue.outGood("Attempting to set cooldown of item type '" + ColorSet.emphasis + item.debug()
                        + ColorSet.good + "' for player '" + ColorSet.emphasis + player.debug()
                        + ColorSet.good + "' to: " + ColorSet.emphasis + duration.debug() + ColorSet.good + "!");
            }
        }
        else {
            passed = player.getOnline(queue.error).getCooldownTracker().resetCooldown(item.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Attempting to reset cooldown of item type '" + ColorSet.emphasis + item.debug()
                        + ColorSet.good + "' for player '" + ColorSet.emphasis + player.debug() + ColorSet.good + "'!");
            }
        }
        if (passed) {
            if (queue.shouldShowGood()) {
                queue.outGood("Cooldown operation passed!");
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("Cooldown operation failed!");
            }
        }
    }
}
