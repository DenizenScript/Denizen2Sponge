package org.mcmonkey.denizen2sponge.spongecommands;

import org.mcmonkey.denizen2core.Denizen2Core;
import org.mcmonkey.denizen2core.tags.objects.BooleanTag;
import org.mcmonkey.denizen2core.tags.objects.MapTag;
import org.mcmonkey.denizen2core.utilities.CoreUtilities;
import org.mcmonkey.denizen2sponge.Denizen2Sponge;
import org.mcmonkey.denizen2sponge.tags.objects.LocationTag;
import org.mcmonkey.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
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
        String cmd = CoreUtilities.concat(new ArrayList<>(commandContext.getAll("dCommand")), " ");
        // TODO: Redirect output to the commandSource!
        MapTag defs = new MapTag();
        if (commandSource instanceof Player) {
            defs.getInternal().put("is_server", new BooleanTag(false));
            defs.getInternal().put("is_player", new BooleanTag(true));
            defs.getInternal().put("player", new PlayerTag((Player) commandSource));
        }
        else if (commandSource instanceof CommandBlockSource) {
            defs.getInternal().put("is_server", new BooleanTag(false));
            defs.getInternal().put("is_player", new BooleanTag(false));
            defs.getInternal().put("is_block", new BooleanTag(true));
            defs.getInternal().put("location", new LocationTag(((CommandBlockSource) commandSource).getLocation()));
        }
        else {
            defs.getInternal().put("is_server", new BooleanTag(true));
            defs.getInternal().put("is_player", new BooleanTag(false));
            defs.getInternal().put("is_block", new BooleanTag(false));
        }
        Denizen2Core.runString(cmd, defs);
        // TODO: Scrap this output in favor of outputting debug optionally.
        commandSource.sendMessage(Text.builder("Command executing... see console for details!").color(TextColors.YELLOW).toText());
        return CommandResult.empty();
    }
}
