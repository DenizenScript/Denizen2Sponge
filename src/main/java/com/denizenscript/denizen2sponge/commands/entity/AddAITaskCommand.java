package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ai.GoalType;
import org.spongepowered.api.entity.ai.GoalTypes;
import org.spongepowered.api.entity.ai.task.AITask;
import org.spongepowered.api.entity.ai.task.builtin.LookIdleAITask;
import org.spongepowered.api.entity.ai.task.builtin.SwimmingAITask;
import org.spongepowered.api.entity.ai.task.builtin.WatchClosestAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.AttackLivingAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.AvoidEntityAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.RangeAgentAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.WanderAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.horse.RunAroundLikeCrazyAITask;
import org.spongepowered.api.entity.ai.task.builtin.creature.target.FindNearestAttackableTargetAITask;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.Ranger;
import org.spongepowered.api.entity.living.animal.RideableHorse;

import java.util.Optional;

public class AddAITaskCommand extends AbstractCommand {


    // <--[explanation]
    // @Since 0.4.0
    // @Name AI Goal Types
    // @Group Useful Lists
    // @Description
    // A list of all default AI goal types can be found here:
    // <@link url https://jd.spongepowered.org/7.1.0-SNAPSHOT/org/spongepowered/api/entity/ai/GoalTypes.html>AI goal types list<@/link>
    // These can be used with the addaitask, removeaitasks and editaitask commands.
    // Keep in mind that the goal type "normal" is available for most living entities,
    // while the "target" goal type is mainly for combat related ones.
    // -->

    // <--[explanation]
    // @Since 0.4.0
    // @Name AI Task Types
    // @Group Useful Lists
    // @Description
    // The default AI task types are "attack_living", "avoid_entity", "find_target",
    // "look_idle", "range", "run_around", "swim", "wander", and "watch_closest".
    // These can be used with the addaitask, removeaitasks and editaitask commands.
    // -->

    // <--[command]
    // @Since 0.4.0
    // @Name addaitask
    // @Arguments <entity> <task type> <map of properties>
    // @Short adds an AI task to an entity's goal.
    // @Updated 2018/01/27
    // @Group Entity
    // @Minimum 3
    // @Maximum 3
    // @Named priority (IntegerTag) Sets the priority to run this task.
    // @Named goal (TextTag) Sets to which goal will the task be added.
    // @Description
    // Adds an AI task to an entity's goal.
    // Related information: <@link explanation AI Goal Types>AI goal types<@/link>.
    // Related information: <@link explanation AI Task Types>AI task types<@/link>.
    // TODO: Explain task priority.
    // TODO: Explain properties of each task type.
    // @Example
    // # Makes the zombie in front of the player sink in water.
    // - addaitask <player.target_entities[type:zombie].get[1]> swim chance:0.0 --priority -10 --goal normal
    // -->

    @Override
    public String getName() {
        return "addaitask";
    }

    @Override
    public String getArguments() {
        return "<entity> <task type> <map of properties>";
    }

