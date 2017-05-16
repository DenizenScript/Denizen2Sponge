package com.denizenscript.denizen2sponge.commands.world;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import com.denizenscript.denizen2sponge.utilities.GameRules;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class RemoveGameRuleCommand extends AbstractCommand {

    // <--[command]
    // @Name removegamerule
    // @Arguments <world name> <list of rules>
    // @Short removes custom game rules from a world.
    // @Updated 2017/05/16
    // @Group World
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Removes the specified custom game rules from a world.
    // Note: you can't remove default minecraft game rules.
    // @Example
    // # Removes the custom game rules 'xp_multiplier' and
    // # 'currency_multiplier' from world 'Games'.
    // - removegamerule Games xp_multiplier|currency_multiplier
    // -->

    @Override
    public String getName() {
        return "removegamerule";
    }

    @Override
    public String getArguments() {
        return "<world name> <list of rules>";
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
        ListTag list = ListTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (AbstractTagObject rule : list.getInternal()) {
            properties.removeGameRule(rule.toString());
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Removed game rules of world '" + ColorSet.emphasis + properties.getWorldName()
                    + ColorSet.good + "' according to list: " + ColorSet.emphasis + list.debug() + ColorSet.good + "!");
        }
    }
}
