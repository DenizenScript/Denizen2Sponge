package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
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

import java.time.Instant;

public class BanCommand extends AbstractCommand {

    // <--[command]
    // @Name ban
    // @Arguments <player> [duration]
    // @Short bans a player for the specified duration.
    // @Updated 2017/04/07
    // @Group Player
    // @Minimum 1
    // @Maximum 2
    // @Named reason (TextTag) Sets the reason of this ban.
    // @Named source (TextTag) Sets the source of this ban.
    // @Description
    // Bans a player for the specified duration, or permanently if no duration
    // is specified. Optionally specify the reason and source of the ban.
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
        return "<player> [duration]";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        Ban.Builder build = Ban.builder().startDate(Instant.now()).type(BanTypes.PROFILE);
        Text reason = Text.of("Banned by an operator.");
        if (entry.namedArgs.containsKey("reason")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "reason");
            if (ato instanceof FormattedTextTag) {
                reason = ((FormattedTextTag) ato).getInternal();
            }
            else {
                reason = Denizen2Sponge.parseColor(ato.toString());
            }
            build.reason(reason);
        }
        GameProfile profile;
        AbstractTagObject atoPlayer = entry.getArgumentObject(queue, 0);
        if (atoPlayer instanceof PlayerTag) {
            Player player = ((PlayerTag) atoPlayer).getInternal();
            player.kick(reason);
            profile = player.getProfile();
        }
        else {
            User user = OfflinePlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0)).getInternal();
            if (user.isOnline()) {
                user.getPlayer().get().kick(reason);
            }
            profile = user.getProfile();
        }
        build.profile(profile);
        if (entry.namedArgs.containsKey("source")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "source");
            if (ato instanceof FormattedTextTag) {
                build.source(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.source(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        if (entry.arguments.size() > 1) {
            DurationTag duration = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
            build.expirationDate(Instant.now().plusSeconds((long) duration.getInternal()));
            if (queue.shouldShowGood()) {
                queue.outGood("Banning " + ColorSet.emphasis + profile.getName().get() + ColorSet.good
                        + " for " + ColorSet.emphasis + duration.debug() + ColorSet.good + " seconds!");
            }
        }
        else {
            if (queue.shouldShowGood()) {
                queue.outGood("Banning " + ColorSet.emphasis + profile.getName().get() + ColorSet.good
                        + " permanently!");
            }
        }
        Sponge.getServiceManager().provide(BanService.class).get().addBan(build.build());
    }
}
