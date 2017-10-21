package com.denizenscript.denizen2sponge.commands.items;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2sponge.Denizen2Sponge;

public class ForgetInventoryCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name forgetinventory
    // @Arguments <name>
    // @Short forgets a remembered inventory.
    // @Updated 2017/06/11
    // @Group Items
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Forgets an inventory that was remembered.
    // See also rememberinventory command.
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
        return "<name>";
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
        String name = entry.getArgumentObject(queue, 0).toString();
        Denizen2Sponge.rememberedInventories.remove(name);
    }
}
