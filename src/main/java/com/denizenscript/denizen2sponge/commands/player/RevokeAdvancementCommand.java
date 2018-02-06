package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.advancement.Advancement;

public class RevokeAdvancementCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name revokeadvancement
    // @Arguments <player> <advancement id>
    // @Short revokes an advancement from a player.
    // @Updated 2018/02/06
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Revokes an advancement from a player.
    // @Example
    // # This example revokes the advancement "iron_man" from the player.
    // - revokeadvancement <player> iron_man
    // -->

    @Override
    public String getName() {
        return "revokeadvancement";
    }

    @Override
    public String getArguments() {
        return "<player> <advancement id>";
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
        String id = entry.getArgumentObject(queue, 1).toString();
        Advancement advancement = (Advancement) Utilities.getTypeWithDefaultPrefix(Advancement.class, id);
        if (advancement == null) {
            Debug.error("There's no registered advancement that matches the specified id!");
            return;
        }
        player.getOnline(queue.error).getProgress(advancement).revoke();
        if (queue.shouldShowGood()) {
            queue.outGood("Revoking advancement '" + ColorSet.emphasis + advancement.getId()
                    + ColorSet.good + "' from player '" + ColorSet.emphasis + player.debug()
                    + ColorSet.good + "'!");
        }
    }
}
