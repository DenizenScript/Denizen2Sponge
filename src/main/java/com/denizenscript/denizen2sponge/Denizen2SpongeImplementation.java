package com.denizenscript.denizen2sponge;

import com.denizenscript.denizen2core.Denizen2Implementation;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;

public class Denizen2SpongeImplementation extends Denizen2Implementation {

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
            Sponge.getServer().getConsole().sendMessage(TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize(("     " + ste.toString())));
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
}
