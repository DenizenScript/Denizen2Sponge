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
    // @Arguments
    // @Short pardons a player.
    // @Updated 2017/04/07
    // @Group Player
    // @Minimum 0
    // @Maximum 0
    // @Named player (PlayerTag) Sets the player that will be pardoned.
    // @Named ip (TextTag) Sets the IP that will be pardoned.
    // @Description
    // Pardons a player or IP, removing its ban. Note that you must
    // specify either a player or an IP for this to work.
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
        return "";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        if (entry.namedArgs.containsKey("player")) {
            AbstractTagObject atoPlayer = entry.getNamedArgumentObject(queue, "player");
            GameProfile profile = OfflinePlayerTag.getFor(queue.error, atoPlayer).getInternal().getProfile();
            if (queue.shouldShowGood()) {
                queue.outGood("Pardoning player " + ColorSet.emphasis + profile.getName().get() + ColorSet.good + "!");
            }
            Sponge.getServiceManager().provide(BanService.class).get().pardon(profile);
        }
        else if (entry.namedArgs.containsKey("ip")) {
            try {
                InetAddress address = InetAddress.getByName(entry.getNamedArgumentObject(queue, "ip").toString());
                if (queue.shouldShowGood()) {
                    queue.outGood("Pardoning IP " + ColorSet.emphasis + address.getHostName() + ColorSet.good + "!");
                }
                Sponge.getServiceManager().provide(BanService.class).get().pardon(address);
            } catch (UnknownHostException e) {
                queue.handleError(entry, "Invalid IP address provided!");
            }
        }
        else {
            queue.handleError(entry, "You must specify either a player or an IP!");
        }
    }
}
