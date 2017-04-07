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
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.net.InetAddress;
import java.time.Instant;

public class BanCommand extends AbstractCommand {

    // <--[command]
    // @Name ban
    // @Arguments <player> <duration> [reason]
    // @Short bans a player for the specified duration.
    // @Updated 2017/04/07
    // @Group Player
    // @Minimum 2
    // @Maximum 3
    // @Named ban_ip (BooleanTag) Sets whether this will be an IP ban or not.
    // @Named source (TextTag) Sets the source of this ban.
    // @Description
    // Bans a player for the specified duration, or permanently if the duration is 0.
    // Optionally specify a reason. Note that you can't ban the IP of an offline player,
    // and you can't ban an IP permanently.
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
        return "<player> <duration> [reason]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        AbstractTagObject atoPlayer = entry.getArgumentObject(queue, 0);
        Ban.Builder build = Ban.builder();
        Text reason = null;
        if (entry.arguments.size() > 2) {
            AbstractTagObject atoReason = entry.getArgumentObject(queue, 2);
            if (atoReason instanceof FormattedTextTag) {
                reason = ((FormattedTextTag) atoReason).getInternal();
            }
            else {
                reason = Denizen2Sponge.parseColor(atoReason.toString());
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
        GameProfile profile;
        InetAddress adress = null;
        if (atoPlayer instanceof PlayerTag) {
            Player player = ((PlayerTag) atoPlayer).getInternal();
            profile = player.getProfile();
            adress =  player.getConnection().getAddress().getAddress();
            if (reason != null) {
                player.kick(reason);
            }
            else {
                player.kick();
            }
        }
        else {
            profile = ((OfflinePlayerTag) atoPlayer).getInternal().getProfile();
        }
        double duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1)).getInternal();
        Ban ban;
        if (entry.namedArgs.containsKey("ban_ip")) {
            Boolean banIp = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "ban_ip")).getInternal();
            if (banIp) {
                if (duration > 0) {
                    build.startDate(Instant.now()).expirationDate(Instant.now().plusSeconds((long) duration));
                    build.type(BanTypes.IP);
                    if (adress != null) {
                        build.address(adress);
                    }
                    else {
                        queue.handleError(entry, "You can't ban an offline player by IP!");
                        return;
                    }
                    ban = build.build();
                    if (queue.shouldShowGood()) {
                        queue.outGood("Banning IP of" + ColorSet.emphasis + atoPlayer.debug() + ColorSet.good
                                + " for " + ColorSet.emphasis + duration + ColorSet.good + " seconds!");
                    }
                }
                else {
                    queue.handleError(entry, "You can't ban an IP permanently!");
                    return;
                }
            }
            else {
                if (duration > 0) {
                    build.startDate(Instant.now()).expirationDate(Instant.now().plusSeconds((long) duration));
                    build.type(BanTypes.PROFILE);
                    build.profile(profile);
                    ban = build.build();
                    if (queue.shouldShowGood()) {
                        queue.outGood("Banning profile of " + ColorSet.emphasis + atoPlayer.debug() + ColorSet.good
                                + " for " + ColorSet.emphasis + duration + ColorSet.good + " seconds!");
                    }
                }
                else {
                    if (reason != null) {
                        ban = Ban.of(profile, reason);
                    }
                    else {
                        ban = Ban.of(profile);
                    }
                    if (queue.shouldShowGood()) {
                        queue.outGood("Banning profile of " + ColorSet.emphasis + atoPlayer.debug() + ColorSet.good
                                + " permanently!");
                    }
                }
            }
        }
        else {
            if (duration > 0) {
                build.startDate(Instant.now()).expirationDate(Instant.now().plusSeconds((long) duration));
                build.type(BanTypes.PROFILE);
                build.profile(profile);
                ban = build.build();
                if (queue.shouldShowGood()) {
                    queue.outGood("Banning profile of " + ColorSet.emphasis + atoPlayer.debug() + ColorSet.good
                            + " for " + ColorSet.emphasis + duration + ColorSet.good + " seconds!");
                }
            }
            else {
                if (reason != null) {
                    ban = Ban.of(profile, reason);
                }
                else {
                    ban = Ban.of(profile);
                }
                if (queue.shouldShowGood()) {
                    queue.outGood("Banning profile of " + ColorSet.emphasis + atoPlayer.debug() + ColorSet.good
                            + " permanently!");
                }
            }
        }
        Sponge.getServiceManager().provide(BanService.class).get().addBan(ban);
    }
}
