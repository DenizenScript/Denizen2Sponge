package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.Denizen2Implementation;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2sponge.spongeevents.Denizen2SpongeReloadEvent;
import com.denizenscript.denizen2sponge.spongescripts.AdvancementScript;
import com.denizenscript.denizen2sponge.spongescripts.GameCommandScript;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.util.HashSet;

public class Denizen2SpongeImplementation extends Denizen2Implementation {

    @Override
    public void preReload() {
        GameCommandScript.clear();
        AdvancementScript.oldAdvancementScripts = new HashSet<>(AdvancementScript.currentAdvancementScripts.keySet());
        AdvancementScript.currentAdvancementScripts.clear();
        Denizen2Sponge.itemScripts.clear();
    }

    @Override
    public void midLoad() {
        // ...?
    }

    @Override
    public void reload() {
        if (!AdvancementScript.oldAdvancementScripts.equals(AdvancementScript.currentAdvancementScripts.keySet())) {
            Debug.info("Advancement scripts have changed, but won't have any effect. " +
                    "Restart the server for the new advancements to be registered!");
        }
        Sponge.getEventManager().post(new Denizen2SpongeReloadEvent(Denizen2Sponge.getGenericCause()));
        // ...?
    }

    @Override
    public boolean generalDebug() {
        return Settings.debugGeneral();
    }

    @Override
    public void outputException(Exception e) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> Internal exception! Trace follows: ").color(TextColors.RED).build());
        if (e instanceof ErrorInducedException) {
            outputError(e.getMessage());
        }
        else {
            trace(e);
        }
    }

    public void trace(Throwable e) {
        if (e == null) {
            return;
        }
        Sponge.getServer().getConsole().sendMessage(TextSerializers.formattingCode(Denizen2Sponge.colorChar)
                .deserialize(("   " + e.getClass().getCanonicalName() + ": " + e.getMessage())));
        for (StackTraceElement ste : e.getStackTrace()) {
            Sponge.getServer().getConsole().sendMessage(TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(("     at " + ste.toString())));
        }
        if (e.getCause() != e) {
            trace(e.getCause());
        }
    }

    @Override
    public void outputGood(String s) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Good] ").append(
                TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(s)).color(TextColors.GREEN).append().build());
    }

    @Override
    public void outputInfo(String s) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Info] ").append(
                TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(s)).color(TextColors.GRAY).build());
    }

    @Override
    public void outputInvalid(CommandQueue queue, CommandEntry entry) {
        queue.handleError(entry, "Invalid/unknown command: " + entry.originalLine + "... -> " + entry.arguments);
    }

    @Override
    public void outputError(String s) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Error] ").append(
                TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(s)).color(TextColors.RED).build());
    }

    @Override
    public File getScriptsFolder() {
        return new File(Denizen2Sponge.instance.getMainDirectory(), "./scripts/");
    }

    @Override
    public File getAddonsFolder() {
        return new File(Denizen2Sponge.instance.getMainDirectory(), "./addons/");
    }

    @Override
    public String getImplementationName() {
        return "Denizen2Sponge";
    }

    @Override
    public String getImplementationVersion() {
        return Denizen2Sponge.version;
    }

    @Override
    public boolean enforceLocale() {
        return Settings.enforceLocale();
    }

    public File getScriptDataFolder() {
        return new File(Denizen2Sponge.instance.getMainDirectory(), "./data/");
    }

    private static File getBaseDirectory() {
        // Genius!
        return new File("./");
    }

    private static File getModsFolder() {
        // TODO: Accuracy!
        return new File(getBaseDirectory(), "./mods/");
    }

    public boolean isSafePath(String file) {
        // TODO: Potentially prevent paths with bad symbolism, EG backslashes or colons, which could be misinterpreted based on environment?
        try {
            File f = new File(getScriptDataFolder(), file);
            String canPath = f.getCanonicalPath();
            if (Settings.noWeirdFiles()) {
                if (canPath.contains(getAddonsFolder().getCanonicalPath())) {
                    return false;
                }
                if (canPath.contains(getScriptsFolder().getCanonicalPath())) {
                    return false;
                }
                if (canPath.contains(getModsFolder().getCanonicalPath())) {
                    return false;
                }
                if (canPath.contains(Denizen2Sponge.instance.getConfigFile().getCanonicalPath())) {
                    return false;
                }
                // TODO: Prevent fiddling with main jar / launcher scripts here?
            }
            if (Settings.noUnrelatedFiles()) {
                if (!canPath.contains(getBaseDirectory().getCanonicalPath())) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e) {
            Debug.exception(e);
            return false;
        }
    }
}
