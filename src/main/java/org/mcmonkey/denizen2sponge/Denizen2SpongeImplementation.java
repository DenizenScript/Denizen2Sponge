package org.mcmonkey.denizen2sponge;

import org.mcmonkey.denizen2core.Denizen2Implementation;
import org.mcmonkey.denizen2core.commands.CommandEntry;
import org.mcmonkey.denizen2core.commands.CommandQueue;

import java.io.File;

public class Denizen2SpongeImplementation extends Denizen2Implementation {
    @Override
    public void outputException(Exception e) {
        Denizen2Sponge.instance.logger.trace("Internal exception", e);
    }

    @Override
    public void outputGood(String s) {
        // TODO: Color?
        Denizen2Sponge.instance.logger.info("+> [Good] " + s);
    }

    @Override
    public void outputInfo(String s) {
        // TODO: Color?
        Denizen2Sponge.instance.logger.info("+> [Info] " + s);
    }

    @Override
    public void outputInvalid(CommandQueue queue, CommandEntry entry) {
        queue.handleError(entry, "Invalid/unknown command: " + entry.originalLine + "... -> " + entry.arguments);
    }

    @Override
    public void outputError(String message) {
        System.out.println("[Error] " + message);
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
