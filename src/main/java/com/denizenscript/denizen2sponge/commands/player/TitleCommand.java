package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.text.title.Title;

public class TitleCommand extends AbstractCommand {

    // <--[command]
    // @Name title
    // @Arguments <player> <title> [subtitle]
    // @Short sends a title to a player.
    // @Updated 2017/04/24
    // @Group Player
    // @Minimum 2
    // @Maximum 3
    // @Named action_bar (FormattedTextTag) Sets the message that will be shown to the player in the action bar.
    // @Named fade_in (DurationTag) Sets the fade in time.
    // @Named stay (DurationTag) Sets the stay time.
    // @Named fade_out (DurationTag) Sets the fade out time.
    // @Description
    // Sends a title to a player. Optionally specify fade in, stay and fade out times.
    // These times all default to 1 second.
    // @Example
    // # This example sends the title "hello there!" to the player.
    // - title <player> "hello there!"
    // -->

    @Override
    public String getName() {
        return "title";
    }

    @Override
    public String getArguments() {
        return "<player> <title> [subtitle]";
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
        Title.Builder build = Title.builder();
        AbstractTagObject title = entry.getArgumentObject(queue, 1);
        if (title instanceof FormattedTextTag) {
            build.title(((FormattedTextTag) title).getInternal());
        }
        else {
            build.title(Denizen2Sponge.parseColor(title.toString()));
        }
        if (entry.arguments.size() > 2) {
            AbstractTagObject subtitle = entry.getArgumentObject(queue, 2);
            if (subtitle instanceof FormattedTextTag) {
                build.subtitle(((FormattedTextTag) subtitle).getInternal());
            }
            else {
                build.subtitle(Denizen2Sponge.parseColor(subtitle.toString()));
            }
        }
        if (entry.namedArgs.containsKey("action_bar")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "action_bar");
            if (ato instanceof FormattedTextTag) {
                build.actionBar(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.actionBar(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        if (entry.namedArgs.containsKey("fade_in")) {
            DurationTag dur = DurationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "fade_in"));
            build.fadeIn((int) (dur.getInternal() * 20));
        }
        else {
            build.fadeIn(20);
        }
        if (entry.namedArgs.containsKey("stay")) {
            DurationTag dur = DurationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "stay"));
            build.stay((int) (dur.getInternal() * 20));
        }
        else {
            build.stay(20);
        }
        if (entry.namedArgs.containsKey("fade_out")) {
            DurationTag dur = DurationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "fade_out"));
            build.fadeOut((int) (dur.getInternal() * 20));
        }
        else {
            build.fadeOut(20);
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Showing title '" + (build.getTitle().isPresent() ?
                    ColorSet.emphasis + build.getTitle().get().toPlain() + ColorSet.good : "") +
                    "' and subtitle '" + (build.getSubtitle().isPresent() ?
                    ColorSet.emphasis + build.getSubtitle().get().toPlain() + ColorSet.good : "") +
                    "' to player:" + ColorSet.emphasis + player.debug() + ColorSet.good + "!");
        }
        player.getOnline(queue.error).sendTitle(build.build());
    }
}
