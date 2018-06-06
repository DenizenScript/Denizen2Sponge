package com.denizenscript.denizen2sponge.commands.items;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;

public class ForgetInventoryCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name forgetinventory
    // @Arguments <inventory/name>
    // @Short forgets a remembered inventory.
    // @Updated 2018/06/05
    // @Group Items
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Forgets an inventory that was remembered.
    // See also the <@link command rememberinventory>rememberinventory command<@/link>.
    // @Example
    // # This example forgets the inventory named "Test".
    // - forgetinventory "Test"
    // -->

    @Override
    public String getName() {
        return "forgetinventory";
    }

    @Override
    public String getArguments() {
        return "<inventory/name>";
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
        AbstractTagObject inv = entry.getArgumentObject(queue, 0);
        String name;
        if (inv instanceof InventoryTag) {
            if (((InventoryTag) inv).remAs != null) {
                name = ((InventoryTag) inv).remAs;
            }
            else {
                queue.error.run("Trying to forget an inventory that isn't a remembered inventory - " + ColorSet.emphasis + inv.debug());
                return;
            }
        }
        else {
            name = inv.toString();
        }
        if (Denizen2Sponge.rememberedInventories.remove(name) == null) {
            queue.error.run("Trying to forget an inventory by a name that isn't remembered - " + ColorSet.emphasis + inv.debug());
        }
    }
}
