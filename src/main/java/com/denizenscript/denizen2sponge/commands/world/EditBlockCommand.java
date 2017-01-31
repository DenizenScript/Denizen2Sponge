package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;

public class EditBlockCommand extends AbstractCommand {

    // <--[command]
    // @Name editblock
    // @Arguments <location> <map of properties>
    // @Short Edits a block.
    // @Updated 2016/09/29
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Edits a location in a world to have the specified map of new properties.
    // @Example
    // # Edits a wool block to be blue.
    // - editblock <player.location> dye_color:blue
    // -->

    @Override
    public String getName() {
        return "editblock";
    }

    @Override
    public String getArguments() {
        return "<location> <map of properties>";
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
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Location<World> loc = locationTag.getInternal().toLocation();
        BlockState bs = loc.getBlock();
        MapTag propertyMap = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> mapEntry : propertyMap.getInternal().entrySet()) {
            Key found = DataKeys.getKeyForName(mapEntry.getKey());
            if (found == null) {
                queue.handleError(entry, "Invalid property '" + mapEntry.getKey() + "' in EditBlock command!");
                return;
            }
            bs = (BlockState) DataKeys.with(bs, found, mapEntry.getValue(), queue.error);
        }
        loc.setBlock(bs, BlockChangeFlag.NONE, Denizen2Sponge.getGenericCause());
        if (queue.shouldShowGood()) {
            queue.outGood("Edited the location " + ColorSet.emphasis + locationTag.toString() + ColorSet.good
                    + " to have the new specified properties...");
        }
    }
}
