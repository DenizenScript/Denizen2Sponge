package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;

import java.util.Optional;

public class PlayEffectCommand extends AbstractCommand {

    // <--[command]
    // @Name playeffect
    // @Arguments <location> <effect> [count] [offset] [motion]
    // @Short plays an effect.
    // @Updated 2016/09/28
    // @Group World
    // @Minimum 2
    // @Maximum 5
    // @Description
    // Plays an effect.
    // TODO: Explain more!
    // @Example
    // # This example plays the 'heart' effect around the player.
    // - playeffect <[player].location> heart 50 1,1,1 0,0,0
    // -->

    @Override
    public String getName() {
        return "playeffect";
    }

    @Override
    public String getArguments() {
        return "<location> <effect> [offset] [motion]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 5;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String effectName = entry.getArgumentObject(queue, 1).toString().toLowerCase();
        ParticleEffect.Builder build = ParticleEffect.builder();
        Optional<ParticleType> type = Sponge.getRegistry().getType(ParticleType.class, effectName);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid particle effect type: '" + effectName + "'!");
            return;
        }
        build.type(type.get());
        if (entry.arguments.size() > 2) {
            IntegerTag integer = IntegerTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            build.quantity((int) integer.getInternal());
        }
        if (entry.arguments.size() > 3) {
            LocationTag offset = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 3));
            build.offset(offset.getInternal().toVector3d());
        }
        if (entry.arguments.size() > 4) {
            LocationTag offset = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 4));
            build.velocity(offset.getInternal().toVector3d());
        }
        loc.getInternal().world.spawnParticles(build.build(), loc.getInternal().toVector3d());
        if (queue.shouldShowGood()) {
            queue.outGood("Successfully played the specified effect!");
        }
    }
}
