package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.tags.objects.BooleanTag;

public class Settings {

    private static boolean tryBool(String input) {
        return BooleanTag.getFor((e) -> {
            throw new RuntimeException("Invalid boolean config setting: " + e);
        }, input).getInternal();
    }

    public static boolean enforceLocale() {
        return tryBool(Denizen2Sponge.instance.config.getString("Enforce Locale", "true"));
    }

    public static boolean debugGeneral() {
        return tryBool(Denizen2Sponge.instance.config.getString("Debug.General", "true"));
    }
}
