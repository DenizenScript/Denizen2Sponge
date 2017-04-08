package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.OfflinePlayerTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

public class BanCommand extends AbstractCommand {

    // <--[command]
    // @Name ban
    // @Arguments [duration] [reason]
    // @Short bans a player.
    // @Updated 2017/04/08
    // @Group Player
    // @Minimum 0
    // @Maximum 2
    // @Named player (PlayerTag) Sets the player that will be banned.
    // @Named ip (TextTag) Sets the IP that will be banned.
    // @Named source (TextTag) Sets the source of this ban.
    // @Description
    // Bans a player for the specified duration, or permanently if no duration
    // is specified. Optionally specify the reason and source of the ban.
    // Note that you must specify either a player or an IP for this to work.
    // @Example
    // # This example bans the current player from the server for 1 hour.
    // - ban <player> 1h
    // -->

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getArguments() {
        return "[duration] [reason]";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        Ban.Builder build = Ban.builder().startDate(Instant.now());
        if (entry.namedArgs.containsKey("source")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "source");
            if (ato instanceof FormattedTextTag) {
                build.source(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.source(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        DurationTag duration = null;
        if (entry.arguments.size() > 0) {
            duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
            build.expirationDate(Instant.now().plusSeconds((long) duration.getInternal()));
        }
        Text reason = null;
        if (entry.arguments.size() > 1) {
            AbstractTagObject ato = entry.getArgumentObject(queue, 1);
            if (ato instanceof FormattedTextTag) {
                reason = ((FormattedTextTag) ato).getInternal();
            }
            else {
                reason = Denizen2Sponge.parseColor(ato.toString());
            }
            build.reason(reason);
        }
        if (entry.namedArgs.containsKey("player")) {
            AbstractTagObject atoPlayer = entry.getNamedArgumentObject(queue, "player");
            GameProfile profile;
            if (atoPlayer instanceof PlayerTag) {
                profile = ((PlayerTag) atoPlayer).getInternal().getProfile();
            }
            else {
                profile = OfflinePlayerTag.getFor(queue.error, atoPlayer).getInternal().getProfile();
            }
            build.type(BanTypes.PROFILE).profile(profile);
            if (queue.shouldShowGood()) {
                queue.outGood("Banning player " + ColorSet.emphasis + profile.getName().get() +
                        ((duration == null) ?
                            (ColorSet.good + " permanently") :
                            (ColorSet.good + " for " + ColorSet.emphasis + duration.debug() + ColorSet.good + " seconds")) +
                        ((reason == null) ?
                            (ColorSet.good + "!") :
                            (ColorSet.good + " with reason " + ColorSet.emphasis + reason.toPlain() + ColorSet.good + "!")));
            }
        }
        else if (entry.namedArgs.containsKey("ip")) {
            try {
                InetAddress address = InetAddress.getByName(entry.getNamedArgumentObject(queue, "ip").toString());
                build.type(BanTypes.IP).address(address);
                if (queue.shouldShowGood()) {
                    queue.outGood("Banning IP " + ColorSet.emphasis + address.getHostName() +
                            ((duration == null) ?
                                    (ColorSet.good + " permanently") :
                                    (ColorSet.good + " for " + ColorSet.emphasis + duration.debug() + ColorSet.good + " seconds")) +
                            ((reason == null) ?
                                    (ColorSet.good + "!") :
                                    (ColorSet.good + " with reason " + ColorSet.emphasis + reason.toPlain() + ColorSet.good + "!")));
                }
            } catch (UnknownHostException e) {
                queue.handleError(entry, "Invalid IP address provided!");
                return;
            }
        }
        else {
            queue.handleError(entry, "You must specify either a player or an IP!");
            return;
        }
        Sponge.getServiceManager().provide(BanService.class).get().addBan(build.build());
    }
}
