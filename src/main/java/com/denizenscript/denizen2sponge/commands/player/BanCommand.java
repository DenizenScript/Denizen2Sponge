package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
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
    // @Since 0.3.0
    // @Name ban
    // @Arguments <PlayerTag>/<IP> [duration] [reason]
    // @Short bans a player.
    // @Updated 2017/04/08
    // @Group Player
    // @Minimum 1
    // @Maximum 3
    // @Named source (TextTag) Sets the source of this ban.
    // @Description
    // Bans a player or IP for the specified duration, or permanently if no duration
    // is specified. Optionally specify the reason and source of the ban.
    // Note that this command will not automatically kick the player from the server.
    // Related commands: <@link command pardon>pardon<@/link> and <@link command kick>kick<@/link>.
    // @Example
    // # This example bans the current player for 1 hour.
    // - ban <player> 1h
    // -->

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getArguments() {
        return "<PlayerTag>/<IP> [duration] [reason]";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        Ban.Builder build = Ban.builder().startDate(Instant.now());
        DurationTag duration = null;
        if (entry.arguments.size() > 1) {
            duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
            build.expirationDate(Instant.now().plusSeconds((long) duration.getInternal()));
        }
        Text reason = null;
        if (entry.arguments.size() > 2) {
            AbstractTagObject ato = entry.getArgumentObject(queue, 2);
            if (ato instanceof FormattedTextTag) {
                reason = ((FormattedTextTag) ato).getInternal();
            }
            else {
                reason = Denizen2Sponge.parseColor(ato.toString());
            }
            build.reason(reason);
        }
        if (entry.namedArgs.containsKey("source")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "source");
            if (ato instanceof FormattedTextTag) {
                build.source(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.source(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        AbstractTagObject target = entry.getArgumentObject(queue, 0);
        if (target instanceof EntityTag) {
            target = PlayerTag.getFrom(queue.error, (EntityTag) target);
        }
        if (target instanceof PlayerTag) {
            GameProfile profile = ((PlayerTag) target).getInternal().getProfile();
            build.type(BanTypes.PROFILE).profile(profile);
            if (queue.shouldShowGood()) {
                queue.outGood("Banning player " + ColorSet.emphasis + profile.getName().orElse("<NameError>") +
                        ((duration == null) ?
                                (ColorSet.good + " permanently") :
                                (ColorSet.good + " for " + ColorSet.emphasis + duration.debug() + ColorSet.good + " seconds")) +
                        ((reason == null) ?
                                (ColorSet.good + "!") :
                                (ColorSet.good + " with reason " + ColorSet.emphasis + reason.toPlain() + ColorSet.good + "!")));
            }
        }
        else {
            try {
                InetAddress address = InetAddress.getByName(target.toString());
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
            }
            catch (UnknownHostException e) {
                queue.handleError(entry, "Invalid IP address provided!");
                return;
            }
        }
        Sponge.getServiceManager().provide(BanService.class).orElseThrow(() -> {
            queue.error.run("BanService is missing!");
            return new RuntimeException("BanService is missing!");
        }).addBan(build.build());
    }
}
