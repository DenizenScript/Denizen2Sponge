package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.Entity;

import java.util.Map;

public class EditEntityCommand extends AbstractCommand {

    // <--[command]
    // @Name editentity
    // @Arguments <entity> <map of properties>
    // @Short edits an entity.
    // @Updated 2016/09/26
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Edits an entity to have the specified map of new properties.
    // @Example
    // # Lights the player on fire.
    // - editentity <player> fire_ticks:999999
    // -->

    @Override
    public String getName() {
        return "editentity";
    }

    @Override
    public String getArguments() {
        return "<entity> <map of properties>";
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
        Entity entity = entityTag.getInternal();
        MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> mapEntry : propertyMap.getInternal().entrySet()) {
            if (mapEntry.getKey().equalsIgnoreCase("rotation")) {
                LocationTag rot = LocationTag.getFor(queue.error, mapEntry.getValue());
                entity.setRotation(rot.getInternal().toVector3d());
            }
            else {
                Key found = DataKeys.getKeyForName(mapEntry.getKey());
                if (found == null) {
                    queue.handleError(entry, "Invalid property '" + mapEntry.getKey() + "' in EditEntity command!");
                    return;
                }
                DataKeys.tryApply(entity, found, mapEntry.getValue(), queue.error);
            }
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Edited the entity "
                    + ColorSet.emphasis + entityTag.debug() + ColorSet.good
                    + " to have the following properties: "
                    + ColorSet.emphasis + propertyMap.debug());
        }
    }
}
