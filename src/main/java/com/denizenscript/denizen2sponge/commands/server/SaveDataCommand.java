package com.denizenscript.denizen2sponge.commands.server;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2sponge.Denizen2Sponge;

public class SaveDataCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name savedata
    // @Arguments
    // @Short saves extra D2 data.
    // @Updated 2017/09/15
    // @Group Server
    // @Minimum 0
    // @Maximum 0
    // @Description
    // Saves extra D2 data, for example: Server flags.
    // @Example
    // # This example saves extra D2 data.
    // - savedata
    // -->

    @Override
    public String getName() {
        return "savedata";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        Denizen2Sponge.instance.saveServerFlags();
        if (queue.shouldShowGood()) {
            queue.outGood("Saved all data.");
        }
    }
}
