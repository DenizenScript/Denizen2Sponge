package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.utilities.AITaskHelper;
import org.spongepowered.api.entity.living.Agent;

import java.util.HashMap;

public class AddAITaskCommand extends AbstractCommand {

    // <--[explanation]
    // @Since 0.5.5
    // @Name AI Goal Types
    // @Group Useful Lists
    // @Description
    // A list of all default AI goal types can be found here:
    // <@link url https://jd.spongepowered.org/7.1.0-SNAPSHOT/org/spongepowered/api/entity/ai/GoalTypes.html>AI goal types list<@/link>
    // These can be used with the <@link command addaitask>addaitask<@/link> and <@link command removeaitasks>removeaitasks<@/link> commands.
    // Keep in mind that the goal type "normal" is available for most living entities,
    // while the "target" goal type is mainly for combat related ones.
    // -->

    // <--[explanation]
    // @Since 0.5.5
    // @Name AI Task Types
    // @Group Useful Lists
    // @Description
    // The default AI task types are "attack_living", "avoid_entity", "find_target",
    // "look_idle", "range", "run_around", "swim", "wander", and "watch_closest".
    // These can be used with the <@link command addaitask>addaitask<@/link> and <@link command removeaitasks>removeaitasks<@/link> commands.
    // -->

    // <--[command]
    // @Since 0.5.5
    // @Name addaitask
    // @Arguments <entity> <task type>
    // @Short adds an AI task to an entity's goal.
    // @Updated 2018/06/15
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named priority (IntegerTag) Sets the priority to run this task.
    // @Named goal (TextTag) Sets to which goal will the task be added.
    // @Named speed (NumberTag) Sets the speed parameter for task types
    // 'attack_living', 'range', 'run_around' and 'wander'.
    // @Named memory (BooleanTag) Sets the memory parameter for task type 'attack_living'.
    // @Named close_speed (NumberTag) Sets the close_speed parameter for task type 'avoid_entity'.
    // @Named far_speed (NumberTag) Sets the far_speed parameter for task type 'avoid_entity'.
    // @Named distance (NumberTag) Sets the distance parameter for task types
    // 'avoid_entity', 'range' and 'watch_closest'.
    // @Named chance (IntegerTag/NumberTag) Sets the chance parameter for task types 'find_target'
    // (IntegerTag), 'swim' (NumberTag), 'wander' (IntegerTag) and 'watch_closest' (NumberTag).
    // @Named target (EntityTypeTag) Sets the target parameter for task types
    // 'find_target' and 'watch_closest'.
    // @Named delay (DurationTag) Sets the delay parameter for task type 'range'.
    // @Description
    // Adds an AI task to an entity's goal. Priority defaults to 0. The lower the priority is,
    // the sooner the task will run in the goal. The default goal is 'normal'.
    // Note: adding a task won't overwrite existing tasks of the same type.
    // Related information: <@link explanation AI Goal Types>AI goal types<@/link>.
    // Related information: <@link explanation AI Task Types>AI task types<@/link>.
    // Related commands: <@link command removeaitasks>removeaitasks<@/link>.
    // @Example
    // # Makes the zombie in front of the player sink in water.
    // - addaitask <player.target_entities[type:zombie].get[1]> swim --chance 0.0 --priority -10 --goal normal
    // -->

    @Override
    public String getName() {
        return "addaitask";
    }

    @Override
    public String getArguments() {
        return "<entity> <task type>";
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
        EntityTag entityTag = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String type = entry.getArgumentObject(queue, 1).toString();
        try {
            Agent agent = (Agent) entityTag.getInternal();
            HashMap<String, AbstractTagObject> properties = new HashMap<>();
            for (String key : entry.namedArgs.keySet()) {
                properties.put(key, entry.getNamedArgumentObject(queue, key));
            }
            AITaskHelper.giveAITask(queue, agent, type, properties);
            if (queue.shouldShowGood()) {
                queue.outGood("Added task of type '" + ColorSet.emphasis + type
                        + ColorSet.good + "' to entity '" + ColorSet.emphasis + entityTag.debug()
                        + ColorSet.good + "'!");
            }
        }
        catch (ClassCastException e) {
            queue.handleError(entry, "This entity doesn't support AI tasks!");
        }
    }
}
