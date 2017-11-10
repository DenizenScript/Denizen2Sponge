package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.weather.WeatherEffect;

public class StrikeCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name strike
    // @Arguments <location>
    // @Short strikes lightning.
    // @Updated 2017/11/10
    // @Group World
    // @Minimum 1
    // @Maximum 1
    // @Named ambient (BooleanTag) Sets whether the lightning will affect entities.
    // @Description
    // Strikes lightning upon a location. Optionally specify whether it will affect entities
    // or not. Damaging lightning will also charge creepers and turn pigs into pig zombies.
    // @Example
    // # This example strikes a damaging lightning upon the player.
    // - strike <player.location> --ambient false
    // -->

    @Override
    public String getName() {
        return "strike";
    }

    @Override
    public String getArguments() {
        return "<location>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        LocationTag loc = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        WeatherEffect ent = (WeatherEffect) loc.getInternal().world.createEntity(EntityTypes.LIGHTNING,
                loc.getInternal().toVector3d());
        if (entry.namedArgs.containsKey("ambient")) {
            BooleanTag ambient = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "ambient"));
            ent.setEffect(ambient.getInternal());
        }
        loc.getInternal().world.spawnEntity(ent);
        if (queue.shouldShowGood()) {
            queue.outGood("Successfully struck " + ColorSet.emphasis
                    + (ent.isEffect() ? "ambient" : "damaging") + ColorSet.good
                    + " lightning at location " + ColorSet.emphasis + loc.debug() + ColorSet.good + "!");
        }
    }
}
