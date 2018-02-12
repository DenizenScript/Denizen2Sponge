package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.advancement.Advancement;

public class AdvancementCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name advancement
    // @Arguments <player> <id> <state>
    // @Short manages the state of a player's advancement.
    // @Updated 2018/02/06
    // @Group Player
    // @Minimum 3
    // @Maximum 3
    // @Description
    // Manages the state of a player's advancement. Set the state
    // to true to grant the advancement, and to false to revoke it.
    // @Example
    // # This example grants the advancement "iron_man" to the player.
    // - advancement <player> iron_man true
    // @Example
    // # This example revokes the advancement "legend" from the player.
    // - advancement <player> legend false
    // -->

    @Override
    public String getName() {
        return "advancement";
    }

    @Override
    public String getArguments() {
        return "<player> <id> <state>";
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
        String id = entry.getArgumentObject(queue, 1).toString();
        Advancement advancement = (Advancement) Utilities.getTypeWithDefaultPrefix(Advancement.class, id);
        if (advancement == null) {
            Debug.error("There's no registered advancement that matches the specified id!");
            return;
        }
        BooleanTag state = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
        if (state.getInternal()) {
            player.getOnline(queue.error).getProgress(advancement).grant();
            if (queue.shouldShowGood()) {
                queue.outGood("Granting advancement '" + ColorSet.emphasis + advancement.getId()
                        + ColorSet.good + "' to player '" + ColorSet.emphasis + player.debug()
                        + ColorSet.good + "'!");
            }
        }
        else {
            player.getOnline(queue.error).getProgress(advancement).revoke();
            if (queue.shouldShowGood()) {
                queue.outGood("Revoking advancement '" + ColorSet.emphasis + advancement.getId()
                        + ColorSet.good + "' from player '" + ColorSet.emphasis + player.debug()
                        + ColorSet.good + "'!");
            }
        }
    }
}
