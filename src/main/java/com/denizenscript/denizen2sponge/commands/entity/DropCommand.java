package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;

public class DropCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name drop
    // @Arguments <item> <location>
    // @Short drops an item.
    // @Updated 2017/05/05
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Tag <[drop_entity]> (EntityTag) returns the item entity that was dropped.
    // @Description
    // Drops an item at the specified location.
    // Related information: <@link explanation Item Types>item types<@/link>.
    // @Example
    // # Drops a diamond at the player's feet.
    // - drop diamond <player.location>
    // -->

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public String getArguments() {
        return "<item> <location>";
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
        ItemTag item = ItemTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        UtilLocation location = locationTag.getInternal();
        if (location.world == null) {
            queue.handleError(entry, "Invalid location with no world in Spawn command!");
            return;
        }
        Entity entity = location.world.createEntity(EntityTypes.ITEM, location.toVector3d());
        entity.offer(Keys.REPRESENTED_ITEM, item.getInternal().createSnapshot());
        location.world.spawnEntity(entity);
        if (queue.shouldShowGood()) {
            queue.outGood("Dropped item " + ColorSet.emphasis + item.debug() + ColorSet.good
                    + " at location " + ColorSet.emphasis + locationTag.debug() + ColorSet.good + "!");
        }
    }
}
