package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ai.Goal;
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

import java.util.HashMap;
import java.util.Optional;

public class AITaskHelper {

    public final static HashMap<String, Function3<CommandQueue, Agent, HashMap<String, AbstractTagObject>, AITask<? extends Agent>>> handlers = new HashMap<>();

    static {
        handlers.put("attack_living", (queue, entity, properties) -> {
            if (!properties.containsKey("speed") || !properties.containsKey("memory")) {
                queue.error.run("AI Tasks of type attack_living require speed and memory values!");
                return null;
            }
            double speed = NumberTag.getFor(queue.error, properties.get("speed")).getInternal();
            boolean memory = BooleanTag.getFor(queue.error, properties.get("memory")).getInternal();
            if (memory) {
                return AttackLivingAITask.builder().speed(speed).longMemory().build((Creature) entity);
            } else {
                return AttackLivingAITask.builder().speed(speed).build((Creature) entity);
            }
        });
        handlers.put("avoid_entity", (queue, entity, properties) -> {
            if (!properties.containsKey("close_speed") || !properties.containsKey("far_speed")
                    || !properties.containsKey("distance")) {
                queue.error.run("AI Tasks of type avoid_entity require close_speed," +
                        " far_speed and distance values!");
                return null;
            }
            double closeSpeed = NumberTag.getFor(queue.error, properties.get("close_speed")).getInternal();
            double farSpeed = NumberTag.getFor(queue.error, properties.get("far_speed")).getInternal();
            float distance = (float) NumberTag.getFor(queue.error, properties.get("distance")).getInternal();
            // TODO: Allow the task to select targets based on a predicate -> .targetSelector(...)
            return AvoidEntityAITask.builder().closeRangeSpeed(closeSpeed).farRangeSpeed(farSpeed)
                    .searchDistance(distance).build((Creature) entity);
        });
        handlers.put("find_target", (queue, entity, properties) -> {
            if (!properties.containsKey("chance") || !properties.containsKey("target")) {
                queue.error.run("AI Tasks of type find_target require chance and target values!");
                return null;
            }
            int chance = (int) IntegerTag.getFor(queue.error, properties.get("chance")).getInternal();
            EntityType target = EntityTypeTag.getFor(queue.error, properties.get("target")).getInternal();
            // TODO: Allow the task to filter targets based on a predicate -> .filter(...)
            return FindNearestAttackableTargetAITask.builder().chance(chance)
                    .target(target.getEntityClass().asSubclass(Living.class)).build((Creature) entity);
        });
        handlers.put("look_idle", (queue, entity, properties) -> LookIdleAITask.builder().build(entity));
        handlers.put("range", (queue, entity, properties) -> {
            if (!properties.containsKey("distance") || !properties.containsKey("delay")
                    || !properties.containsKey("speed")) {
                queue.error.run("AI Tasks of type range require distance, delay and speed values!");
                return null;
            }
            float radius = (float) NumberTag.getFor(queue.error, properties.get("distance")).getInternal();
            int delay = (int) (DurationTag.getFor(queue.error, properties.get("delay")).getInternal() * 20);
            double moveSpeed = NumberTag.getFor(queue.error, properties.get("speed")).getInternal();
            return RangeAgentAITask.builder().attackRadius(radius).delayBetweenAttacks(delay)
                    .moveSpeed(moveSpeed).build((Ranger) entity);
        });
        handlers.put("run_around", (queue, entity, properties) -> {
            if (!properties.containsKey("speed")) {
                queue.error.run("AI Tasks of type run_around require a speed value!");
                return null;
            }
            double runSpeed = NumberTag.getFor(queue.error, properties.get("speed")).getInternal();
            return RunAroundLikeCrazyAITask.builder().speed(runSpeed).build((RideableHorse) entity);
        });
        handlers.put("swim", (queue, entity, properties) -> {
            if (!properties.containsKey("chance")) {
                queue.error.run("AI Tasks of type swim require a chance value!");
                return null;
            }
            float swimChance = (float) NumberTag.getFor(queue.error, properties.get("chance")).getInternal();
            return SwimmingAITask.builder().swimChance(swimChance).build(entity);
        });
        handlers.put("wander", (queue, entity, properties) -> {
            if (!properties.containsKey("chance") || !properties.containsKey("speed")) {
                queue.error.run("AI Tasks of type wander require chance and speed values!");
                return null;
            }
            int executionChance = (int) IntegerTag.getFor(queue.error, properties.get("chance")).getInternal();
            double wanderSpeed = NumberTag.getFor(queue.error, properties.get("speed")).getInternal();
            return WanderAITask.builder().executionChance(executionChance).speed(wanderSpeed).build((Creature) entity);
        });
        handlers.put("watch_closest", (queue, entity, properties) -> {
            if (!properties.containsKey("chance") || !properties.containsKey("distance")
                    || !properties.containsKey("target")) {
                queue.error.run("AI Tasks of type range require chance, distance and target values!");
                return null;
            }
            float watchChance = (float) NumberTag.getFor(queue.error, properties.get("chance")).getInternal();
            float maxDistance = (float) NumberTag.getFor(queue.error, properties.get("distance")).getInternal();
            EntityType watch = EntityTypeTag.getFor(queue.error, properties.get("target")).getInternal();
            return WatchClosestAITask.builder().chance(watchChance).maxDistance(maxDistance)
                    .watch(watch.getEntityClass()).build(entity);
        });
    }

    public static void giveAITask(CommandQueue queue, Agent entity, String type, HashMap<String, AbstractTagObject> properties) {
        try {
            Function3<CommandQueue, Agent, HashMap<String, AbstractTagObject>, AITask<? extends Agent>> taskAction = handlers.get(type);
            if (taskAction == null) {
                queue.error.run("Invalid AI Task type: " + type + "!");
                return;
            }
            AITask<? extends Agent> task = taskAction.apply(queue, entity, properties);
            int priority;
            if (properties.containsKey("priority")) {
                priority = (int) IntegerTag.getFor(queue.error, properties.get("priority")).getInternal();
            }
            else {
                priority = 0;
            }
            GoalType goalType;
            if (properties.containsKey("goal")) {
                TextTag tt = TextTag.getFor(queue.error, properties.get("goal"));
                goalType = (GoalType) Utilities.getTypeWithDefaultPrefix(GoalType.class, tt.getInternal());
                if (goalType == null) {
                    queue.error.run("Invalid AI Goal type: " + tt.debug() + "!");
                    return;
                }
            }
            else {
                goalType = GoalTypes.NORMAL;
            }
            Optional<Goal<Agent>> goal = entity.getGoal(goalType);
            if (!goal.isPresent()) {
                queue.error.run("This entity doesn't have an AI goal of type: " + goalType.getId());
                return;
            }
            goal.get().addTask(priority, task);
        }
        catch (ClassCastException e) {
            queue.error.run("This entity doesn't support this type of AI task!");
        }
    }
}
