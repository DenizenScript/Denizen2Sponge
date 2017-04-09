package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.OfflinePlayerTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PardonCommand extends AbstractCommand {

    // <--[command]
    // @Name pardon
    // @Arguments <OfflinePlayerTag>/<IP>
    // @Short pardons a player.
    // @Updated 2017/04/08
    // @Group Player
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Pardons a player or IP, removing its ban.
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
        return "<OfflinePlayerTag>/<IP>";
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
        AbstractTagObject target = entry.getArgumentObject(queue, 0);
        if (target instanceof  OfflinePlayerTag) {
            GameProfile profile = ((OfflinePlayerTag) target).getInternal().getProfile();
            if (queue.shouldShowGood()) {
                queue.outGood("Pardoning player " + ColorSet.emphasis + profile.getName().get() + ColorSet.good + "!");
            }
            Sponge.getServiceManager().provide(BanService.class).get().pardon(profile);
        }
        else {
            try {
                InetAddress address = InetAddress.getByName(target.toString());
                if (queue.shouldShowGood()) {
                    queue.outGood("Pardoning IP " + ColorSet.emphasis + address.getHostName() + ColorSet.good + "!");
                }
                Sponge.getServiceManager().provide(BanService.class).get().pardon(address);
            } catch (UnknownHostException e) {
                queue.handleError(entry, "Invalid IP address provided!");
            }
        }
    }
}
