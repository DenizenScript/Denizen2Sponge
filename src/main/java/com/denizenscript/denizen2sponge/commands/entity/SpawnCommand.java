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
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
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
    // @Named aitasks (MapTag) Sets a map of AI tasks (each of which takes a map itself as a value) to apply to the entity on spawn.
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
    // - spawn skeleton <player.location> clear_ai_tasks:normal --aitasks swimming:<map[chance:0].escaped>
    // -->

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getArguments() {
        return "<entity type> <location> [map of properties]";
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
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        UtilLocation location = locationTag.getInternal();
        if (location.world == null) {
            queue.handleError(entry, "Invalid location with no world in Spawn command!");
            return;
        }
        String inputType = entry.getArgumentObject(queue, 0).toString();
        EntityType entType = (EntityType) Utilities.getTypeWithDefaultPrefix(EntityType.class, inputType);
        boolean fromScript = entType == null;
        EntityTemplate template = null;
        if (fromScript) {
            String inputTypeLow = CoreUtilities.toLowerCase(inputType);
            if (Denizen2Sponge.entityScripts.containsKey(inputTypeLow)) {
                template = Denizen2Sponge.entityScripts.get(inputTypeLow).getEntityCopy(queue);
                entType = template.type;
            }
            else {
                queue.handleError(entry, "No entity types nor scripts found for id '"
                        + ColorSet.emphasis + inputType + ColorSet.warning + "'.");
                return;
            }
        }
        Entity entity = location.world.createEntity(entType, location.toVector3d());
        HashMap<String, AbstractTagObject> propertyMap = template == null ? null : template.properties;
        if (entry.arguments.size() > 2) {
            MapTag argProps = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            if (propertyMap == null) {
                propertyMap = argProps.getInternal();
            }
            else {
                propertyMap.putAll(argProps.getInternal());
            }
        }
        if (propertyMap != null && !propertyMap.isEmpty()) {
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
        HashMap<String, HashMap<String, AbstractTagObject>> taskMap = template == null ? null : template.tasks;
        if (entry.namedArgs.containsKey("aitasks")) {
            MapTag moreTasks = MapTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "aitasks"));
            if (taskMap == null) {
                taskMap = new HashMap<>();
            }
            for (Map.Entry<String, AbstractTagObject> task : moreTasks.getInternal().entrySet()) {
                MapTag taskData = MapTag.getFor(queue.error, task.getValue());
                taskMap.put(task.getKey(), taskData.getInternal());
            }
        }
        if (taskMap != null && !taskMap.isEmpty()) {
            try {
                for (String taskType : taskMap.keySet()) {
                    AITaskHelper.giveAITask(queue, (Agent) entity, taskType, taskMap.get(taskType));
                }
            }
            catch (ClassCastException e) {
                queue.handleError(entry, "This entity doesn't support AI tasks!");
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
            queue.outGood("Spawning an entity "
                    + (fromScript ? "from script " + ColorSet.emphasis + inputType : "of type " + ColorSet.emphasis + new EntityTypeTag(entType).debug())
                    + ColorSet.good + (propertyMap == null ? "" : " with the following additional properties: "
                    + ColorSet.emphasis + new MapTag(propertyMap).debug() + ColorSet.good) + " at location "
                    + ColorSet.emphasis + locationTag.debug() + ColorSet.good + " and with cause "
                    + ColorSet.emphasis + Utilities.getIdWithoutDefaultPrefix(cause.getId()) + ColorSet.good + "...");
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
}
