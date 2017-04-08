package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.OfflinePlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;

public class PardonCommand extends AbstractCommand {

    // <--[command]
    // @Name pardon
    // @Arguments <player>
    // @Short pardons a player.
    // @Updated 2017/04/07
    // @Group Player
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Pardons a player, removing its ban.
    // @Example
    // # This example pardons the current player.
    // - pardon <player>
    // -->

    @Override
    public String getName() {
        return "pardon";
    }

    @Override
    public String getArguments() {
        return "<player>";
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
        GameProfile profile = OfflinePlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0)).getInternal().getProfile();
        if (queue.shouldShowGood()) {
            queue.outGood("Pardoning " + ColorSet.emphasis + profile.getName().get() + ColorSet.good
                    + "!");
        }
        Sponge.getServiceManager().provide(BanService.class).get().pardon(profile);
    }
}
