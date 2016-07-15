package org.mcmonkey.denizen2sponge.spongecommands;

import org.mcmonkey.denizen2core.Denizen2Core;
import org.mcmonkey.denizen2core.utilities.CoreUtilities;
import org.mcmonkey.denizen2sponge.Denizen2Sponge;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

public class ExCommand implements CommandExecutor {

    public static void register() {
        CommandSpec cmd = CommandSpec.builder()
                .description(Text.of("Executes a Denizen2 command."))
                .permission("denizen2.commands.ex")
                .arguments(GenericArguments.allOf(GenericArguments.string(Text.of("dCommand"))))
                .executor(new ExCommand())
                .build();
        Sponge.getCommandManager().register(Denizen2Sponge.instance, cmd, "ex", "denizenex", "dex", "dexecute", "execute");
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        String cmd = CoreUtilities.concat(new ArrayList<String>(commandContext.getAll("dCommand")), " ");
        // TODO: Redirect output to the commandSource!
        Denizen2Core.runString(cmd);
        commandSource.sendMessage(Text.builder("Command executing... see console for details!").color(TextColors.YELLOW).toText()); // TODO: Scrap!
        return CommandResult.empty();
    }
}
