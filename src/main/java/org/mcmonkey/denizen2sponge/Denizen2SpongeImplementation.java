package org.mcmonkey.denizen2sponge;

import org.mcmonkey.denizen2core.Denizen2Implementation;
import org.mcmonkey.denizen2core.commands.CommandEntry;
import org.mcmonkey.denizen2core.commands.CommandQueue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;

public class Denizen2SpongeImplementation extends Denizen2Implementation {
    @Override
    public void outputException(Exception e) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> Internal exception! Trace follows: ").color(TextColors.RED).build());
        Denizen2Sponge.instance.logger.trace("Internal exception", e);
    }

    @Override
    public void outputGood(String s) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Good] " + s).color(TextColors.GREEN).build());
    }

    @Override
    public void outputInfo(String s) {
        // TODO: Color?
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Info] " + s).color(TextColors.GRAY).build());
    }

    @Override
    public void outputInvalid(CommandQueue queue, CommandEntry entry) {
        queue.handleError(entry, "Invalid/unknown command: " + entry.originalLine + "... -> " + entry.arguments);
    }

    @Override
    public void outputError(String s) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> [Error] " + s).color(TextColors.DARK_RED).build());
    }

    @Override
    public File getScriptsFolder() {
        return new File("./Denizen/scripts/");
    }

    @Override
    public String getImplementationName() {
        return "Denizen2Sponge";
    }

    @Override
    public String getImplementationVersion() {
        return Denizen2Sponge.version;
    }
}
