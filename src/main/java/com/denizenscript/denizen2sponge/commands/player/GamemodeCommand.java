package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;

import java.util.Optional;

public class GamemodeCommand extends AbstractCommand {

    // <--[command]
    // @Name gamemode
    // @Arguments <player> <gamemode>
    // @Short sets gamemode of a player.
    // @Updated 2017/09/10
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Sets the gamemode of a player. The standard set of gamemodes is
    // 'adventure', 'survival', 'creative', and 'spectator'.
    // @Example
    // # Sets the gamemode to 'creative' for player 'Joe'.
    // - gamemode Joe creative
    // -->

    @Override
    public String getName() {
        return "gamemode";
    }

    @Override
    public String getArguments() {
        return "<player> <gamemode>";
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
        PlayerTag player = (PlayerTag) entry.getArgumentObject(queue, 0);
        String gamemode = entry.getArgumentObject(queue, 1).toString();
        Optional<GameMode> type = Sponge.getRegistry().getType(GameMode.class, gamemode);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid gamemode: '" + gamemode + "'!");
            return;
        }
        player.getInternal().offer(Keys.GAME_MODE, type.get());
        if (queue.shouldShowGood()) {
            queue.outGood("Set gamemode of player '" + ColorSet.emphasis + player.debug()
                    + ColorSet.good + "' to: " + ColorSet.emphasis + type.get().getName() + ColorSet.good + "!");
        }
    }
}
