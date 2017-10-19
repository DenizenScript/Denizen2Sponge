package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;

public class TeleportCommand extends AbstractCommand {

    // <--[command]
    // @Name teleport
    // @Arguments <entity> <location>
    // @Short teleports the entity to a location.
    // @Updated 2017/10/18
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named rotation (LocationTag) Sets the entity's rotation.
    // @Description
    // Teleports the entity to a location. Optionally specify a rotation.
    // TODO: Explain more!
    // @Example
    // # This example teleports the player five blocks upward
    // - teleport <player> <player.location.add[0,5,0]>
    // -->

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getArguments() {
        return "<entity> <location>";
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
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (entry.namedArgs.containsKey("rotation")) {
            LocationTag rot = LocationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "rotation"));
            ent.getInternal().setTransform(ent.getInternal().getTransform()
                    .setLocation(loc.getInternal().toLocation())
                    .setRotation(rot.getInternal().toVector3d())); // TODO: use the returned boolean?
            if (queue.shouldShowGood()) {
                queue.outGood("Teleporting " + ColorSet.emphasis + ent.debug() + ColorSet.good
                        + " to location " + ColorSet.emphasis + loc.debug() + ColorSet.good
                        + " and rotation " + ColorSet.emphasis + rot.debug() + "!");
            }
        }
        else {
            ent.getInternal().setLocation(loc.getInternal().toLocation()); // TODO: use the returned boolean?
            if (queue.shouldShowGood()) {
                queue.outGood("Teleporting " + ColorSet.emphasis + ent.debug() + ColorSet.good
                        + " to location " + ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
            }
        }

    }
}
