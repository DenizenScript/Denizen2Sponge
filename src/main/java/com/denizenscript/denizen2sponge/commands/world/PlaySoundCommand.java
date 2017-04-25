package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;

import java.util.Optional;

public class PlaySoundCommand extends AbstractCommand {

    // <--[explanation]
    // @Name Sound Types
    // @Group Useful Lists
    // @Description
    // A list of all default sound types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/effect/sound/SoundTypes.html>sound types list<@/link>
    // These can be used with the playsound command, if you replace '_' with '.'.
    // For example, 'entity.arrow.hit' is a valid sound type.
    // -->

    // <--[command]
    // @Name playsound
    // @Arguments <location> <sound> <volume>
    // @Short plays a sound.
    // @Updated 2017/04/24
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Named pitch (IntegerTag) Sets the pitch of the sound.
    // @Named min_volume (IntegerTag) Sets the minimum volume of the sound.
    // @Description
    // Plays a sound at certain location. Optionally specify a pitch and minimum volume.
    // Related information: <@link explanation Sound Types>sound types<@/link>.
    // TODO: Explain more!
    // @Example
    // # This example plays a loud 'entity_arrow_hit' sound at the player's location.
    // - playsound <player.location> entity.arrow.hit 2
    // -->

    @Override
    public String getName() {
        return "playsound";
    }

    @Override
    public String getArguments() {
        return "<location> <sound> <volume>";
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
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        String soundName = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 1).toString());
        Optional<SoundType> type = Sponge.getRegistry().getType(SoundType.class, soundName);
        if (!type.isPresent()) {
            queue.handleError(entry, "Invalid sound type: '" + soundName + "'!");
            return;
        }
        NumberTag volume = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
        NumberTag pitch;
        NumberTag min_volume;
        // TODO: Only play the sound to a list of target players.
        // TODO: Allow for playing custom sounds.
        if (entry.namedArgs.containsKey("pitch")) {
            pitch = NumberTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "pitch"));
            if (entry.namedArgs.containsKey("min_volume")) {
                min_volume = NumberTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "min_volume"));
                loc.getInternal().world.playSound(type.get(), loc.getInternal().toVector3d(), volume.getInternal(), pitch.getInternal(), min_volume.getInternal());
                if (queue.shouldShowGood()) {
                    queue.outGood("Successfully played the sound of type '" + ColorSet.emphasis + type.get().getName() +
                            ColorSet.good + "' with volume of " + ColorSet.emphasis + volume.debug() + ColorSet.good +
                            ", pitch of " + ColorSet.emphasis + pitch.debug() + ColorSet.good + " and minimum volume of " +
                            ColorSet.emphasis + min_volume.debug() + ColorSet.good + " at location " + ColorSet.emphasis +
                            loc.debug() + ColorSet.good + "!");
                }
            }
            else {
                loc.getInternal().world.playSound(type.get(), loc.getInternal().toVector3d(), volume.getInternal(), pitch.getInternal());
                if (queue.shouldShowGood()) {
                    queue.outGood("Successfully played the sound of type '" + ColorSet.emphasis + type.get().getName() +
                            ColorSet.good + "' with volume of " + ColorSet.emphasis + volume.debug() + ColorSet.good +
                            " and pitch of " + ColorSet.emphasis + pitch.debug() + ColorSet.good + " at location " +
                            ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
                }
            }
        }
        else {
            loc.getInternal().world.playSound(type.get(), loc.getInternal().toVector3d(), volume.getInternal());
            if (queue.shouldShowGood()) {
                queue.outGood("Successfully played the sound of type '" + ColorSet.emphasis + type.get().getName() +
                        ColorSet.good + "' with volume of " + ColorSet.emphasis + volume.debug() + ColorSet.good +
                        " at location " + ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
            }
        }

    }
}
