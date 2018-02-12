package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

import java.util.Map;

public class SpawnCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name spawn
    // @Arguments <entity type> <location> [map of properties]
    // @Short spawns a new entity.
    // @Updated 2017/10/18
    // @Group Entity
    // @Minimum 2
    // @Maximum 3
    // @Tag <[spawn_success]> (BooleanTag) returns whether the spawn passed.
    // @Tag <[spawn_entity]> (EntityTag) returns the entity that was spawned (only if the spawn passed).
    // @Description
    // Spawns an entity at the specified location. Optionally, specify a MapTag
    // of properties to spawn the entity with those values automatically set on
    // it. The MapTag can also contain a "rotation" key with a LocationTag.
    // Related information: <@link explanation Entity Types>entity types<@/link>.
    // Related commands: <@link command remove>remove<@/link>.
    // @Example
    // # Spawns a sheep that feels the burn.
    // - spawn sheep <player.location> display_name:<texts.for_input[text:Bahhhb]>|max_health:300|health:300|fire_ticks:999999|is_sheared:true
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
        EntityTypeTag entityTypeTag = EntityTypeTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        EntityType entityType = entityTypeTag.getInternal();
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        UtilLocation location = locationTag.getInternal();
        if (location.world == null) {
            queue.handleError(entry, "Invalid location with no world in Spawn command!");
            return;
        }
        Entity entity = location.world.createEntity(entityType, location.toVector3d());
        MapTag propertyMap = new MapTag();
        if (entry.arguments.size() > 2) {
            propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            for (Map.Entry<String, AbstractTagObject> mapEntry : propertyMap.getInternal().entrySet()) {
                if (mapEntry.getKey().equalsIgnoreCase("rotation")) {
                    LocationTag rot = LocationTag.getFor(queue.error, mapEntry.getValue());
                    entity.setRotation(rot.getInternal().toVector3d());
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
        if (queue.shouldShowGood()) {
                queue.outGood("Spawning an entity of type " + ColorSet.emphasis + entityType.getId()
                        + ColorSet.good + " with the following properties: " + ColorSet.emphasis
                        + propertyMap.debug() + ColorSet.good + " at location " + ColorSet.emphasis
                        + locationTag.debug() + ColorSet.good + "...");
        }
        boolean passed = location.world.spawnEntity(entity);
        // TODO: "Cause" argument!
        if (queue.shouldShowGood()) {
            queue.outGood("Spawning " + (passed ? "succeeded" : "was blocked") + "!");
        }
        queue.commandStack.peek().setDefinition("spawn_success", new BooleanTag(passed));
        if (passed) {
            queue.commandStack.peek().setDefinition("spawn_entity", new EntityTag(entity));
        }
    }
}
