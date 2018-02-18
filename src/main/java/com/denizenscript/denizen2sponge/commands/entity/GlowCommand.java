package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;

public class GlowCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name glow
    // @Arguments <entity> <state>
    // @Short manages the glowing outline of an entity.
    // @Updated 2018/02/18
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Manages the glowing outline of an entity.
    // TODO: Research how to add colored outlines.
    // @Example
    // # This example makes the player glow.
    // - glow <player> true
    // -->

    @Override
    public String getName() {
        return "glow";
    }

    @Override
    public String getArguments() {
        return "<entity> <state>";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        BooleanTag bt = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        ent.getInternal().offer(Keys.GLOWING, bt.getInternal());
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (bt.getInternal() ? "Enabling" : "Disabling") + ColorSet.good
                    + " the glowing outline of entity '" + ColorSet.emphasis + ent.debug() + ColorSet.good + "'!");
        }
    }
}
