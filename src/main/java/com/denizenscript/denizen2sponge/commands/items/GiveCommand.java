package com.denizenscript.denizen2sponge.commands.items;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

public class GiveCommand extends AbstractCommand {

    // <--[explanation]
    // @Since 0.3.0
    // @Name Item Types
    // @Group Useful Lists
    // @Description
    // A list of all default item types can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/item/ItemTypes.html>item types list<@/link>
    // These can be used with the give and equip commands.
    // -->

    // <--[command]
    // @Since 0.3.0
    // @Name give
    // @Arguments <inventory/entity> <item>
    // @Short gives an inventory an item.
    // @Updated 2018/06/05
    // @Group Items
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Gives an inventory an item. Input an entity that carries an inventory (such as a player) to give to that entity's inventory.
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
        return "<inventory/entity> <item>";
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
        ItemTag item = ItemTag.getFor(queue.error, entry.getArgumentObject(queue, 1), queue);
        AbstractTagObject ato = entry.getArgumentObject(queue, 0);
        InventoryTag inv;
        if (ato instanceof InventoryTag) {
            inv = (InventoryTag) ato;
        }
        else {
            EntityTag et = EntityTag.getFor(queue.error, ato);
            if (et.getInternal() instanceof Carrier) {
                inv = new InventoryTag(((Carrier) et.getInternal()).getInventory());
            }
            else {
                queue.error.run("Entity " + ColorSet.emphasis + et.debug() + ColorSet.warning + " does not carry an inventory!");
                return;
            }
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Giving " + ColorSet.emphasis + ato.debug() + ColorSet.good + ": " + ColorSet.emphasis + item.debug());
        }
        InventoryTransactionResult itr = inv.getInternal().offer(item.getInternal().copy());
        queue.outGood("Give result: " + ColorSet.emphasis + itr.getType().name());
        for (ItemStackSnapshot iss : itr.getReplacedItems()) {
            if (queue.shouldShowGood()) {
                queue.outGood("Successfully gave: " + ColorSet.emphasis + new ItemTag(iss.createStack()).debug());
            }
        }
        for (ItemStackSnapshot iss : itr.getRejectedItems()) {
            if (queue.shouldShowGood()) {
                queue.outGood("Failed to give: " + ColorSet.emphasis + new ItemTag(iss.createStack()).debug());
            }
        }
    }
}
