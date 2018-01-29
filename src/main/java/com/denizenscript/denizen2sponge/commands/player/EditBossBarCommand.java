package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.BossBars;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.ServerBossBar;

import java.util.Optional;

public class EditBossBarCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name editbossbar
    // @Arguments <id> [title]
    // @Short edits a server BossBar.
    // @Updated 2018/01/29
    // @Group Player
    // @Minimum 1
    // @Maximum 2
    // @Named color (TextTag) Sets the color of this BossBar.
    // @Named create_fog (BooleanTag) Sets whether this BossBar should create fog.
    // @Named darken_sky (BooleanTag) Sets whether this BossBar should darken the sky.
    // @Named overlay (TextTag) Sets the overlay of this BossBar.
    // @Named percent (NumberTag) Sets the completion percent of this BossBar.
    // @Named play_music (BooleanTag) Sets whether this BossBar should play music.
    // @Named visible (BooleanTag) Sets whether this BossBar should be visible.
    // @Named add_players (ListTag) Sets the players that will be added to the BossBar.
    // @Named remove_players (ListTag) Sets the players that will be removed from the BossBar.
    // @Description
    // Edits the properties of the specified server BossBar.
    // You can also add or remove players to it.
    // Related commands: <@link command createbossbar>createbossbar<@/link> and <@link command removebossbar>removebossbar<@/link>.
    // @Example
    // # This example edits the BossBar with ID 'MyBossBar', changing its title to 'I'm watching!'.
    // - editbossbar MyBossBar "I'm watching!"
    // @Example
    // # This example edits the fancy BossBar with ID 'FancyBossBar', removing the player from it.
    // - editbossbar FancyBossBar --remove_players <player>
    // -->

    @Override
    public String getName() {
        return "editbossbar";
    }

    @Override
    public String getArguments() {
        return "<id> [title]";
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
        String id = entry.getArgumentObject(queue, 0).toString();
        if (!BossBars.CurrentBossBars.containsKey(id)) {
            queue.handleError(entry, "The BossBar with ID '" + id + "' doesn't exist!");
            return;
        }
        ServerBossBar bar = BossBars.CurrentBossBars.get(id);
        if (queue.shouldShowGood()) {
            queue.outGood("Editing BossBar with ID '" + ColorSet.emphasis + id + ColorSet.good + "'...");
        }
        if (entry.arguments.size() > 1) {
            AbstractTagObject ato = entry.getArgumentObject(queue, 1);
            if (ato instanceof FormattedTextTag) {
                bar.setName(((FormattedTextTag) ato).getInternal());
            }
            else {
                bar.setName(Denizen2Sponge.parseColor(ato.toString()));
            }
            if (queue.shouldShowGood()) {
                queue.outGood("Setting title of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + ato.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("color")) {
            String color = entry.getNamedArgumentObject(queue, "color").toString();
            Optional<BossBarColor> opt = Sponge.getRegistry().getType(BossBarColor.class, color);
            if (!opt.isPresent()) {
                queue.handleError(entry, "The color '" + color + "' is not valid!");
                return;
            }
            bar.setColor(opt.get());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting color of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + opt.get().getId() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("create_fog")) {
            BooleanTag fog = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "create_fog"));
            bar.setCreateFog(fog.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should create fog to '" + ColorSet.emphasis + fog.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("darken_sky")) {
            BooleanTag sky = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "darken_sky"));
            bar.setDarkenSky(sky.getInternal());
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
            bar.setOverlay(opt.get());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting overlay of BossBar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + opt.get().getId() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("percent")) {
            NumberTag percent = NumberTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "percent"));
            bar.setPercent((float) percent.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting percent of Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' to '" + ColorSet.emphasis + percent.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("play_music")) {
            BooleanTag music = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "play_music"));
            bar.setPlayEndBossMusic(music.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should play music to '" + ColorSet.emphasis + music.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("visible")) {
            BooleanTag visible = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "visible"));
            bar.setVisible(visible.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Setting whether Bossbar '" + ColorSet.emphasis + id + ColorSet.good
                        + "' should be visible to '" + ColorSet.emphasis + visible.debug() + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("add_players")) {
            ListTag players = ListTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "add_players"));
            for (AbstractTagObject player : players.getInternal()) {
                bar.addPlayer(PlayerTag.getFor(queue.error, player).getOnline(queue.error));
            }
            if (queue.shouldShowGood()) {
                queue.outGood("Adding players '" + ColorSet.emphasis + players.debug() + ColorSet.good
                        + "' to Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "'...");
            }
        }
        if (entry.namedArgs.containsKey("remove_players")) {
            ListTag players = ListTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "remove_players"));
            for (AbstractTagObject player : players.getInternal()) {
                bar.removePlayer(PlayerTag.getFor(queue.error, player).getOnline(queue.error));
            }
            if (queue.shouldShowGood()) {
                queue.outGood("Removing players '" + ColorSet.emphasis + players.debug() + ColorSet.good
                        + "' from Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "'...");
            }
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "' edited successfully!");
        }
    }
}
