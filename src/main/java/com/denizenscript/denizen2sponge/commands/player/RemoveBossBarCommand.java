package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.utilities.BossBars;
import org.spongepowered.api.boss.ServerBossBar;

public class RemoveBossBarCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.4.0
    // @Name removebossbar
    // @Arguments <id>
    // @Short removes a server BossBar.
    // @Updated 2018/01/29
    // @Group Player
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Removes the specified server BossBar.
    // Related commands: <@link command createbossbar>createbossbar<@/link> and <@link command editbossbar>editbossbar<@/link>.
    // @Example
    // # This example removes the BossBar with ID 'MyBossBar'.
    // - removebossbar MyBossBar
    // -->

    @Override
    public String getName() {
        return "removebossbar";
    }

    @Override
    public String getArguments() {
        return "<id>";
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
        String id = CoreUtilities.toLowerCase(entry.getArgumentObject(queue, 0).toString());
        if (!BossBars.CurrentBossBars.containsKey(id)) {
            queue.handleError(entry, "The BossBar with ID '" + id + "' doesn't exist!");
            return;
        }
        ServerBossBar bar = BossBars.CurrentBossBars.get(id);
        if (queue.shouldShowGood()) {
            queue.outGood("Removing BossBar with ID '" + ColorSet.emphasis + id + ColorSet.good + "'...");
        }
        bar.removePlayers(bar.getPlayers());
        if (queue.shouldShowGood()) {
            queue.outGood("Removing all players from Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "'...");
        }
        BossBars.CurrentBossBars.remove(id);
        if (queue.shouldShowGood()) {
            queue.outGood("Bossbar '" + ColorSet.emphasis + id + ColorSet.good + "' removed successfully!");
        }
    }
}
