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

public class TabListCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name tablist
    // @Arguments <player> <header> <footer>
    // @Short sets the header or footer of a player's tablist.
    // @Updated 2018/02/04
    // @Group Player
    // @Minimum 3
    // @Maximum 3
    // @Description
    // Sets the header or footer of a player's tablist.
    // @Example
    // # This example changes both the header and footer of the player's tablist.
    // - tablist <player> "Hello!" "Bye!"
    // @Example
    // # This example only changes the header of the player's tablist.
    // - tablist <player> "I'm a header!" <player.tablist_footer>
    // @Example
    // # This example only changes the footer of the player's tablist.
    // - tablist <player> <player.tablist_header> "I'm a footer!"
    // -->

    @Override
    public String getName() {
        return "tablist";
    }

    @Override
    public String getArguments() {
        return "<player> <header> <footer>";
    }

    @Override
    public int getMinimumArguments() {
        return 3;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        AbstractTagObject ato1 = entry.getArgumentObject(queue, 1);
        Text header;
        if (ato1 instanceof FormattedTextTag) {
            header = ((FormattedTextTag) ato1).getInternal();
        }
        else {
            header = Denizen2Sponge.parseColor(ato1.toString());
        }
        AbstractTagObject ato2 = entry.getArgumentObject(queue, 2);
        Text footer;
        if (ato2 instanceof FormattedTextTag) {
             footer = ((FormattedTextTag) ato2).getInternal();
        }
        else {
             footer = Denizen2Sponge.parseColor(ato2.toString());
        }
        player.getOnline(queue.error).getTabList().setHeaderAndFooter(header, footer);
        if (queue.shouldShowGood()) {
            queue.outGood("Setting the tablist header to '" + ColorSet.emphasis + ato1.debug()
                    + ColorSet.good + "' and footer to '" + ColorSet.emphasis + ato2.debug()
                    + ColorSet.good + "' for player '" + ColorSet.emphasis + player.debug());
        }
    }
}