    @Override
    public int getMinimumArguments() {
        return 3;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        EntityTag entityTag = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        try {
            Agent agent = (Agent) entityTag.getInternal();
            TextTag type = TextTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
            MapTag properties = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            AITask<? extends Agent> task;
            switch (type.getInternal()) {
                case "attack_living":
                    double speed = NumberTag.getFor(queue.error, properties.getInternal().get("speed")).getInternal();
                    boolean memory = BooleanTag.getFor(queue.error, properties.getInternal().get("memory")).getInternal();
                    if (memory) {
                        task = AttackLivingAITask.builder().speed(speed).longMemory().build((Creature) agent);
                    }
                    else {
                        task = AttackLivingAITask.builder().speed(speed).build((Creature) agent);
                    }
                    break;
                case "avoid_entity":
                    double closeSpeed = NumberTag.getFor(queue.error, properties.getInternal().get("close_speed")).getInternal();
                    double farSpeed = NumberTag.getFor(queue.error, properties.getInternal().get("far_speed")).getInternal();
                    float distance = (float) NumberTag.getFor(queue.error, properties.getInternal().get("distance")).getInternal();
                    // TODO: Allow the task to select targets based on a predicate -> .targetSelector(...)
                    task = AvoidEntityAITask.builder().closeRangeSpeed(closeSpeed).farRangeSpeed(farSpeed)
                            .searchDistance(distance).build((Creature) agent);
                    break;
                case "find_target":
                    int chance = (int) IntegerTag.getFor(queue.error, properties.getInternal().get("chance")).getInternal();
                    EntityType target = EntityTypeTag.getFor(queue.error, properties.getInternal().get("target")).getInternal();
                    // TODO: Allow the task to filter targets based on a predicate -> .filter(...)
                    task = FindNearestAttackableTargetAITask.builder().chance(chance)
                            .target(target.getEntityClass().asSubclass(Living.class)).build((Creature) agent);
                    break;
                case "look_idle":
                    task = LookIdleAITask.builder().build(agent);
                    break;
                case "range":
                    float radius = (float) NumberTag.getFor(queue.error, properties.getInternal().get("radius")).getInternal();
                    int delay = (int) DurationTag.getFor(queue.error, properties.getInternal().get("delay")).getInternal()* 20;
                    double moveSpeed = NumberTag.getFor(queue.error, properties.getInternal().get("speed")).getInternal();
                    task = RangeAgentAITask.builder().attackRadius(radius).delayBetweenAttacks(delay)
                            .moveSpeed(moveSpeed).build((Ranger) agent);
                    break;
                case "run_around":
                    double runSpeed = NumberTag.getFor(queue.error, properties.getInternal().get("speed")).getInternal();
                    task = RunAroundLikeCrazyAITask.builder().speed(runSpeed).build((RideableHorse) agent);
                    break;
                case "swim":
                    float swimChance = (float) NumberTag.getFor(queue.error, properties.getInternal().get("chance")).getInternal();
                    task = SwimmingAITask.builder().swimChance(swimChance).build(agent);
                    break;
                case "wander":
                    int executionChance = (int) IntegerTag.getFor(queue.error, properties.getInternal().get("chance")).getInternal();
                    double wanderSpeed = NumberTag.getFor(queue.error, properties.getInternal().get("speed")).getInternal();
                    task = WanderAITask.builder().executionChance(executionChance).speed(wanderSpeed).build((Creature) agent);
                    break;
                case "watch_closest":
                    float watchChance = (float) NumberTag.getFor(queue.error, properties.getInternal().get("chance")).getInternal();
                    float maxDistance = (float) NumberTag.getFor(queue.error, properties.getInternal().get("max_distance")).getInternal();
                    EntityType watch = EntityTypeTag.getFor(queue.error, properties.getInternal().get("target")).getInternal();
                    task = WatchClosestAITask.builder().chance(watchChance).maxDistance(maxDistance)
                            .watch(watch.getEntityClass()).build(agent);
                    break;
                default:
                    queue.handleError(entry, "Invalid task type '" + type.debug() + "' in AddAITask command!");
                    return;
            }
            int priority;
            if (entry.namedArgs.containsKey("priority")) {
                priority = (int) IntegerTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "priority")).getInternal();
            }
            else {
                priority = 0;
            }
            GoalType goal;
            if (entry.namedArgs.containsKey("goal")) {
                TextTag goalType = TextTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "goal"));
                Optional<GoalType> opt = Sponge.getRegistry().getType(GoalType.class, goalType.getInternal());
                if (opt.isPresent()) {
                    goal = opt.get();
                }
                else {
                    queue.handleError(entry, "Invalid goal type '" + goalType.debug() + "' in AddAITask command!");
                    return;
                }
            }
            else {
                goal = GoalTypes.NORMAL;
            }
            agent.getGoal(goal).get().addTask(priority, task);
            if (queue.shouldShowGood()) {
                queue.outGood("Added task of type '" + ColorSet.emphasis + type.debug()
                        + ColorSet.good + "' to goal '" + ColorSet.emphasis + goal.getId()
                        + ColorSet.good + "' of entity '" + ColorSet.emphasis + entityTag.debug()
                        + ColorSet.good + "'!");
            }
        }
        catch (ClassCastException e) {
            queue.handleError(entry, "This entity doesn't support AI goals and tasks!");
        }
    }
}
