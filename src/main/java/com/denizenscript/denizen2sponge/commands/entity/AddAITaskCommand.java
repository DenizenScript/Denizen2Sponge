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
    // @Arguments <entity> <task type>
    // @Short adds an AI task to an entity's goal.
    // @Updated 2018/01/27
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
    // Adds an AI task to an entity's goal. Priority defaults to 0. The lower
    // the priority is, the sooner the task will run in the goal. The default
    // goal is 'normal'.
    // Related information: <@link explanation AI Goal Types>AI goal types<@/link>.
    // Related information: <@link explanation AI Task Types>AI task types<@/link>.
    // Related commands: <@link command removeaitasks>removeaitasks<@/link>.
    // TODO: Explain task priority.
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
        try {
            Agent agent = (Agent) entityTag.getInternal();
            TextTag type = TextTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
            AITask<? extends Agent> task;
            switch (type.getInternal()) {
                case "attack_living":
                    if (!entry.namedArgs.containsKey("speed") || !entry.namedArgs.containsKey("memory")) {
                        queue.handleError(entry,
                                "Tasks of type attack_living require 'speed' and 'memory' arguments!");
                    }
                    double speed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "speed")).getInternal();
                    boolean memory = BooleanTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "memory")).getInternal();
                    if (memory) {
                        task = AttackLivingAITask.builder().speed(speed).longMemory().build((Creature) agent);
                    }
                    else {
                        task = AttackLivingAITask.builder().speed(speed).build((Creature) agent);
                    }
                    break;
                case "avoid_entity":
                    if (!entry.namedArgs.containsKey("close_speed") || !entry.namedArgs.containsKey("far_speed")
                            || !entry.namedArgs.containsKey("distance")) {
                        queue.handleError(entry,
                                "Tasks of type avoid_entity require 'close_speed', 'far_speed' and 'distance' arguments!");
                    }
                    double closeSpeed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "close_speed")).getInternal();
                    double farSpeed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "far_speed")).getInternal();
                    float distance = (float) NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "distance")).getInternal();
                    // TODO: Allow the task to select targets based on a predicate -> .targetSelector(...)
                    task = AvoidEntityAITask.builder().closeRangeSpeed(closeSpeed).farRangeSpeed(farSpeed)
                            .searchDistance(distance).build((Creature) agent);
                    break;
                case "find_target":
                    if (!entry.namedArgs.containsKey("chance") || !entry.namedArgs.containsKey("target")) {
                        queue.handleError(entry,
                                "Tasks of type find_target require 'chance' and 'target' arguments!");
                    }
                    int chance = (int) IntegerTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "chance")).getInternal();
                    EntityType target = EntityTypeTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "target")).getInternal();
                    // TODO: Allow the task to filter targets based on a predicate -> .filter(...)
                    task = FindNearestAttackableTargetAITask.builder().chance(chance)
                            .target(target.getEntityClass().asSubclass(Living.class)).build((Creature) agent);
                    break;
                case "look_idle":
                    task = LookIdleAITask.builder().build(agent);
                    break;
                case "range":
                    if (!entry.namedArgs.containsKey("distance") || !entry.namedArgs.containsKey("delay")
                            || !entry.namedArgs.containsKey("speed")) {
                        queue.handleError(entry,
                                "Tasks of type range require 'distance', 'delay' and 'speed' arguments!");
                    }
                    float radius = (float) NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "distance")).getInternal();
                    int delay = (int) DurationTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "delay")).getInternal() * 20;
                    double moveSpeed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "speed")).getInternal();
                    task = RangeAgentAITask.builder().attackRadius(radius).delayBetweenAttacks(delay)
                            .moveSpeed(moveSpeed).build((Ranger) agent);
                    break;
                case "run_around":
                    if (!entry.namedArgs.containsKey("speed")) {
                        queue.handleError(entry,
                                "Tasks of type run_around require a 'speed' argument!");
                    }
                    double runSpeed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "speed")).getInternal();
                    task = RunAroundLikeCrazyAITask.builder().speed(runSpeed).build((RideableHorse) agent);
                    break;
                case "swim":
                    if (!entry.namedArgs.containsKey("chance")) {
                        queue.handleError(entry,
                                "Tasks of type swim require a 'chance' argument!");
                    }
                    float swimChance = (float) NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "chance")).getInternal();
                    task = SwimmingAITask.builder().swimChance(swimChance).build(agent);
                    break;
                case "wander":
                    if (!entry.namedArgs.containsKey("chance") || !entry.namedArgs.containsKey("speed")) {
                        queue.handleError(entry,
                                "Tasks of type wander require 'chance' and 'speed' arguments!");
                    }
                    int executionChance = (int) IntegerTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "chance")).getInternal();
                    double wanderSpeed = NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "speed")).getInternal();
                    task = WanderAITask.builder().executionChance(executionChance).speed(wanderSpeed).build((Creature) agent);
                    break;
                case "watch_closest":
                    if (!entry.namedArgs.containsKey("chance") || !entry.namedArgs.containsKey("distance")
                            || !entry.namedArgs.containsKey("target")) {
                        queue.handleError(entry,
                                "Tasks of type watch_closest require 'chance', 'distance' and 'target' arguments!");
                    }
                    float watchChance = (float) NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "chance")).getInternal();
                    float maxDistance = (float) NumberTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "distance")).getInternal();
                    EntityType watch = EntityTypeTag.getFor(queue.error,
                            entry.getNamedArgumentObject(queue, "target")).getInternal();
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
            queue.handleError(entry, "This entity doesn't support this type of AI task!");
        }
    }
}
