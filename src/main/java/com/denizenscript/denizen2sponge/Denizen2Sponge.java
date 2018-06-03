package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2core.utilities.yaml.YAMLConfiguration;
import com.denizenscript.denizen2sponge.commands.entity.*;
import com.denizenscript.denizen2sponge.commands.items.CreateInventoryCommand;
import com.denizenscript.denizen2sponge.commands.items.ForgetInventoryCommand;
import com.denizenscript.denizen2sponge.commands.items.GiveCommand;
import com.denizenscript.denizen2sponge.commands.items.RememberInventoryCommand;
import com.denizenscript.denizen2sponge.commands.player.*;
import com.denizenscript.denizen2sponge.commands.server.AnnounceCommand;
import com.denizenscript.denizen2sponge.commands.server.ExecuteCommand;
import com.denizenscript.denizen2sponge.commands.server.SaveDataCommand;
import com.denizenscript.denizen2sponge.commands.server.ShutdownCommand;
import com.denizenscript.denizen2sponge.commands.world.*;
import com.denizenscript.denizen2sponge.events.entity.*;
import com.denizenscript.denizen2sponge.events.player.*;
import com.denizenscript.denizen2sponge.events.server.ClientPingsServerScriptEvent;
import com.denizenscript.denizen2sponge.events.server.CommandSentScriptEvent;
import com.denizenscript.denizen2sponge.events.server.InternalScriptEvent;
import com.denizenscript.denizen2sponge.events.server.ServerStopsScriptEvent;
import com.denizenscript.denizen2sponge.events.world.*;
import com.denizenscript.denizen2sponge.spongecommands.ExCommand;
import com.denizenscript.denizen2sponge.spongeevents.Denizen2SpongeLoadedEvent;
import com.denizenscript.denizen2sponge.spongeevents.Denizen2SpongeLoadingEvent;
import com.denizenscript.denizen2sponge.spongescripts.AdvancementScript;
import com.denizenscript.denizen2sponge.spongescripts.EntityScript;
import com.denizenscript.denizen2sponge.spongescripts.GameCommandScript;
import com.denizenscript.denizen2sponge.spongescripts.ItemScript;
import com.denizenscript.denizen2sponge.tags.handlers.*;
import com.denizenscript.denizen2sponge.tags.objects.*;
import com.denizenscript.denizen2sponge.utilities.GameRules;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

    public YAMLConfiguration config;

    public static char colorChar = '\u00A7';

    public static Cause getGenericCause() {
        // TODO: Decipher new cause system, make a nice generic cause
        return Cause.builder().append(plugin).build(EventContext.empty());
    }

    public static Text parseColor(String inp) {
        return TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(inp);
    }

    public static final Map<String, InventoryTag> rememberedInventories = new HashMap<>();

    @Inject
    public Logger logger;

    public static final Map<String, ItemScript> itemScripts = new HashMap<>();

    public static final Map<String, EntityScript> entityScripts = new HashMap<>();

    static {
        YAMLConfiguration tconfig = null;
        try {
            InputStream is = Denizen2Sponge.class.getResourceAsStream("/denizen2sponge.yml");
            tconfig = YAMLConfiguration.load(CoreUtilities.streamToString(is));
            is.close();
        }
        catch (Exception ex) {
            Debug.exception(ex);
        }
        if (tconfig == null) {
            version = "UNKNOWN (Error reading version file!)";
        }
        else {
            version = tconfig.getString("VERSION", "UNKNOWN") + " (build " + tconfig.getString("BUILD_NUMBER", "UNKNOWN") + ")";
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
        // Load config (save default if it doesn't exist)
        saveDefaultConfig();
        loadConfig();
        // Denizen2
        Denizen2Core.init(new Denizen2SpongeImplementation());
        // Ensure the scripts, addons, and data folders exist
        Denizen2Core.getImplementation().getScriptsFolder().mkdirs();
        Denizen2Core.getImplementation().getAddonsFolder().mkdirs();
        Denizen2Core.getImplementation().getScriptDataFolder().mkdirs();
        // Commands: Entity
        Denizen2Core.register(new AbsorptionCommand());
        Denizen2Core.register(new AddAITaskCommand());
        Denizen2Core.register(new AirCommand());
        Denizen2Core.register(new BurnCommand());
        Denizen2Core.register(new CastCommand());
        Denizen2Core.register(new DefuseCommand());
        Denizen2Core.register(new DetonateCommand());
        Denizen2Core.register(new DropCommand());
        Denizen2Core.register(new EditEntityCommand());
        Denizen2Core.register(new EquipCommand());
        Denizen2Core.register(new FlagCommand());
        Denizen2Core.register(new GlowCommand());
        Denizen2Core.register(new HealCommand());
        Denizen2Core.register(new HurtCommand());
        Denizen2Core.register(new InvisibleCommand());
        Denizen2Core.register(new LookAtCommand());
        Denizen2Core.register(new MountCommand());
        Denizen2Core.register(new PrimeCommand());
        Denizen2Core.register(new RemoveAITasksCommand());
        Denizen2Core.register(new RemoveCommand());
        Denizen2Core.register(new SpawnCommand());
        Denizen2Core.register(new TargetCommand());
        Denizen2Core.register(new TeleportCommand());
        Denizen2Core.register(new UnflagCommand());
        Denizen2Core.register(new VanishCommand());
        // Commands: Item
        Denizen2Core.register(new CreateInventoryCommand());
        Denizen2Core.register(new ForgetInventoryCommand());
        Denizen2Core.register(new RememberInventoryCommand());
        // Commands: Player
        Denizen2Core.register(new ActionBarCommand());
        Denizen2Core.register(new AdvancementCommand());
        Denizen2Core.register(new BanCommand());
        Denizen2Core.register(new CooldownCommand());
        Denizen2Core.register(new CreateBossBarCommand());
        Denizen2Core.register(new EditBossBarCommand());
        Denizen2Core.register(new FeedCommand());
        Denizen2Core.register(new GamemodeCommand());
        Denizen2Core.register(new GiveCommand());
        Denizen2Core.register(new HotbarCommand());
        Denizen2Core.register(new KickCommand());
        Denizen2Core.register(new NarrateCommand());
        Denizen2Core.register(new PardonCommand());
        Denizen2Core.register(new RemoveBossBarCommand());
        Denizen2Core.register(new RemoveRespawnCommand());
        Denizen2Core.register(new SetRespawnCommand());
        Denizen2Core.register(new TabListCommand());
        Denizen2Core.register(new TakeCommand());
        Denizen2Core.register(new TellCommand());
        Denizen2Core.register(new TitleCommand());
        // Commands: Server
        Denizen2Core.register(new AnnounceCommand());
        Denizen2Core.register(new ExecuteCommand());
        Denizen2Core.register(new SaveDataCommand());
        Denizen2Core.register(new ShutdownCommand());
        // Commands: World
        Denizen2Core.register(new DeleteWorldCommand());
        Denizen2Core.register(new DifficultyCommand());
        Denizen2Core.register(new EditBlockCommand());
        Denizen2Core.register(new ExplodeCommand());
        Denizen2Core.register(new LoadWorldCommand());
        Denizen2Core.register(new PlayEffectCommand());
        Denizen2Core.register(new PlaySoundCommand());
        Denizen2Core.register(new RemoveGameRuleCommand());
        Denizen2Core.register(new SetBlockCommand());
        Denizen2Core.register(new SetGameRuleCommand());
        Denizen2Core.register(new StrikeCommand());
        Denizen2Core.register(new UnloadWorldCommand());
        Denizen2Core.register(new ViewDistanceCommand());
        Denizen2Core.register(new WeatherCommand());
        // Events: Entity
        Denizen2Core.register(new EntityCollidesWithBlockScriptEvent());
        Denizen2Core.register(new EntityCollidesWithEntityScriptEvent());
        Denizen2Core.register(new EntityDamagedScriptEvent());
        Denizen2Core.register(new EntityDiesScriptEvent());
        Denizen2Core.register(new EntityEntersAreaScriptEvent());
        Denizen2Core.register(new EntityKilledScriptEvent());
        Denizen2Core.register(new EntityLeavesAreaScriptEvent());
        Denizen2Core.register(new EntityMovesScriptEvent());
        Denizen2Core.register(new EntitySpawnsScriptEvent());
        Denizen2Core.register(new ProjectileImpactsBlockScriptEvent());
        Denizen2Core.register(new ProjectileImpactsEntityScriptEvent());
        Denizen2Core.register(new ProjectileLaunchedScriptEvent());
        // Events: Player
        Denizen2Core.register(new ExperienceChangesScriptEvent());
        Denizen2Core.register(new ItemCooldownEndsScriptEvent());
        Denizen2Core.register(new ItemCooldownStartsScriptEvent());
        Denizen2Core.register(new LevelChangesScriptEvent());
        Denizen2Core.register(new PlayerBreaksBlockScriptEvent());
        Denizen2Core.register(new PlayerChangesGamemodeScriptEvent());
        Denizen2Core.register(new PlayerChatsScriptEvent());
        Denizen2Core.register(new PlayerDisconnectsScriptEvent());
        Denizen2Core.register(new PlayerFinishesUsingItemScriptEvent());
        Denizen2Core.register(new PlayerJoinsScriptEvent());
        Denizen2Core.register(new PlayerKeepsUsingItemScriptEvent());
        Denizen2Core.register(new PlayerLeftClicksBlockScriptEvent());
        Denizen2Core.register(new PlayerLeftClicksEntityScriptEvent());
        Denizen2Core.register(new PlayerLeftClicksScriptEvent());
        Denizen2Core.register(new PlayerPlacesBlockScriptEvent());
        Denizen2Core.register(new PlayerRightClicksBlockScriptEvent());
        Denizen2Core.register(new PlayerRightClicksEntityScriptEvent());
        Denizen2Core.register(new PlayerRightClicksScriptEvent());
        Denizen2Core.register(new PlayerStartsUsingItemScriptEvent());
        Denizen2Core.register(new PlayerStopsUsingItemScriptEvent());
        Denizen2Core.register(new StatisticChangesScriptEvent());
        // Events: Server
        Denizen2Core.register(new ClientPingsServerScriptEvent());
        Denizen2Core.register(new CommandSentScriptEvent());
        Denizen2Core.register(new InternalScriptEvent());
        Denizen2Core.register(new ServerStopsScriptEvent());
        // Events: World
        Denizen2Core.register(new BlockChangesScriptEvent());
        Denizen2Core.register(new BlockFadesScriptEvent());
        Denizen2Core.register(new ExplosionOccursScriptEvent());
        Denizen2Core.register(new PortalFormedScriptEvent());
        Denizen2Core.register(new WeatherChangesScriptEvent());
        Denizen2Core.register(new WorldLoadsScriptEvent());
        Denizen2Core.register(new WorldUnloadsScriptEvent());
        // Tag Handlers: Sponge Basics
        Denizen2Core.register(new AmpersandTagBase());
        Denizen2Core.register(new BlockTypeTagBase());
        Denizen2Core.register(new ColorTagBase());
        Denizen2Core.register(new ContextTagBase());
        Denizen2Core.register(new CuboidTagBase());
        Denizen2Core.register(new EntityTagBase());
        Denizen2Core.register(new EntityTypeTagBase());
        Denizen2Core.register(new FormattedTextTagBase());
        Denizen2Core.register(new InventoryTagBase());
        Denizen2Core.register(new ItemTagBase());
        Denizen2Core.register(new ItemTypeTagBase());
        Denizen2Core.register(new LocationTagBase());
        Denizen2Core.register(new PlayerTagBase());
        Denizen2Core.register(new ServerTagBase());
        Denizen2Core.register(new TextsTagBase());
        Denizen2Core.register(new WorldTagBase());
        // Sponge Script Types
        Denizen2Core.register("command", GameCommandScript::new);
        Denizen2Core.register("advancement", AdvancementScript::new);
        Denizen2Core.register("item", ItemScript::new);
        Denizen2Core.register("entity", EntityScript::new);
        // Tag Types
        Denizen2Core.customSaveLoaders.put("BlockTypeTag", BlockTypeTag::getFor);
        Denizen2Core.customSaveLoaders.put("CuboidTag", CuboidTag::getFor);
        Denizen2Core.customSaveLoaders.put("EntityTag", EntityTag::getFor);
        Denizen2Core.customSaveLoaders.put("EntityTypeTag", EntityTypeTag::getFor);
        Denizen2Core.customSaveLoaders.put("FormattedTextTag", FormattedTextTag::getFor);
        Denizen2Core.customSaveLoaders.put("InventoryTag", InventoryTag::getFor);
        Denizen2Core.customSaveLoaders.put("ItemTag", ItemTag::getFor);
        Denizen2Core.customSaveLoaders.put("ItemTypeTag", ItemTypeTag::getFor);
        Denizen2Core.customSaveLoaders.put("LocationTag", LocationTag::getFor);
        Denizen2Core.customSaveLoaders.put("PlayerTag", PlayerTag::getFor);
        Denizen2Core.customSaveLoaders.put("WorldTag", WorldTag::getFor);
        Denizen2Core.customSaveLoaders.put("ServerBaseTag", (e, s) -> new ServerTagBase.ServerBaseTag());
        Denizen2Core.customSaveLoaders.put("TextsBaseTag", (e, s) -> new TextsTagBase.TextsBaseTag());
        // Sponge Commands
        ExCommand.register();
        // Sponge related Helpers
        FlagHelper.register();
        GameRules.init();
        // Server Flags
        loadServerFlags();
        // Call loading event for sub-plugins registering things
        Sponge.getEventManager().post(new Denizen2SpongeLoadingEvent(getGenericCause()));
        // Load Denizen2
        Denizen2Core.start();
        // Build loaded advancements
        AdvancementScript.buildAll();
        // Central loop
        Sponge.getScheduler().createTaskBuilder().intervalTicks(1).execute(() -> Denizen2Core.tick(0.05)).submit(this);
        // Call loaded event for sub-plugins to listen for
        Sponge.getEventManager().post(new Denizen2SpongeLoadedEvent(getGenericCause()));
        // TODO: Config option -> readyToSpamEvents = true;
    }

    public File getMainDirectory() {
        return new File("./config/denizen/");
    }

    @Listener
    public void onServerStop(GameStoppedEvent event) {
        // Save server data
        saveServerFlags();
        // Disable Denizen2
        Denizen2Core.unload();
    }

    public File getConfigFile() {
        return new File(getMainDirectory(), "./config/config.yml");
    }

    public void loadServerFlags() {
        try {
            if (!getServerFlagsFile().exists()) {
                serverFlagMap = new MapTag();
                return;
            }
            InputStream is = new FileInputStream(getServerFlagsFile());
            String str = CoreUtilities.streamToString(is);
            is.close();
            serverFlagMap = (MapTag) Denizen2Core.loadFromSaved(Debug::error, str);
        }
        catch (Exception e) {
            Debug.exception(e);
        }
    }

    public void saveServerFlags() {
        try {
            String flags = serverFlagMap.savable();
            OutputStream os = new FileOutputStream(getServerFlagsFile(), false);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(flags);
            osw.flush();
            os.flush();
            osw.close();
            os.close();
        }
        catch (Exception e) {
            Debug.exception(e);
        }
    }

    public MapTag serverFlagMap = new MapTag();

    public File getServerFlagsFile() {
        return new File(getMainDirectory(), "./server_flags.yml");
    }

    private void saveDefaultConfig() {
        File cf = getConfigFile();
        if (!cf.exists()) {
            cf.getParentFile().mkdirs();
            try {
                InputStream is = getClass().getResourceAsStream("default_config.yml");
                String res = CoreUtilities.streamToString(is);
                is.close();
                PrintWriter writer = new PrintWriter(cf);
                writer.write(res);
                writer.close();
            }
            catch (IOException ex) {
                Debug.exception(ex);
            }
        }
    }

    private void loadConfig() {
        config = null;
        try {
            File cf = getConfigFile();
            if (!cf.exists()) {
                saveDefaultConfig();
            }
            config = YAMLConfiguration.load(CoreUtilities.streamToString(new FileInputStream(cf)));
        }
        catch (IOException ex) {
            Debug.exception(ex);
        }
        if (config == null) {
            config = new YAMLConfiguration();
        }
    }
}
