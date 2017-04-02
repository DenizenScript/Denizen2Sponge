package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;

public class GiveCommand extends AbstractCommand {

    // <--[explanation]
    // @Name Item Types
    // @Group Useful Lists
    // @Description
    // A list of all default item types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/item/ItemTypes.html>item types list<@/link>
    // These can be used with the give and equip commands.
    // -->

    // <--[command]
    // @Name give
    // @Arguments <player> <item>
    // @Short gives a player an item.
    // @Updated 2016/11/24
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Gives a player an item.
    // Related information: <@link explanation Item Types>item types<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example gives the player a stone.
    // - give <player> stone
    // -->

    @Override
    public String getName() {
        return "give";
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
            queue.outGood("Giving " + ColorSet.emphasis + player.debug() + ColorSet.good
                    + ": " + ColorSet.emphasis + item.debug());
        }
        InventoryTransactionResult itr = player.getInternal().getInventory().offer(item.getInternal());
        for (ItemStackSnapshot iss : itr.getReplacedItems()) {
            if (queue.shouldShowGood()) {
                queue.outGood("Gave: " + new ItemTag(iss.createStack()).debug());
            }
        }
        for (ItemStackSnapshot iss : itr.getRejectedItems()) {
            if (queue.shouldShowGood()) {
                queue.outGood("Failed to give: " + new ItemTag(iss.createStack()).debug());
            }
        }
    }
}
