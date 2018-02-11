package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperation;
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
    // @Named operation (TextTag) Sets the matching operation that will be used. Available types are "item", "item_exact" and "item_type".
    // @Description
    // Takes an item from a player. The "item" operation takes items that exactly match the
    // item specified, ignoring its quantity. The "item_exact" operation only takes items that
    // exactly match the item specified and its quantity. The "item_type" operation takes every
    // time whose type matches the specified item's type. Operation defaults to "item".
    // @Example
    // # This example takes 5 plain stone blocks from the player.
    // - take <player> stone/5
    // @Example
    // # This example takes an items of type "dirt" from the player.
    // - take <player> dirt --operation item_type
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
        String type;
        if (entry.namedArgs.containsKey("operation")) {
            type = entry.getNamedArgumentObject(queue, "operation").toString();
        }
        else {
            type = "item";
        }
        QueryOperation<?> operation;
        switch (type) {
            case "item":
                operation = QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item.getInternal());
                break;
            case "item_exact":
                operation = QueryOperationTypes.ITEM_STACK_EXACT.of(item.getInternal());
                break;
            case "item_type":
                operation = QueryOperationTypes.ITEM_TYPE.of(item.getInternal().getType());
                break;
            default:
                queue.handleError(entry, "The operation type specified is invalid!");
                return;
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Attempting to take " + ColorSet.emphasis + item.debug() + ColorSet.good
                    + " from " + ColorSet.emphasis + player.debug() + ColorSet.good + "!");
        }
        Optional<ItemStack> result = player.getInternal().getInventory().query(operation)
                .poll(item.getInternal().getQuantity());
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
