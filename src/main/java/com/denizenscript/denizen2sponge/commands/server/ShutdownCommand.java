package com.denizenscript.denizen2sponge.commands.server;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;

public class ShutdownCommand extends AbstractCommand {

    // <--[command]
    // @Name shutdown
    // @Arguments [reason]
    // @Short stops the server.
    // @Updated 2016/09/22
    // @Group World
    // @Minimum 0
    // @Maximum 1
    // @Description
    // Stops the server. Optionally specify a reason.
    // @Example
    // # This example stops the server.
    // - shutdown

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getArguments() {
        return "[reason]";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        if (entry.arguments.size() < 1) {
            if (queue.shouldShowGood()) {
                queue.outGood("Stopping the server");
            }
            Sponge.getServer().shutdown();
        }
        else {
            AbstractTagObject ato = entry.getArgumentObject(queue, 0    );
            if (queue.shouldShowGood()) {
                queue.outGood("Stopping the server with reason " + ColorSet.emphasis + ato.debug());
            }
            if (ato instanceof FormattedTextTag) {
                Sponge.getServer().shutdown(((FormattedTextTag) ato).getInternal());
            }
            else {
                Sponge.getServer().shutdown(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
    }
}
