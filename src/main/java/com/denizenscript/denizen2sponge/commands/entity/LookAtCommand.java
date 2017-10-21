package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;

public class LookAtCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.3.0
    // @Name lookat
    // @Arguments <entity> <location>
    // @Short makes an entity look at a location.
    // @Updated 2017/10/02
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Makes a living entity look at a location. This will rotate the entity's head, but not its body.
    // @Example
    // # This example makes the target entity of the player look at him in the eye
    // - lookat <player.entities_on_cursor.get[1]> <player.eye_location>
    // -->

    @Override
    public String getName() {
        return "lookat";
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
        EntityTag entityTag = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Living ent = (Living) entityTag.getInternal();
        LocationTag locationTag = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Vector3d loc = locationTag.getInternal().toVector3d();
        ent.lookAt(loc);
        if (queue.shouldShowGood()) {
            queue.outGood("Entity " + ColorSet.emphasis + entityTag.debug() + ColorSet.good + " is now looking at "
                    + ColorSet.emphasis + locationTag.debug() + ColorSet.good + "!");
        }
    }
}
