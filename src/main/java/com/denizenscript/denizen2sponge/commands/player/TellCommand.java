package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.text.Text;

public class TellCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name tell
    // @Arguments <player list> <message>
    // @Short tells a list of players a message.
    // @Updated 2017/09/25
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Tells a list of players a message.
    // TODO: Explain more!
    // @Example
    // # This example tells the current player the message "hello there!" in the chat box.
    // - tell <player> "hello there!"
    // -->

    @Override
    public String getName() {
        return "tell";
    }

    @Override
    public String getArguments() {
        return "<player list> <message>";
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
        ListTag players = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        AbstractTagObject message = entry.getArgumentObject(queue, 1);
        Text text;
        if (message instanceof FormattedTextTag) {
            text = ((FormattedTextTag) message).getInternal();
        }
        else {
            text = Denizen2Sponge.parseColor(message.toString());
        }
        for (AbstractTagObject ato : players.getInternal()) {
            PlayerTag player = PlayerTag.getFor(queue.error, ato);
            player.getOnline(queue.error).sendMessage(text);
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Telling " + ColorSet.emphasis + players.debug()
                    + ColorSet.good + ": " + ColorSet.emphasis + message.debug());
        }
    }
}
