package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.text.Text;

public class GiveCommand extends AbstractCommand {

    // <--[command]
    // @Name give
    // @Arguments <player> <item>
    // @Short gives a player an item.
    // @Updated 2016/11/24
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Gives a player an item.
    // TODO: Explain more!
    // @Example
    // # This example gives the player a stone.
    // - give <player> stone
    // -->

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getArguments() {
        return "<player> <item>";
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
        ItemTag item = ItemTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (queue.shouldShowGood()) {
            queue.outGood("Giving " + ColorSet.emphasis + player.getInternal().getName() + ColorSet.good + ": " + ColorSet.emphasis + item.toString());
        }
        player.getInternal().getInventory().offer(item.getInternal()); // TODO: use return value?
    }
}
