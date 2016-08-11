package org.mcmonkey.denizen2sponge;

import org.mcmonkey.denizen2core.Denizen2Implementation;
import org.mcmonkey.denizen2core.commands.CommandEntry;
import org.mcmonkey.denizen2core.commands.CommandQueue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;

public class Denizen2SpongeImplementation extends Denizen2Implementation {

    @Override
    public boolean generalDebug() {
        return true; // TODO: Config!
    }

    @Override
    public void outputException(Exception e) {
        Sponge.getServer().getConsole().sendMessage(Text.builder("+> Internal exception! Trace follows: ").color(TextColors.RED).build());
        Denizen2Sponge.instance.logger.trace("Internal exception", e);
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
