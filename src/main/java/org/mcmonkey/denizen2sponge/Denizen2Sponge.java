package org.mcmonkey.denizen2sponge;

import com.google.inject.Inject;
import org.mcmonkey.denizen2core.Denizen2Core;
import org.mcmonkey.denizen2core.utilities.CoreUtilities;
import org.mcmonkey.denizen2core.utilities.debugging.ColorSet;
import org.mcmonkey.denizen2core.utilities.debugging.Debug;
import org.mcmonkey.denizen2core.utilities.yaml.YAMLConfiguration;
import org.mcmonkey.denizen2sponge.events.player.PlayerBreaksBlockScriptEvent;
import org.mcmonkey.denizen2sponge.events.server.ClientPingsServerScriptEvent;
import org.mcmonkey.denizen2sponge.spongecommands.ExCommand;
import org.slf4j.Logger;
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

    public static Denizen2Sponge instance;

    public static char colorChar = '\u00A7';

    @Inject
    public Logger logger;


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
        // Setup
        instance = this;
        plugin = Sponge.getPluginManager().getPlugin(PluginID).orElse(null);
        // Colors
        ColorSet.base = colorChar + "7";
        ColorSet.good = colorChar + "a";
        ColorSet.warning = colorChar + "c";
        ColorSet.emphasis = colorChar + "b";
        // Denizen2
        Denizen2Core.init(new Denizen2SpongeImplementation());
        // Events: Player
        Denizen2Core.register(new PlayerBreaksBlockScriptEvent());
        // Events: Server
        Denizen2Core.register(new ClientPingsServerScriptEvent());
        // Load Denizen2
        Denizen2Core.load();
        // Commands
        ExCommand.register();
        // Central loop
        Sponge.getScheduler().createTaskBuilder().intervalTicks(1).execute(new Runnable() {
            @Override
            public void run() {
                Denizen2Core.tick(0.05);
            }
        }).submit(this);
    }
}
