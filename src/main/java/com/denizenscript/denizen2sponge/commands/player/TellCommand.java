package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.text.Text;

public class TellCommand extends AbstractCommand {

    // <--[command]
    // @Name tell
    // @Arguments <player> <message>
    // @Short tells a player a message.
    // @Updated 2016/08/27
    // @Group Queue
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Tells a player a message.
    // TODO: Explain more!
    // @Example
    // # This example tells player "bob" the message "hello there!"
    // - tell bob "hello there!"
    // -->

    @Override
    public String getName() {
        return "tell";
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
        String message = entry.getArgumentObject(queue, 1).toString();
        if (queue.shouldShowGood()) {
            queue.outGood("Telling " + ColorSet.emphasis + player.getInternal().getName() + ColorSet.good + ": " + ColorSet.emphasis + message);
        }
        // TODO: preformatted text magic stuff. A wrapper tag type around Sponge Text class.
        player.getInternal().sendMessage(Text.of(message));
    }
}
