package com.denizenscript.denizen2sponge.commands.items;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;

public class CreateInventoryCommand extends AbstractCommand {

    // <--[command]
    // @Name createinventory
    // @Arguments <type>
    // @Short creates a new inventory.
    // @Updated 2017/06/11
    // @Group Items
    // @Minimum 1
    // @Maximum 1
    // @Save createinventory_inv (InventoryTag) returns the created inventory.
    // @Description
    // Creates a new inventory of the given type.
    // See also rememberinventory command.
    // @Example
    // # This example creates an inventory and saves it in definition "test_inv".
    // - createinventory CHEST --save test_inv
    // -->

    @Override
    public String getName() {
        return "createinventory";
    }

    @Override
    public String getArguments() {
        return "<type>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        String type = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 0).toString());
        InventoryArchetype arch = null;
        for (InventoryArchetype ia : Sponge.getRegistry().getAllOf(InventoryArchetype.class)) {
            if (CoreUtilities.toLowerCase(ia.getId()).equals(type)
                    || CoreUtilities.toLowerCase(ia.getName()).equals(type)) {
                arch = ia;
            }
        }
        if (arch == null) {
            for (InventoryArchetype ia : Sponge.getRegistry().getAllOf(InventoryArchetype.class)) {
                if (CoreUtilities.toLowerCase(ia.getId()).contains(type)) {
                    arch = ia;
                }
            }
        }
        if (arch == null) {
            queue.handleError(entry, "Unknown inventory type!");
            return;
        }
        InventoryTag inv = new InventoryTag(Inventory.builder().of(arch).build(Denizen2Sponge.plugin));
        queue.commandStack.peek().setDefinition(entry.resName(queue, "rememberinventory_inv"), inv);
    }
}
