package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class SpawnCommand extends AbstractCommand {

    // <--[command]
    // @Name spawn
    // @Arguments <entity type> <location> [map of properties]
    // @Short Spawns a new entity.
    // @Updated 2016/09/26
    // @Group Entities
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Spawns an entity at the specified location. Optionally, specify a MapTag of properties
    // to spawn the entity with those values automatically set on it.
    // @Example
    // # Spawns a sheep that feels the burn.
    // - spawn sheep <[player].location> display_name:<texts.for_input[text:Bahhhb]>|max_health:300|health:300|fire_ticks:999999|is_sheared:true
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
    public boolean isProcedural() {
        return false;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        String entityTypeString = entry.getArgumentObject(queue, 0).toString(); // TODO: EntityTypeTag?
        Optional<EntityType> optEntityType = Sponge.getRegistry().getType(EntityType.class, entityTypeString);
        if (!optEntityType.isPresent()) {
            queue.handleError(entry, "Invalid entity type in Spawn command!");
            return;
        }
        EntityType entityType = optEntityType.get();
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        UtilLocation location = locationTag.getInternal();
        if (location.world == null) {
            queue.handleError(entry, "Invalid location with no world in Spawn command!");
            return;
        }
        Entity entity = location.world.createEntity(entityType, location.toVector3d());
        Collection<Key> keys = Sponge.getRegistry().getAllOf(Key.class);
        if (entry.arguments.size() > 2) {
            MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            for (Map.Entry<String, AbstractTagObject> mapEntry : propertyMap.getInternal().entrySet()) {
                String property = CoreUtilities.toLowerCase(mapEntry.getKey());
                Key found = null;
                for (Key key : keys) {
                    if (property.equals(CoreUtilities.after(key.getId(), ":"))) {
                        found = key;
                    }
                }
                if (found == null) {
                    queue.handleError(entry, "Invalid property '" + property + "' in Spawn command!");
                    continue;
                }
                if (!entity.supports(found)) {
                    queue.handleError(entry, "The entity type '" + entityType.getName()
                            + "' does not support the property '" + found.getId() + "'!");
                    continue;
                }
                Class clazz = found.getElementToken().getRawType();
                if (Boolean.class.isAssignableFrom(clazz)) {
                    entity.offer(found, BooleanTag.getFor(queue.error, mapEntry.getValue()).getInternal());
                }
                else if (Double.class.isAssignableFrom(clazz)) {
                    entity.offer(found, NumberTag.getFor(queue.error, mapEntry.getValue()).getInternal());
                }
                else if (Enum.class.isAssignableFrom(clazz)) {
                    entity.offer(found, Enum.valueOf(clazz, mapEntry.getValue().toString().toUpperCase()));
                }
                else if (Integer.class.isAssignableFrom(clazz)) {
                    entity.offer(found, (int) IntegerTag.getFor(queue.error, mapEntry.getValue()).getInternal());
                }
                else if (Text.class.isAssignableFrom(clazz)) {
                    entity.offer(found, FormattedTextTag.getFor(queue.error, mapEntry.getValue()).getInternal());
                }
                else {
                    queue.handleError(entry, "The value type '" + clazz.getName() + "' is not supported yet!");
                }
            }
        }
        location.world.spawnEntity(entity, Cause.source(EntitySpawnCause.builder()
                .entity(entity).type(SpawnTypes.PLUGIN)).build());
    }
}
