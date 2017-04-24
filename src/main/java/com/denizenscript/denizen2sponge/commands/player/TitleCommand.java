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
    // @Arguments <player>
    // @Short sends a title to a player.
    // @Updated 2017/04/24
    // @Group Player
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Sends a title to a player. Optionally specify dafe in, stay and fade out times.
    // These times all default to 1 second.
    // @Example
    // # This example tells the current player the message "hello there!" in the chat box.
    // - tell <player> "hello there!"
    // -->

    @Override
    public String getName() {
        return "title";
    }

    @Override
    public String getArguments() {
        return "<player>";
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
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Title.Builder build = Title.builder();
        if (entry.namedArgs.containsKey("title")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "title");
            if (ato instanceof FormattedTextTag) {
                build.title(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.title(Denizen2Sponge.parseColor(ato.toString()));
            }
        }
        if (entry.namedArgs.containsKey("subtitle")) {
            AbstractTagObject ato = entry.getNamedArgumentObject(queue, "subtitle");
            if (ato instanceof FormattedTextTag) {
                build.subtitle(((FormattedTextTag) ato).getInternal());
            }
            else {
                build.subtitle(Denizen2Sponge.parseColor(ato.toString()));
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
        player.getInternal().sendTitle(build.build());
    }
}
