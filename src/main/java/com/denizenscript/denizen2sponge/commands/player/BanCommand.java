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
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;

import java.time.Instant;

public class BanCommand extends AbstractCommand {

    // <--[command]
    // @Name ban
    // @Arguments <player> <duration> [reason]
    // @Short bans a player for the specified duration.
    // @Updated 2017/04/06
    // @Group Player
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Bans a player for the specified duration. Optionally specify a reason.
    // TODO: Explain more!
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
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        DurationTag duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Ban.Builder build = Ban.builder().startDate(Instant.now()).expirationDate(Instant.now().plusSeconds((long) duration.getInternal()));
        if (entry.namedArgs.containsKey("ban_ip")) {
            BooleanTag bt = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "ban_ip"));
            if (bt.getInternal()) {
                build.type(BanTypes.IP);
                build.address(player.getInternal().getConnection().getAddress().getAddress());
            }
            else {
                build.type(BanTypes.PROFILE);
                build.profile(player.getInternal().getProfile());
            }
        }
        else {
            build.type(BanTypes.PROFILE);
            build.profile(player.getInternal().getProfile());
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
        if (entry.arguments.size() > 2) {
            AbstractTagObject ato = entry.getArgumentObject(queue, 2);
            if (ato instanceof FormattedTextTag) {
                Text reason = ((FormattedTextTag) ato).getInternal();
                build.reason(reason);
                player.getInternal().kick(reason);
            }
            else {
                Text reason = Denizen2Sponge.parseColor(ato.toString());
                build.reason(reason);
                player.getInternal().kick(reason);
            }
        }
        else {
            player.getInternal().kick();
        }
        Sponge.getServiceManager().provide(BanService.class).get().addBan(build.build());
        if (queue.shouldShowGood()) {
            queue.outGood("Banning " + ColorSet.emphasis + player.debug() + ColorSet.good
                    + " for " + ColorSet.emphasis + duration.debug() + ColorSet.good + " seconds!");
        }
    }
}
