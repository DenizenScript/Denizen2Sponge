package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.entity.living.Agent;

public class TargetCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name target
    // @Arguments <entity> <target entity>
    // @Short sets the target entity of an entity's AI.
    // @Updated 2018/01/16
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Sets the target entity of an entity's AI. This overrides the AI's target selector.
    // @Example
    // # This example makes an iron golem in front of the player attack him.
    // - target <player.target_entities[type:iron_golem].get[1]> <player>
    // -->

    @Override
    public String getName() {
        return "target";
    }

    @Override
    public String getArguments() {
        return "<entity> <target entity>";
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
        EntityTag target = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        ((Agent) ent.getInternal()).setTarget(target.getInternal());
        if (queue.shouldShowGood()) {
            queue.outGood("Entity '" + ColorSet.emphasis + ent.debug() + ColorSet.good
                    + "' is now targeting entity '" + ColorSet.emphasis + target.debug() + ColorSet.good + "'!");
        }
    }
}
