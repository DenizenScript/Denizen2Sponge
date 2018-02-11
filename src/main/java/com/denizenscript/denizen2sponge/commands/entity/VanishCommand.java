package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;

public class VanishCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name vanish
    // @Arguments <entity> <state>
    // @Short changes the vanish state of an entity.
    // @Updated 2018/02/11
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named ignore_collisions (BooleanTag) Sets whether this vanished entity can collide with other entities.
    // @Named untargeteable (BooleanTag) Sets whether this vanished entity can be selected as a target.
    // @Description
    // Changes the vanish state of an entity. Vanished entities don't send packets to clients,
    // but they can still affect collisions or target selection server-side. This can be adjusted
    // with the "ignore_collisions" and "untargeteable" named arguments.
    // but update packets are still sent to the server and clients.
    // Related commands: <@link command invisible>invisible<@/link>.
    // @Example
    // # Vanishes the player and makes it untargeteable and uncollidable.
    // - vanish <player> true --ignore_collisions true --untargeteable true
    // -->

    @Override
    public String getName() {
        return "vanish";
    }

    @Override
    public String getArguments() {
        return "<entity> <state>";
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
        BooleanTag state = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Entity entity = entityTag.getInternal();
        entity.offer(Keys.VANISH, state.getInternal());
        if (entry.namedArgs.containsKey("ignore_collisions")) {
            BooleanTag bt = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "ignore_collisions"));
            entity.offer(Keys.VANISH_IGNORES_COLLISION, bt.getInternal());
        }
        if (entry.namedArgs.containsKey("untargeteable")) {
            BooleanTag bt = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "untargeteable"));
            entity.offer(Keys.VANISH_PREVENTS_TARGETING, bt.getInternal());
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Setting vanish state of entity '" + ColorSet.emphasis + entityTag.debug()
                    + ColorSet.good + "' to '" + ColorSet.emphasis + state.debug() + ColorSet.good + "'!");
        }
    }
}
