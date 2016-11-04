package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.utilities.CoreUtilities;

public class Settings {

    public static boolean enforceLocale() {
        return CoreUtilities.toLowerCase(Denizen2Sponge.instance.config.getString("Enforce Locale", "true")).equals("true");
    }

    public static boolean debugGeneral() {
        return CoreUtilities.toLowerCase(Denizen2Sponge.instance.config.getString("Debug.General", "true")).equals("true");
    }
}
