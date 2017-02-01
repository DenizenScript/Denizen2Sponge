package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.entity.living.player.Player;

public class NarrateCommand extends AbstractCommand {

    // <--[command]
    // @Name narrate
    // @Arguments <message>
    // @Short tells the most relevant player a message.
    // @Updated 2017/01/31
    // @Group Player
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Tells the most relevant player a message.
    // TODO: Explain more!
    // @Example
    // # This example tells the current player the message "hello there!" in the chat box.
    // - narrate "hello there!"
    // -->

    @Override
    public String getName() {
        return "narrate";
    }

    @Override
    public String getArguments() {
        return "<message>";
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
        PlayerTag player = null;
        if (queue.commandStack.peek().hasDefinition("player")){
            AbstractTagObject ato = queue.commandStack.peek().getDefinition("player");
            if (ato instanceof PlayerTag) {
                player = (PlayerTag) ato;
            }
        }
        else if (queue.commandStack.peek().hasDefinition("context")) {
            AbstractTagObject ato = queue.commandStack.peek().getDefinition("context");
            if (ato instanceof MapTag) {
                if (((MapTag) ato).getInternal().containsKey("player")) {
                    AbstractTagObject plt = ((MapTag) ato).getInternal().get("player");
                    if (plt instanceof PlayerTag) {
                        player = (PlayerTag) plt;
                    }
                }
            }
        }
        if (player == null) {
            queue.handleError(entry, "No player located to narrate to!");
        }
        AbstractTagObject ato = entry.getArgumentObject(queue, 0);
        if (queue.shouldShowGood()) {
            queue.outGood("Telling " + ColorSet.emphasis + player.getInternal().getName() + ColorSet.good + ": " + ColorSet.emphasis + ato.toString());
        }
        if (ato instanceof FormattedTextTag) {
            player.getInternal().sendMessage(((FormattedTextTag) ato).getInternal());
        }
        else {
            player.getInternal().sendMessage(Denizen2Sponge.parseColor(ato.toString()));
        }
    }
}
