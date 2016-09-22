package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.world.BlockChangeFlag;

public class SetBlockCommand extends AbstractCommand {

    // <--[command]
    // @Name setblock
    // @Arguments <location> <blocktype> [physics? boolean]
    // @Short Sets a block's type.
    // @Updated 2016/09/22
    // @Group World
    // @Minimum 2
    // @Maximum 3
    // @Description
    // Sets a block's type. Physics defaults to enabled.
    // TODO: Explain more!
    // @Example
    // # This example sets the block at a player's location to stone.
    // - setblock <[player].location> minecraft:stone
    // @Example
    // # This example sets the block at a player's location to sand that won't fall immediately.
    // - setblock <[player].location> minecraft:sand false
    // -->

    @Override
    public String getName() {
        return "setblock";
    }

    @Override
    public String getArguments() {
        return "<location> <blocktype> [physics? boolean]";
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
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        BlockTypeTag type = BlockTypeTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        boolean phys = true;
        if (entry.arguments.size() > 2) {
            phys = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 2)).getInternal();
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Changing location " + ColorSet.emphasis + loc + ColorSet.good
                    + " to type " + ColorSet.emphasis + type + ColorSet.good
                    + " with physics " + ColorSet.emphasis + (phys ? "on" : "off"));
        }
        loc.getInternal().world.setBlockType(loc.getInternal().toVector3i(), type.getInternal(),
                phys ? BlockChangeFlag.ALL : BlockChangeFlag.NONE, Denizen2Sponge.getGenericCause());
        // TODO: "Cause" argument!
    }
}
