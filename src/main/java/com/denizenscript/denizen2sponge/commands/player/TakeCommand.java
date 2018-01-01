package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public class TakeCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name take
    // @Arguments <player> <item>
    // @Short takes an item from a player.
    // @Updated 2018/01/01
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Takes an item from a player.
    // Related information: <@link explanation Item Types>item types<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example takes 5 stone blocks from the player.
    // - take <player> stone/5
    // -->

    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getArguments() {
        return "<player> <item>";
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
        ItemTag item = ItemTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (queue.shouldShowGood()) {
            queue.outGood("Attempting to take " + ColorSet.emphasis + item.debug() + ColorSet.good
                    + " from " + ColorSet.emphasis + player.debug() + ColorSet.good + "!");
        }
        Optional<ItemStack> result = player.getInternal().getInventory()
                .query(QueryOperationTypes.ITEM_TYPE.of(item.getInternal().getType())).poll(item.getInternal().getQuantity());
        if (result.isPresent()) {
            if (queue.shouldShowGood()) {
                queue.outGood("Items taken: " + ColorSet.emphasis + new ItemTag(result.get()).debug());
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("No items taken!");
            }
        }
    }
}
