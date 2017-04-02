package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.world.BlockChangeFlag;

public class SetBlockCommand extends AbstractCommand {

    // <--[command]
    // @Name setblock
    // @Arguments <list of locations> <blocktype>
    // @Short sets a block's type.
    // @Updated 2016/11/24
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Named physics (BooleanTag) Sets whether the block will have physics enabled or not.
    // @Description
    // Sets a block's type. Physics defaults to enabled.
    // Related information: <@link explanation Block Types>block types<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example sets the block at a player's location to stone.
    // - setblock <player.location> minecraft:stone
    // @Example
    // # This example sets the block at a player's location to sand that won't fall immediately.
    // - setblock <player.location> minecraft:sand false
    // -->

    @Override
    public String getName() {
        return "setblock";
    }

    @Override
    public String getArguments() {
        return "<list of locations> <blocktype>";
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
        ListTag locs = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        BlockTypeTag type = BlockTypeTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        boolean phys = true;
        if (entry.namedArgs.containsKey("physics")) {
            phys = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "physics")).getInternal();
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Changing location(s) " + ColorSet.emphasis + locs.debug() + ColorSet.good
                    + " to type " + ColorSet.emphasis + type.debug() + ColorSet.good
                    + " with physics " + ColorSet.emphasis + (phys ? "on" : "off"));
        }
        for (AbstractTagObject ato : locs.getInternal()) {
            LocationTag loc = LocationTag.getFor(queue.error, ato);
            loc.getInternal().world.setBlockType(loc.getInternal().toVector3i(), type.getInternal(),
                    phys ? BlockChangeFlag.ALL : BlockChangeFlag.NONE, Denizen2Sponge.getGenericCause());
        }
        // TODO: "Cause" argument!
    }
}
