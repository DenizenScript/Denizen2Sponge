package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.DurationTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Optional;

public class CastCommand extends AbstractCommand {

    // <--[explanation]
    // @Name Potion Effect Types
    // @Group Useful Lists
    // @Description
    // A list of all default potion effect types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/effect/potion/PotionEffectTypes.html>potion effect types list<@/link>
    // These can be used with the cast command.
    // -->

    // <--[command]
    // @Name cast
    // @Arguments <entity> <effect> <duration>
    // @Short casts a potion effect on an entity.
    // @Updated 2017/04/01
    // @Group Entities
    // @Minimum 3
    // @Maximum 3
    // @Named amplifier (IntegerTag) Sets the potion effect amplifier.
    // @Named ambient (BooleanTag) Sets whether the effect will be ambient or not.
    // @Named particles (BooleanTag) Sets whether the effect will show particles or not.
    // @Description
    // Casts a potion effect on an entity for the specified duration.
    // Particles are set to true by default, and ambient to false.
    // Related information: <@link explanation Potion Effect Types>potion effect types<@/link>.
    // @Example
    // # This example casts 'poison' on the player for 5 seconds
    // - cast <player> poison 5s
    // -->

    @Override
    public String getName() {
        return "cast";
    }

    @Override
    public String getArguments() {
        return "<entity> <effect> <duration>";
    }

    @Override
    public int getMinimumArguments() {
        return 3;
    }

    @Override
    public int getMaximumArguments() {
        return 3;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String name = entry.getArgumentObject(queue, 1).toString().toLowerCase();
        if (!ent.getInternal().supports(PotionEffectData.class)) {
            queue.handleError(entry, "This entity type does not support potion effects!");
            return;
        }
        PotionEffectData data = ent.getInternal().getOrCreate(PotionEffectData.class).get();
        Optional<PotionEffectType> type = Sponge.getRegistry().getType(PotionEffectType.class, name);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid potion effect name: '" + name + "'!");
            return;
        }
        DurationTag dur = DurationTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
        PotionEffect.Builder build = PotionEffect.builder().potionType(type.get()).duration((int) dur.getInternal() * 20);
        if (entry.namedArgs.containsKey("amplifier")) {
            IntegerTag amp = IntegerTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "amplifier"));
            build.amplifier((int) amp.getInternal() - 1);
        }
        if (entry.namedArgs.containsKey("ambient")) {
            BooleanTag amb = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "ambient"));
            build.ambience(amb.getInternal());
        }
        else {
            build.particles(false);
        }
        if (entry.namedArgs.containsKey("particles")) {
            BooleanTag part = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "particles"));
            build.particles(part.getInternal());
        }
        else {
            build.particles(true);
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Casting " + ColorSet.emphasis + name + ColorSet.good
                    + " on " + ColorSet.emphasis + ent.debug() + ColorSet.good
                    + " for " + ColorSet.emphasis + dur.debug() + ColorSet.good + " seconds!");
        }
        data.addElement(build.build());
        ent.getInternal().offer(data);
    }
}
