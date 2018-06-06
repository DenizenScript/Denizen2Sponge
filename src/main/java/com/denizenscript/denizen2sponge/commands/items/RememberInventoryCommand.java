package com.denizenscript.denizen2sponge.commands.items;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;

public class RememberInventoryCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name rememberinventory
    // @Arguments <inventory> <name>
    // @Short remembers an inventory until shutdown.
    // @Updated 2018/06/05
    // @Group Items
    // @Minimum 2
    // @Maximum 2
    // @Save rememberinventory_inv (InventoryTag) returns the remembered inventory.
    // @Description
    // Remembers an inventory until shutdown.
    // Does not persist shutdowns. // TODO: Make persistable! Ideally an argument to this command for persistent or not.
    // Will exactly store the original inventory - meaning a player inventory remembered will still be owned by that player.
    // TODO: Clear way to duplicate an existing inventory to form a new rememberd inventory.
    // Can take any inventory type.
    // See also the <@link command forgetinventory>forgetinventory command<@/link>.
    // The inventory will be accessible as "shared/<name>".
    // Inputting an already used name will override it.
    // @Example
    // # This example remembers the player's inventory as "Test", and stores it in definition "my_inv".
    // - rememberinventory <player.inventory> "Test" --save my_inv
    // -->

    @Override
    public String getName() {
        return "rememberinventory";
    }

    @Override
    public String getArguments() {
        return "<inventory> <name>";
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
        InventoryTag inv = InventoryTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String name = entry.getArgumentObject(queue, 1).toString();
        inv.remAs = name;
        Denizen2Sponge.rememberedInventories.put(name, inv);
        queue.commandStack.peek().setDefinition(entry.resName(queue, "rememberinventory_inv"), inv);
        if (queue.shouldShowGood()) {
            queue.outGood("Inventory " + ColorSet.emphasis + inv.remAs + ColorSet.good + " remembered as " + ColorSet.emphasis + name);
        }
    }
}
