package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.RespawnLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetRespawnCommand extends AbstractCommand {

    // <--[command]
    // @Since 0.5.5
    // @Name setrespawn
    // @Arguments <player> <location>
    // @Short sets a respawn location for the player.
    // @Updated 2018/06/15
    // @Group Player
    // @Warning This command doesn't work due to not being implemented in Sponge yet.
    // @Minimum 2
    // @Maximum 2
    // @Named force (BooleanTag) Sets whether this respawn position is forced.
    // @Description
    // Sets a respawn location for the player. A player can only have one respawn location
    // per world. You can also specify if the position to respawn at is forced or can
    // be varied for safety.
    // Related commands: <@link command removerespawn>removerespawn<@/link>.
    // @Example
    // # This example sets the player's respawn point in their current world to their current location.
    // - setrespawn <player> <player.location>
    // -->

    @Override
    public String getName() {
        return "setrespawn";
    }

    @Override
    public String getArguments() {
        return "<player> <location>";
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
        LocationTag location = LocationTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        RespawnLocation.Builder builder = RespawnLocation.builder().location(location.getInternal().toLocation());
        if (entry.namedArgs.containsKey("force")) {
            BooleanTag force = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "force"));
            builder.forceSpawn(force.getInternal());
        }
        Map<UUID, RespawnLocation> map = player.get(Keys.RESPAWN_LOCATIONS).orElseGet(HashMap::new);
        map.put(location.getInternal().world.getUniqueId(), builder.build());
        player.offer(Keys.RESPAWN_LOCATIONS, map);
        if (queue.shouldShowGood()) {
            queue.outGood("Setting respawn location for player '" + ColorSet.emphasis + playerTag.debug()
                    + ColorSet.good + "' to '" + ColorSet.emphasis + location.debug() + ColorSet.good + "'!");
        }
    }
}
