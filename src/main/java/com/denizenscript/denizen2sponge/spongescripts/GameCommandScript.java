package com.denizenscript.denizen2sponge.spongescripts;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.commands.CommandScriptSection;
import com.denizenscript.denizen2core.scripts.CommandScript;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.AbstractSender;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2core.utilities.yaml.YAMLConfiguration;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameCommandScript extends CommandScript implements CommandExecutor {

    public static List<GameCommandScript> currentCommandScripts = new ArrayList<>();

    public static void clear() {
        for (GameCommandScript script : currentCommandScripts) {
            script.disable();
        }
        currentCommandScripts.clear();
    }

    // <--[explanation]
    // @Name In-Game Command Scripts
    // @Group Script Types
    // @Description
    // An in-game command script is a script that handles in-game commands.
    // It is simply identified with the type "command".
    // -->

    public GameCommandScript(String name, YAMLConfiguration section) {
        super(name, section);
        if (section.isList("name")) {
            cmdName = section.getStringList("name");
        }
        else {
            cmdName = new ArrayList<>();
            cmdName.add(section.getString("name"));
        }
        cmdDesc = section.getString("description", "No description.");
        cmdPerm = section.getString("permission", null);
    }

    public List<String> cmdName;

    public String cmdDesc;

    public String cmdPerm;

    @Override
    public boolean isExecutable(String section) {
        return !section.startsWith("constant");
    }

    public CommandScriptSection getSection(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return sections.get(CoreUtilities.toLowerCase(name));
    }

    @Override
    public boolean init() {
        if (super.init()) {
            register();
            return true;
        }
        return false;
    }

    public CommandMapping cmdMapping;

    public void disable() {
        if (Denizen2Core.getImplementation().generalDebug()) {
            Debug.good("De-registering command " + ColorSet.emphasis + cmdName + ColorSet.good + " from sponge...");
        }
        Sponge.getCommandManager().removeMapping(cmdMapping);
    }

    public void register() {
        currentCommandScripts.add(this);
        if (Denizen2Core.getImplementation().generalDebug()) {
            Debug.good("Registering command " + ColorSet.emphasis + cmdName + ColorSet.good + " to sponge...");
        }
        CommandSpec.Builder cmdspecb = CommandSpec.builder()
                .description(Text.of(cmdDesc))
                .arguments(GenericArguments.optional(GenericArguments.remainingRawJoinedStrings(Text.of("dCommand"))))
                .executor(this);
        if (cmdPerm != null) {
            cmdspecb.permission(cmdPerm);
        }
        CommandSpec cmdspec = cmdspecb.build();
        cmdMapping = Sponge.getCommandManager().register(Denizen2Sponge.instance, cmdspec, cmdName).get();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        ArrayList<String> argset = new ArrayList<>(commandContext.hasAny("dCommand") ?
                commandContext.getAll("dCommand") : new ArrayList<>());
        String cmd = CoreUtilities.concat(argset, " ");
        CommandQueue q = getSection("script").toQueue();
        MapTag context = new MapTag();
        context.getInternal().put("raw_arguments", new TextTag(cmd));
        ListTag lt = new ListTag();
        // TODO: better splitter?
        for (String str : CoreUtilities.split(cmd, ' ')) {
            if (str.length() > 0) {
                lt.getInternal().add(new TextTag(str));
            }
        }
        context.getInternal().put("arguments", lt);
        if (commandSource instanceof Player) {
            context.getInternal().put("source", new TextTag("player"));
            context.getInternal().put("player", new PlayerTag((Player) commandSource));
        }
        else if (commandSource instanceof CommandBlockSource) {
            context.getInternal().put("source", new TextTag("block"));
            context.getInternal().put("location", new LocationTag(((CommandBlockSource) commandSource).getLocation()));
        }
        else {
            context.getInternal().put("source", new TextTag("server"));
        }
        q.commandStack.peek().setDefinition("context", context);
        if (getDebugMode().showFull) {
            Debug.good("Running in-game command script: " + ColorSet.emphasis + cmdName + ColorSet.good + "...");
            for (Map.Entry<String, AbstractTagObject> def : context.getInternal().entrySet()) {
                Debug.good("Context Definition: " + ColorSet.emphasis + def.getKey() + ColorSet.good
                        + " is " + ColorSet.emphasis + def.getValue().toString());
            }
        }
        q.start();
        return CommandResult.empty();
    }
}
