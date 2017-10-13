package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TimeTag;
import com.denizenscript.denizen2core.utilities.Action;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Utilities {

    public static double getHandReach(Player player) {
        return player.gameMode().equals(GameModes.CREATIVE) ? 5.0 : 4.0;
    }

    public static boolean flagIsValidAndNotExpired(Action<String> error, MapTag flags, String flagName) {
        boolean b = false;
        if (flags.getInternal().containsKey(flagName)) {
            b = true;
            MapTag subMap = MapTag.getFor(error, flags.getInternal().get(flagName));
            if (subMap.getInternal().containsKey("duration")) {
                    TimeTag tt = TimeTag.getFor(error, subMap.getInternal().get("duration"));
                    if (tt.getInternal().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
                        b = false;
                    }
                }
            }
        return b;
    }
}
