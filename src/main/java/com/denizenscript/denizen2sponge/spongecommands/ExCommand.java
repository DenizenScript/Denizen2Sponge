package com.denizenscript.denizen2sponge.spongecommands;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.AbstractSender;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import com.denizenscript.denizen2sponge.utilities.PlayerSender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.minecart.CommandBlockMinecart;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class ExCommand implements CommandExecutor {

    // <--[explanation]
    // @Name The Ex Command
    // @Group Sponge Commands
    // @Description
    // The /ex command lets you execute any code directly from the console or in-game chat!
    //
    // A regular example is: /ex echo 'hello world!'
    // By default, it will feed back all output to you if you are in-game.
    // To disable this do, run: /ex -q <command>
    // specifically with the -q flag.
    // To run multiple commands, simply separate them with dashes. EG: /ex command - command - command
    //
    // You can use definitions: source (returns TextTag of player, block, minecart, or server),
    // player (for source==player), location (for source==block), or entity (for source==minecart).
    //
    // This can be used for such quick helpers as: /ex reload
    // TODO: Explain better
    // -->

    public static void register() {
        CommandSpec cmd = CommandSpec.builder()
                .description(Text.of("Executes a Denizen2 command. Use -q for quiet."))
                .permission("denizen2.commands.ex")
                .arguments(GenericArguments.remainingRawJoinedStrings(Text.of("dCommand")))
                .executor(new ExCommand())
                .build();
        Sponge.getCommandManager().register(Denizen2Sponge.instance, cmd, "ex", "denizenex", "dex", "dexecute", "execute");
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        ArrayList<String> argset = new ArrayList<>(commandContext.getAll("dCommand"));
        boolean quiet = false;
        if (argset.size() > 0 && argset.get(0).equals("-q")) {
            quiet = true;
            argset.remove(0);
        }
        String cmd = CoreUtilities.concat(argset, " ");
        MapTag defs = new MapTag();
        AbstractSender send = null;
        if (commandSource instanceof Player) {
            defs.getInternal().put("source", new TextTag("player"));
            defs.getInternal().put("player", new PlayerTag((Player) commandSource));
            send = new PlayerSender((Player) commandSource);
        }
        else if (commandSource instanceof CommandBlock) {
            defs.getInternal().put("source", new TextTag("block"));
            defs.getInternal().put("location", new LocationTag(((CommandBlock) commandSource).getLocation()));
        }
        else if (commandSource instanceof CommandBlockMinecart) {
            defs.getInternal().put("source", new TextTag("minecart"));
            defs.getInternal().put("entity", new EntityTag((CommandBlockMinecart) commandSource));
        }
        else {
            defs.getInternal().put("source", new TextTag("server"));
        }
        Denizen2Core.runString(cmd, defs, quiet ? null : send);
        return CommandResult.empty();
    }
}
