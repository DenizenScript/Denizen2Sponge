package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.RespawnLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RemoveRespawnCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.5.5
    // @Name removerespawn
    // @Arguments <player> <world>
    // @Short removes a respawn location for the player.
    // @Updated 2018/06/15
    // @Group Player
    // @Warning This command doesn't work due to not being implemented in Sponge yet.
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Removes the respawn location for the player in the specified world.
    // Related commands: <@link command setrespawn>setrespawn<@/link>.
    // @Example
    // # This example removes the player's respawn point in their current world.
    // - removerespawn <player> <player.location.world>
    // -->

    @Override
    public String getName() {
        return "removerespawn";
    }

    @Override
    public String getArguments() {
        return "<player> <world>";
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
        PlayerTag playerTag = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        Player player = playerTag.getOnline(queue.error);
        WorldTag world = WorldTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Optional<Map<UUID, RespawnLocation>> mapOpt = player.get(Keys.RESPAWN_LOCATIONS);
        Map<UUID, RespawnLocation> map;
        if (mapOpt.isPresent()) {
            map = mapOpt.get();
            map.remove(world.getInternal().getUniqueId());
        }
        else {
            map = new HashMap<>();
        }
        player.offer(Keys.RESPAWN_LOCATIONS, map);
        if (queue.shouldShowGood()) {
            queue.outGood("Removing respawn location for player '" + ColorSet.emphasis + playerTag.debug()
                    + ColorSet.good + "' in world '" + ColorSet.emphasis + world.debug() + ColorSet.good + "'!");
        }
    }
}
