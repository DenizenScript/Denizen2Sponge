package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.BossBars;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.*;

import java.util.Optional;

public class CreateBossBarCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name createbossbar
    // @Arguments <id> <title> [player list]
    // @Short creates a server BossBar.
    // @Updated 2018/01/29
    // @Group Player
    // @Minimum 2
    // @Maximum 3
    // @Named color (TextTag) Sets the color of this BossBar.
    // @Named create_fog (BooleanTag) Sets whether this BossBar should create fog.
    // @Named darken_sky (BooleanTag) Sets whether this BossBar should darken the sky.
    // @Named overlay (TextTag) Sets the overlay of this BossBar.
    // @Named percent (NumberTag) Sets the completion percent of this BossBar.
    // @Named play_music (BooleanTag) Sets whether this BossBar should play music.
    // @Named visible (BooleanTag) Sets whether this BossBar should be visible.
    // @Description
    // Creates a server BossBar with the given ID and title. You can also specify
    // a list of players to automatically add them to the bar once it's created, or add
    // then yourself later on.
    // Related commands: <@link command editbossbar>editbossbar<@/link> and <@link command removebossbar>removebossbar<@/link>.
    // @Example
    // # This example creates a BossBar with ID 'MyBossBar' and title 'Hello folks!' for all online players.
    // - createbossbar MyBossBar "Hello folks!" <server.online_players>
    // @Example
    // # This example creates a fancy BossBar with ID 'FancyBossBar' and title 'Look at me!' for the current player.
    // - createbossbar FancyBossBar "Look at me!" <player> --overlay NOTCHED_12 --color PURPLE
    // -->

    @Override
    public String getName() {
        return "createbossbar";
    }

    @Override
    public String getArguments() {
        return "<id> <title> [player list]";
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
        String id = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 0).toString());
        if (BossBars.CurrentBossBars.containsKey(id)) {
            queue.handleError(entry, "A BossBar with ID '" + id + "' already exists!");
            return;
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Creating BossBar with ID '" + ColorSet.emphasis + id + ColorSet.good + "'...");
        }
        ServerBossBar.Builder builder = ServerBossBar.builder();
        AbstractTagObject ato = entry.getArgumentObject(queue, 1);
        if (ato instanceof FormattedTextTag) {
            builder.name(((FormattedTextTag) ato).getInternal());
        }
        else {
            builder.name(Denizen2Sponge.parseColor(ato.toString()));
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Setting title of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                    + "' to '" + ColorSet.emphasis + ato.debug() + ColorSet.good + "'...");
        }
        if (entry.namedArgs.containsKey("color")) {
            String color = entry.getNamedArgumentObject(queue, "color").toString();
            Optional<BossBarColor> opt = Sponge.getRegistry().getType(BossBarColor.class, color);
            if (!opt.isPresent()) {
                queue.handleError(entry, "The color '" + color + "' is not valid!");
                return;
            }
            builder.color(opt.get());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting color of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + opt.get().getId() + ColorSet.good + "'...");
            }
        }
        else {
            builder.color(BossBarColors.WHITE);
            if (queue.shouldShowGood()) {
                queue.outGood("No color specified for BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "', defaulting to '" + ColorSet.emphasis + "white" + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("create_fog")) {
            BooleanTag fog = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "create_fog"));
            builder.createFog(fog.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should create fog to '" + ColorSet.emphasis + fog.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("darken_sky")) {
            BooleanTag sky = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "darken_sky"));
            builder.darkenSky(sky.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should darken the sky to '" + ColorSet.emphasis + sky.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("overlay")) {
            String overlay = entry.getNamedArgumentObject(queue, "overlay").toString();
            Optional<BossBarOverlay> opt = Sponge.getRegistry().getType(BossBarOverlay.class, overlay);
            if (!opt.isPresent()) {
                queue.handleError(entry, "The overlay '" + overlay + "' is not valid!");
                return;
            }
            builder.overlay(opt.get());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting overlay of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + opt.get().getId() + ColorSet.good + "'...");
            }
        }
        else {
            builder.overlay(BossBarOverlays.PROGRESS);
            if (queue.shouldShowGood()) {
                queue.outGood("No overlay specified for BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "', defaulting to '" + ColorSet.emphasis + "progress" + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("percent")) {
            NumberTag percent = NumberTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "percent"));
            builder.percent((float) percent.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting percent of Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + percent.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("play_music")) {
            BooleanTag music = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "play_music"));
            builder.playEndBossMusic(music.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should play music to '" + ColorSet.emphasis + music.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("visible")) {
            BooleanTag visible = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "visible"));
            builder.visible(visible.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should be visible to '" + ColorSet.emphasis + visible.debug() + ColorSet.good + "'...");
            }
        }
        ServerBossBar bar = builder.build();
        if (entry.arguments.size() > 2) {
            ListTag players = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            for (AbstractTagObject player : players.getInternal()) {
                bar.addPlayer(PlayerTag.getFor(queue.error, player).getOnline(queue.error));
            }
            if (queue.shouldShowGood()) {
                queue.outGood("Adding players '" + ColorSet.emphasis + players.debug() + ColorSet.good
                        + "' to Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "'...");
            }
        }
        BossBars.CurrentBossBars.put(id, bar);
        if (queue.shouldShowGood()) {
            queue.outGood("Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "' created successfully!");
        }
    }
}
