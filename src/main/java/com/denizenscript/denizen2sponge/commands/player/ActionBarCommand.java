package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

public class ActionBarCommand extends AbstractCommand {

    // <--[command]
    // @Name actionbar
    // @Arguments <player> <message>
    // @Short sends an actionbar message to a player a message.
    // @Updated 2016/09/22
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Sends an actionbar message to a player a message.
    // TODO: Explain more!
    // @Example
    // # This example sends the player "bob" the message "hello there!"
    // - actionbar bob "hello there!"
    // -->

    @Override
    public String getName() {
        return "actionbar";
    }

    @Override
    public String getArguments() {
        return "<player> <message>";
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
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        AbstractTagObject ato = entry.getArgumentObject(queue, 1);
        if (queue.shouldShowGood()) {
            queue.outGood("Telling " + ColorSet.emphasis + player.getInternal().getName() + ColorSet.good + ": " + ColorSet.emphasis + ato.toString());
        }
        if (ato instanceof FormattedTextTag) {
            player.getInternal().sendMessage(ChatTypes.ACTION_BAR, ((FormattedTextTag) ato).getInternal());
        }
        else {
            player.getInternal().sendMessage(ChatTypes.ACTION_BAR, Denizen2Sponge.parseColor(ato.toString()));
        }
    }
}
