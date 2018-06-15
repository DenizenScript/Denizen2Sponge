package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.ai.Goal;
import org.spongepowered.api.entity.ai.GoalType;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpawnCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name spawn
    // @Arguments <entity type> <location> [map of properties]
    // @Short spawns a new entity.
    // @Updated 2018/06/10
    // @Group Entity
    // @Minimum 2
    // @Maximum 4
    // @Named cause (TextTag) Sets what caused this entity to spawn.
    // @Tag <[spawn_success]> (BooleanTag) returns whether the spawn passed.
    // @Tag <[spawn_entity]> (EntityTag) returns the entity that was spawned (only if the spawn passed).
    // @Description
    // Spawns an entity of the specified entity type or from a script at a location.
    // Optionally, specify a MapTag of properties to spawn the entity with those values
    // automatically set on it. The MapTag can also contain an "orientation" key with a
    // LocationTag and a "clear_ai_tasks" key with either a "normal" or "target" TextTag.
    // You can also specify a MapTag with task types as keys and MapTags of properties as
    // values to give the entity some custom behavior on spawn.
    // Related information: <@link explanation Entity Types>entity types<@/link> and <@link explanation Spawn Causes>spawn causes<@/link>.
    // Related commands: <@link command remove>remove<@/link>.
    // @Example
    // # Spawns a sheep that feels the burn.
    // - spawn sheep <player.location> display_name:<texts.for_input[text:Bahhhb]>|max_health:300|health:300|fire_ticks:999999|is_sheared:true
    // @Example
    // # Spawns the custom mob "cool_zombie" with the "breeding" spawn cause.
    // - spawn cool_zombie <player.location> --cause breeding
    // @Example
    // # Spawns a skeleton, clears its AI Tasks for goal "normal", and then makes it sink on water.
    // - spawn skeleton <player.location> clear_ai_tasks:normal swimming:<map[chance:0].escaped>
    // -->

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getArguments() {
        return "<entity type> <location> [map of properties] [map of task maps]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 4;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        UtilLocation location = locationTag.getInternal();
        if (location.world == null) {
            queue.handleError(entry, "Invalid location with no world in Spawn command!");
            return;
        }
        String str = entry.getArgumentObject(queue, 0).toString();
        EntityType entType = (EntityType) Utilities.getTypeWithDefaultPrefix(EntityType.class, str);
        Entity entity;
        boolean fromScript;
        HashMap<String, AbstractTagObject> propertyMap;
        HashMap<String, HashMap<String, AbstractTagObject>> taskMap;
        if (entType != null) {
            fromScript = false;
            propertyMap = new HashMap<>();
            taskMap = new HashMap<>();
            entity = location.world.createEntity(entType, location.toVector3d());
        }
        else {
            String strLow = CoreUtilities.toLowerCase(str);
            if (Denizen2Sponge.entityScripts.containsKey(strLow)) {
                EntityTemplate template = Denizen2Sponge.entityScripts.get(strLow).getEntityCopy(queue);
                fromScript = true;
                entType = template.type;
                propertyMap = template.properties;
                taskMap = template.tasks;
                entity = location.world.createEntity(entType, location.toVector3d());

            }
            else {
                queue.handleError(entry, "No entity types nor scripts found for id '" + str + "'.");
                return;
            }
        }
        MapTag moreProperties;
        if (entry.arguments.size() > 2) {
            moreProperties = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            propertyMap.putAll(moreProperties.getInternal());
        }
        else {
            moreProperties = new MapTag();
        }
        try {
            if (!propertyMap.isEmpty()) {
                for (Map.Entry<String, AbstractTagObject> mapEntry : propertyMap.entrySet()) {
                    if (mapEntry.getKey().equalsIgnoreCase("orientation")) {
                        LocationTag rot = LocationTag.getFor(queue.error, mapEntry.getValue());
                        entity.setRotation(rot.getInternal().toVector3d());
                    }
                    if (mapEntry.getKey().equalsIgnoreCase("clear_ai_tasks")) {
                        TextTag gt = TextTag.getFor(queue.error, mapEntry.getValue());
                        GoalType goalType = (GoalType) Utilities.getTypeWithDefaultPrefix(GoalType.class, gt.getInternal());
                        if (goalType == null) {
                            queue.handleError(entry, "Invalid goal type '" + gt.debug()
                                    + "' for clear_ai_tasks property in Spawn command!");
                            return;
                        }
                        Agent agent = (Agent) entity;
                        Optional<Goal<Agent>> goal = agent.getGoal(goalType);
                        if (!goal.isPresent()) {
                            queue.handleError(entry, "This entity doesn't have an AI Goal of type '" + goalType.getId() + "'!");
                            return;
                        }
                        goal.get().clear();
                    }
                    else {
                        Key found = DataKeys.getKeyForName(mapEntry.getKey());
                        if (found == null) {
                            queue.handleError(entry, "Invalid property '" + mapEntry.getKey() + "' in Spawn command!");
                            return;
                        }
                        DataKeys.tryApply(entity, found, mapEntry.getValue(), queue.error);
                    }
                }
            }
            MapTag moreTasks;
            if (entry.arguments.size() > 3) {
                moreTasks = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 3));
                for (Map.Entry<String, AbstractTagObject> task : moreTasks.getInternal().entrySet()) {
                    MapTag taskData = MapTag.getFor(queue.error, task.getValue());
                    taskMap.put(task.getKey(), taskData.getInternal());
                }
            }
            if (!taskMap.isEmpty()) {
                for (String taskType : taskMap.keySet()) {
                    AITaskHelper.giveAITask(queue, (Agent) entity, taskType, taskMap.get(taskType));
                }
            }
            SpawnType cause;
            if (entry.namedArgs.containsKey("cause")) {
                String causeStr = entry.getNamedArgumentObject(queue, "cause").toString();
                cause = (SpawnType) Utilities.getTypeWithDefaultPrefix(SpawnType.class, causeStr);
                if (cause == null) {
                    queue.handleError(entry, "Invalid spawn cause '" + causeStr + "' in Spawn command!");
                    return;
                }
            }
            else {
                cause = SpawnTypes.CUSTOM;
            }
            Sponge.getCauseStackManager().addContext(EventContextKeys.SPAWN_TYPE, cause);
            if (queue.shouldShowGood()) {
                queue.outGood("Spawning an entity " + ColorSet.emphasis
                        + (fromScript ? "from script " + str : "of type " + entType.getId())
                        + ColorSet.good + " with the following additional properties: "
                        + ColorSet.emphasis + moreProperties.debug() + ColorSet.good + " at location "
                        + ColorSet.emphasis + locationTag.debug() + ColorSet.good + " and with cause "
                        + ColorSet.emphasis + Utilities.getIdWithoutDefaultPrefix(cause.getId()) + "...");
            }
            boolean passed = location.world.spawnEntity(entity);
            if (queue.shouldShowGood()) {
                queue.outGood("Spawning " + (passed ? "succeeded" : "was blocked") + "!");
            }
            queue.commandStack.peek().setDefinition("spawn_success", new BooleanTag(passed));
            if (passed) {
                queue.commandStack.peek().setDefinition("spawn_entity", new EntityTag(entity));
            }
        }
        catch (ClassCastException e) {
            queue.handleError(entry, "This entity doesn't support AI tasks!");
        }
    }
}
