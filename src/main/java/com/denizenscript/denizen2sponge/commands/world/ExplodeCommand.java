package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.world.explosion.Explosion;

public class ExplodeCommand extends AbstractCommand {

    // <--[command]
    // @Name explode
    // @Arguments <location> <radius>
    // @Short creates an explosion.
    // @Updated 2017/09/08
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Named fire (BooleanTag) Sets whether the explosion can cause fire.
    // @Named break_blocks (BooleanTag) Sets whether the explosion can break blocks.
    // @Named damage_entities (BooleanTag) Sets whether the explosion can damage entities.
    // @Named smoke (BooleanTag) Sets whether the explosion will have smoke particles.
    // @Description
    // Creates an explosion with the specified radius at certain location. Note that common
    // explosion sizes are: 1 = small, 2 = medium, 3 = big, and 4+ = massive. Optionally
    // specify whether it can cause fire, break blocks, damage entities or spawn smoke particles.
    // @Example
    // # This example creates a very big explosion at the player's location and leaves a
    // # crater behind, even though it will not damage any entity.
    // - explode <player.location> 5.5 --break_blocks true --damage_entities false
    // -->

    @Override
    public String getName() {
        return "explode";
    }

    @Override
    public String getArguments() {
        return "<location> <radius>";
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
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        NumberTag radius = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Explosion.Builder build = Explosion.builder();
        build.location(loc.getInternal().toLocation()).radius((float) radius.getInternal());
        if (entry.namedArgs.containsKey("fire")) {
            BooleanTag fire = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "fire"));
            build.canCauseFire(fire.getInternal());
        }
        if (entry.namedArgs.containsKey("break_blocks")) {
            BooleanTag break_blocks = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "break_blocks"));
            build.shouldBreakBlocks(break_blocks.getInternal());
        }
        if (entry.namedArgs.containsKey("damage_entities")) {
            BooleanTag damage_entities = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "damage_entities"));
            build.shouldDamageEntities(damage_entities.getInternal());
        }
        if (entry.namedArgs.containsKey("smoke")) {
            BooleanTag smoke = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "smoke"));
            build.shouldPlaySmoke(smoke.getInternal());
        }
        loc.getInternal().world.triggerExplosion(build.build(), Denizen2Sponge.getGenericCause());
        if (queue.shouldShowGood()) {
            queue.outGood("Successfully created an explosion at location " +
                    ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
        }
    }
}
