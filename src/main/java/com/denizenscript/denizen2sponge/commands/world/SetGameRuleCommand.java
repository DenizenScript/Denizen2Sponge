package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import com.denizenscript.denizen2sponge.utilities.GameRules;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Map;
import java.util.Optional;

public class SetGameRuleCommand extends AbstractCommand {

    // <--[explanation]
    // @Name Default Game Rules
    // @Group Useful Lists
    // @Description
    // A list of all default game rules can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/world/gamerule/DefaultGameRules.html>default game rule list<@/link>
    // These can be used with the setgamerule command. Note: these Sponge game rule
    // ids are used by Denizen2, but they don't match the Minecraft ones. For example,
    // 'keepInventory' in Minecraft becomes 'keep_inventory' in Sponge/Denizen2.
    // -->

    // <--[command]
    // @Name setgamerule
    // @Arguments <world name> <map of rules>
    // @Short modifies the game rules of a world.
    // @Updated 2017/05/16
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Modifies the existing game rules of a world or creates new ones according
    // to the map specified. Related information: <@link explanation Default Game Rules>default game rules<@/link>.
    // @Example
    // # Sets the default game rule 'mob_griefing' to 'false' in world 'Games'.
    // - setgamerule Games mob_griefing:false
    // @Example
    // # Sets the custom game rules 'xp_multiplier' and 'currency_multiplier' to
    // '3' and '5' respectively in world 'Adventure'.
    // - setgamerule Adventure xp_multiplier:3|currency_multiplier:5
    // -->

    @Override
    public String getName() {
        return "setgamerule";
    }

    @Override
    public String getArguments() {
        return "<world name> <map of rules>";
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
        AbstractTagObject world = entry.getArgumentObject(queue, 0);
        WorldProperties properties;
        if (world instanceof WorldTag) {
            properties = ((WorldTag) world).getInternal().getProperties();
        }
        else {
            Optional<WorldProperties> opt = Sponge.getServer().getWorldProperties(world.toString());
            if (!opt.isPresent()) {
                queue.handleError(entry, "Invalid world specified!");
                return;
            }
            properties = opt.get();
        }
        MapTag map = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> mapEntry : map.getInternal().entrySet()) {
            if (GameRules.SpongeToMinecraft.containsKey(mapEntry.getKey())) {
                properties.setGameRule(GameRules.SpongeToMinecraft.get(mapEntry.getKey()), mapEntry.getValue().toString());
            }
            else {
                properties.setGameRule(mapEntry.getKey(), mapEntry.getValue().toString());
            }
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Modified game rules of world '" + ColorSet.emphasis + properties.getWorldName()
                    + ColorSet.good + "' according to map: " + ColorSet.emphasis + map.debug() + ColorSet.good + "!");
        }
    }
}
