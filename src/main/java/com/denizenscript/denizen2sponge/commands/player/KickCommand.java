package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;

public class KickCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name kick
    // @Arguments <player> [reason]
    // @Short kicks a player with the specified reason.
    // @Updated 2017/03/28
    // @Group Player
    // @Minimum 1
    // @Maximum 2
    // @Description
    // Kicks a player with the specified reason.
    // Related commands: <@link command ban>ban<@/link> and <@link command pardon>pardon<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example kicks the current player from the server given the reason "Spam".
    // - kick <player> "Spam"
    // -->

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getArguments() {
        return "<player> [reason]";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        if (entry.arguments.size() < 2) {
            if (queue.shouldShowGood()) {
                queue.outGood("Kicking " + ColorSet.emphasis + player.debug());
            }
            player.getOnline(queue.error).kick();
        }
        else {
            AbstractTagObject ato = entry.getArgumentObject(queue, 1);
            if (queue.shouldShowGood()) {
                queue.outGood("Kicking " + ColorSet.emphasis + player.debug()
                        + ColorSet.good + " with reason " + ColorSet.emphasis + ato.debug());
            }
            if (ato instanceof FormattedTextTag) {
                player.getOnline(queue.error).kick(((FormattedTextTag) ato).getInternal());
            }
            else {
                player.getOnline(queue.error).kick(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
    }
}
