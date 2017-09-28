package com.denizenscript.denizen2sponge.commands.server;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class AnnounceCommand extends AbstractCommand {

    // <--[command]
    // @Name announce
    // @Arguments <message>
    // @Short announces a message to all online players.
    // @Updated 2017/09/25
    // @Group Server
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Announces a message to all online players.
    // @Example
    // # This example announces to all players that the server is being shut down.
    // - announce "Shutting down the server!"
    // -->

    @Override
    public String getName() {
        return "announce";
    }

    @Override
    public String getArguments() {
        return "<message>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        AbstractTagObject message = entry.getArgumentObject(queue, 0);
        Text text;
        if (message instanceof FormattedTextTag) {
            text = ((FormattedTextTag) message).getInternal();
        }
        else {
            text = Denizen2Sponge.parseColor(message.toString());
        }
        Sponge.getServer().getBroadcastChannel().send(text);
        if (queue.shouldShowGood()) {
            queue.outGood("Announcing to all players: " + ColorSet.emphasis + message.debug());
        }
    }
}
