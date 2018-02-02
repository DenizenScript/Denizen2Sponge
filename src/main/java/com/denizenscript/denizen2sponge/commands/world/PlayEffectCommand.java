package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;

public class PlayEffectCommand extends AbstractCommand {

    // <--[explanation]
    // @Since 0.3.0
    // @Name Particle Types
    // @Group Useful Lists
    // @Description
    // A list of all default particle types can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/effect/particle/ParticleTypes.html>particle types list<@/link>
    // These can be used with the playeffect command.
    // -->

    // <--[command]
    // @Since 0.3.0
    // @Name playeffect
    // @Arguments <location> <effect>
    // @Short plays an effect.
    // @Updated 2017/04/01
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Named count (IntegerTag) Sets how many particles will be played.
    // @Named offset (LocationTag) Sets the offset of the particles.
    // @Named motion (LocationTag) Sets the motion of the particles.
    // @Named visibility (IntegerTag) Sets the visibility radius of the effect.
    // @Description
    // Plays an effect at certain location. Optionally specify a particle count, offset, velocity and visibility radius.
    // Related information: <@link explanation Particle Types>particle types<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example plays the 'heart' effect around the player.
    // - playeffect <player.location> heart --count 50 --offset 1,1,1
    // -->

    @Override
    public String getName() {
        return "playeffect";
    }

    @Override
    public String getArguments() {
        return "<location> <effect>";
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
        String effectName = entry.getArgumentObject(queue, 1).toString();
        ParticleEffect.Builder build = ParticleEffect.builder();
        Object type = Utilities.getTypeWithDefaultPrefix(ParticleType.class, effectName);
        if (type == null) {
            queue.handleError(entry, "Invalid particle effect type: '" + effectName + "'!");
            return;
        }
        build.type((ParticleType) type);
        if (entry.namedArgs.containsKey("count")) {
            IntegerTag count = IntegerTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "count"));
            build.quantity((int) count.getInternal());
        }
        if (entry.namedArgs.containsKey("offset")) {
            LocationTag offset = LocationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "offset"));
            build.offset(offset.getInternal().toVector3d());
        }
        if (entry.namedArgs.containsKey("motion")) {
            LocationTag motion = LocationTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "motion"));
            build.velocity(motion.getInternal().toVector3d());
        }
        // TODO: Only show the particles to a list of target players.
        if (entry.namedArgs.containsKey("visibility")) {
            IntegerTag visibility = IntegerTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "visibility"));
            loc.getInternal().world.spawnParticles(build.build(), loc.getInternal().toVector3d(), (int) visibility.getInternal());
        }
        else {
            loc.getInternal().world.spawnParticles(build.build(), loc.getInternal().toVector3d());
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Successfully played the particle effect of type '" +
                    ColorSet.emphasis + ((ParticleType) type).getId() + ColorSet.good + "' at location " +
                    ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
        }
    }
}
