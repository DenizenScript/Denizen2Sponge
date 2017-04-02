package com.denizenscript.denizen2sponge.commands.server;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;

public class ExecuteCommand  extends AbstractCommand {

    // <--[command]
    // @Name execute
    // @Arguments 'as_server'/'as_player' <command> [source]
    // @Short executes a command as the specified command sender.
    // @Updated 2016/09/22
    // @Group World
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Executes a command as the specified command sender.
    // TODO: Explain more!
    // @Example
    // # This example stops the server.
    // - execute as_server "stop"
    // @Example
    // # This example makes the specified player execute 'say hi'.
    // - execute as_player "say hi" <player>
    // -->

    @Override
    public String getName() {
        return "execute";
    }

    @Override
    public String getArguments() {
        return "'as_server'/'as_player' <command> [source]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        String mode = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 0).toString());
        String cmd = entry.getArgumentObject(queue, 1).toString();
        if (mode.equals("as_server")) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
            if (queue.shouldShowGood()) {
                queue.outGood("Executed " + ColorSet.emphasis + cmd + ColorSet.good + " as the server!");
            }
        }
        else if (mode.equals("as_player")) {
            PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            Sponge.getCommandManager().process(player.getInternal(), cmd);
            if (queue.shouldShowGood()) {
                queue.outGood("Executed " + ColorSet.emphasis + cmd + ColorSet.good + " as the player: "
                        + ColorSet.emphasis + player.debug());
            }
        }
        else {
            queue.handleError(entry, "Invalid mode specified: '" + mode + "'!");
        }
    }
}
