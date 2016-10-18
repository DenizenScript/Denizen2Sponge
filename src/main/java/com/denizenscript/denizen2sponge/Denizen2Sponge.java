package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2core.utilities.yaml.YAMLConfiguration;
import com.denizenscript.denizen2sponge.commands.entity.EditEntityCommand;
import com.denizenscript.denizen2sponge.commands.entity.SpawnCommand;
import com.denizenscript.denizen2sponge.commands.player.ActionBarCommand;
import com.denizenscript.denizen2sponge.commands.player.TellCommand;
import com.denizenscript.denizen2sponge.commands.server.ExecuteCommand;
import com.denizenscript.denizen2sponge.commands.world.EditBlockCommand;
import com.denizenscript.denizen2sponge.commands.world.PlayEffectCommand;
import com.denizenscript.denizen2sponge.commands.world.SetBlockCommand;
import com.denizenscript.denizen2sponge.events.entity.EntityDamagedScriptEvent;
import com.denizenscript.denizen2sponge.events.player.PlayerBreaksBlockScriptEvent;
import com.denizenscript.denizen2sponge.events.player.PlayerChatsScriptEvent;
import com.denizenscript.denizen2sponge.events.player.PlayerPlacesBlockScriptEvent;
import com.denizenscript.denizen2sponge.events.player.PlayerRightClicksBlockScriptEvent;
import com.denizenscript.denizen2sponge.events.player.PlayerRightClicksEntityScriptEvent;
import com.denizenscript.denizen2sponge.events.server.ClientPingsServerScriptEvent;
import com.denizenscript.denizen2sponge.events.world.BlockChangeScriptEvent;
import com.denizenscript.denizen2sponge.events.world.EntitySpawnScriptEvent;
import com.denizenscript.denizen2sponge.spongecommands.ExCommand;
import com.denizenscript.denizen2sponge.spongeevents.Denizen2SpongeLoadedEvent;
import com.denizenscript.denizen2sponge.spongeevents.Denizen2SpongeLoadingEvent;
import com.denizenscript.denizen2sponge.tags.handlers.*;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.InputStream;

/**
 * Main plugin class for Denizen2Sponge.
 */
@Plugin(id = Denizen2Sponge.PLUGIN_ID, name = Denizen2Sponge.PLUGIN_NAME, version = Denizen2Sponge.PLUGIN_VERSION)
public class Denizen2Sponge {

    public final static String PLUGIN_ID = "denizen2sponge";

    public final static String PLUGIN_NAME = "Denizen2Sponge";

    public final static String PLUGIN_VERSION = PomData.VERSION + " (build " + PomData.BUILD_NUMBER + ")";

    public final static String version;

    public static PluginContainer plugin;

    public static Denizen2Sponge instance;

    public static char colorChar = '\u00A7';

    public static Cause getGenericCause() {
        return Cause.of(NamedCause.of("plugin", plugin));
    }

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
    public void onServerStart(GamePreInitializationEvent event) {
        // Setup
        instance = this;
        plugin = Sponge.getPluginManager().getPlugin(PLUGIN_ID).orElse(null);
        // Colors
        ColorSet.base = colorChar + "7";
        ColorSet.good = colorChar + "a";
        ColorSet.warning = colorChar + "c";
        ColorSet.emphasis = colorChar + "b";
        // Denizen2
        Denizen2Core.init(new Denizen2SpongeImplementation());
        // Ensure the scripts and addons folders exist
        Denizen2Core.getImplementation().getScriptsFolder().mkdirs();
        Denizen2Core.getImplementation().getAddonsFolder().mkdirs();
        // Commands: Entity
        Denizen2Core.register(new EditEntityCommand());
        Denizen2Core.register(new SpawnCommand());
        // Commands: Player
        Denizen2Core.register(new ActionBarCommand());
        Denizen2Core.register(new TellCommand());
        // Commands: Server
        Denizen2Core.register(new ExecuteCommand());
        // Commands: World
        Denizen2Core.register(new EditBlockCommand());
        Denizen2Core.register(new PlayEffectCommand());
        Denizen2Core.register(new SetBlockCommand());
        // Events: Entity
        Denizen2Core.register(new EntityDamagedScriptEvent());
        // Events: Player
        Denizen2Core.register(new PlayerBreaksBlockScriptEvent());
        Denizen2Core.register(new PlayerChatsScriptEvent());
        Denizen2Core.register(new PlayerPlacesBlockScriptEvent());
        Denizen2Core.register(new PlayerRightClicksBlockScriptEvent());
        Denizen2Core.register(new PlayerRightClicksEntityScriptEvent());
        // Events: Server
        Denizen2Core.register(new ClientPingsServerScriptEvent());
        // Events: World
        Denizen2Core.register(new BlockChangeScriptEvent());
        Denizen2Core.register(new EntitySpawnScriptEvent());
        // Tag Handlers: Sponge Basics
        Denizen2Core.register(new BlockTypeTagBase());
        Denizen2Core.register(new ContextTagBase());
        Denizen2Core.register(new EntityTagBase());
        Denizen2Core.register(new EntityTypeTagBase());
        Denizen2Core.register(new FormattedTextTagBase());
        Denizen2Core.register(new ItemTypeTagBase());
        Denizen2Core.register(new LocationTagBase());
        Denizen2Core.register(new OfflinePlayerTagBase());
        Denizen2Core.register(new PlayerTagBase());
        Denizen2Core.register(new TextsTagBase());
        Denizen2Core.register(new WorldTagBase());
        // Sponge Commands
        ExCommand.register();
        // Call loading event for sub-plugins registering things
        Sponge.getEventManager().post(new Denizen2SpongeLoadingEvent(getGenericCause()));
        // Load Denizen2
        Denizen2Core.start();
        // Central loop
        Sponge.getScheduler().createTaskBuilder().intervalTicks(1).execute(() -> Denizen2Core.tick(0.05)).submit(this);
        // Call loaded event for sub-plugins to listen for
        Sponge.getEventManager().post(new Denizen2SpongeLoadedEvent(getGenericCause()));
    }

    public File getMainDirectory() {
        return new File("./assets/Denizen/");
    }

    @Listener
    public void onServerStop(GameStoppedEvent event) {
        // Disable Denizen2
        Denizen2Core.unload();
    }
}
