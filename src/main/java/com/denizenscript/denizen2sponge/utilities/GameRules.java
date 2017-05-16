package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.utilities.CoreUtilities;
import org.spongepowered.api.world.gamerule.DefaultGameRules;

import java.lang.reflect.Field;
import java.util.HashMap;

public class GameRules {

    public final static HashMap<String, String> SpongeToMinecraft = new HashMap<>();

    public final static HashMap<String, String> MinecraftToSponge = new HashMap<>();

    public static void init() {
        try {
            for (Field field : DefaultGameRules.class.getDeclaredFields()) {
                SpongeToMinecraft.put(CoreUtilities.toLowerCase(field.getName()), (String) field.get(null));
                MinecraftToSponge.put((String) field.get(null), CoreUtilities.toLowerCase(field.getName()));
            }
        }
        catch (IllegalAccessException e) {
        }
    }
}
