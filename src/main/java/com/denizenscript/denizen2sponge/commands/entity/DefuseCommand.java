package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.entity.explosive.FusedExplosive;

public class DefuseCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name defuse
    // @Arguments <entity>
    // @Short defuses an explosive entity.
    // @Updated 2018/01/10
    // @Group Entity
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Defuses an explosive entity. Inverse of <@link command prime>prime<@/link>.
    // @Example
    // # This example defuses the creeper the player is looking at.
    // - defuse <player.target_entities[type:creeper].get[1]>
    // -->

    @Override
    public String getName() {
        return "defuse";
    }

    @Override
    public String getArguments() {
        return "<entity>";
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
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        try {
            ((FusedExplosive) ent.getInternal()).defuse();
            if (queue.shouldShowGood()) {
                queue.outGood("Defusing entity: " + ColorSet.emphasis + ent.debug() + ColorSet.good + "!");
            }
        }
        catch (ClassCastException e) {
            queue.handleError("This entity is not an explosive, so it can't be defused!");
        }
        catch (IllegalStateException e) {
            queue.handleError("This explosive entity is not primed!");
        }
    }
}
