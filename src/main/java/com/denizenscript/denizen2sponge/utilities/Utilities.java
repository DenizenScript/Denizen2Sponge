package com.denizenscript.denizen2sponge.utilities;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

public class Utilities {

    public static double getHandReach(Player player) {
        return player.gameMode().equals(GameModes.CREATIVE) ? 5.0 : 4.0;
    }
}
