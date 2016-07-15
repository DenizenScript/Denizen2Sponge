package org.mcmonkey.denizen2sponge;

import org.mcmonkey.denizen2core.utilities.CoreUtilities;
import org.mcmonkey.denizen2core.utilities.debugging.Debug;
import org.mcmonkey.denizen2core.utilities.yaml.YAMLConfiguration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.InputStream;

/**
 * Main plugin class for Denizen2Sponge.
 */
@Plugin(id = Denizen2Sponge.PluginID, name = Denizen2Sponge.PluginName, version=Denizen2Sponge.PluginVersionString)
public class Denizen2Sponge {

    public final static String PluginID = "denizen2sponge";

    public final static String PluginName = "Denizen2Sponge";

    public final static String PluginVersionString = PomData.VERSION + " (build " + PomData.BUILD_NUMBER + ")";

    public final static String version;

    public static PluginContainer plugin;

    static {
        YAMLConfiguration config = null;
        try {
            InputStream is = Denizen2Sponge.class.getResourceAsStream("/denizen2sponge.yml");
            config = YAMLConfiguration.load(CoreUtilities.streamToString(is));
            is.close();
        }
        catch (Exception ex) {
            Debug.exception(ex);
        }
        if (config == null) {
            version = "UNKNOWN (Error reading version file!)";
        }
        else {
            version = config.getString("VERSION", "UNKNOWN") + " (build " + config.getString("BUILD_NUMBER", "UNKNOWN") + ")";
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        plugin = Sponge.getPluginManager().getPlugin(PluginID).orElse(null);
    }
}
