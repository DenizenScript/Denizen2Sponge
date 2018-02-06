package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TimeTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class Utilities {

    public static double getHandReach(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).gameMode().equals(GameModes.CREATIVE) ? 5.0 : 4.0;
        }
        else {
            return 5.0;
        }
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

    public static String getIdWithoutDefaultPrefix(String id) {
        if (id.startsWith("minecraft")) {
            return id.substring("minecraft:".length());
        }
        if (id.startsWith("sponge")) {
            return id.substring("sponge:".length());
        }
        if (id.startsWith("denizen2sponge")) {
            return id.substring("denizen2sponge:".length());
        }
        return id;
    }

    public static Object getTypeWithDefaultPrefix(Class clazz, String name) {
        Optional<?> opt = Sponge.getRegistry().getType(clazz, name);
        if (opt.isPresent()) {
            return opt.get();
        }
        opt = Sponge.getRegistry().getType(clazz, "minecraft:" + name);
        if (opt.isPresent()) {
            return opt.get();
        }
        opt = Sponge.getRegistry().getType(clazz, "sponge:" + name);
        if (opt.isPresent()) {
            return opt.get();
        }
        opt = Sponge.getRegistry().getType(clazz, "denizen2sponge:" + name);
        return opt.orElse(null);
    }
}
