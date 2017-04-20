package com.denizenscript.denizen2sponge.commands.player;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

public class HotbarCommand extends AbstractCommand {

    // <--[command]
    // @Name hotbar
    // @Arguments <player> <value>
    // @Short changes the hotbar slot currently selected by the player.
    // @Updated 2017/04/20
    // @Group Player
    // @Minimum 2
    // @Maximum 2
    // @Named operation (TextTag) Sets whether the command will add or set the value.
    // @Description
    // Changes the hotbar slot currently selected by the player. Optionally specify
    // whether the command will 'add' or 'set' the value. Defaults to 'set'.
    // @Example
    // # This example sets the selected hotbar slot of the player to the first one
    // - hotbar <player> 1
    // -->

    @Override
    public String getName() {
        return "hotbar";
    }

    @Override
    public String getArguments() {
        return "<player> <value>";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        PlayerTag player = PlayerTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        IntegerTag value = IntegerTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        Hotbar hotbar = ((PlayerInventory) player.getInternal().getInventory()).getHotbar();
        String operation;
        if (entry.namedArgs.containsKey("operation")) {
            operation = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "operation").toString());
            int index;
            switch (operation) {
                case "set":
                    index = (int) value.getInternal();
                    if (index > 9 || index < 1) {
                        queue.handleError(entry, "Invalid slot index: '" + index + "'. It must be inside the 1-9 range!");
                        return;
                    }
                    hotbar.setSelectedSlotIndex(index - 1);
                    break;
                case "add":
                    index = hotbar.getSelectedSlotIndex() + (int) value.getInternal();
                    if (index > 9) {
                        index -= 9;
                    }
                    else if (index < 1) {
                        index += 9;
                    }
                    hotbar.setSelectedSlotIndex(index);
                    break;
                default:
                    queue.handleError(entry, "Invalid operation: '" + operation + "'!");
                    return;
            }
        }
        else {
            operation = "set";
            int index = (int) value.getInternal();
            if (index > 9 || index < 1) {
                queue.handleError(entry, "Invalid slot index: '" + index + "'. It must be inside the 1-9 range!");
                return;
            }
            hotbar.setSelectedSlotIndex(index - 1);
        }
        if (queue.shouldShowGood()) {
            queue.outGood(ColorSet.emphasis + (operation.equals("add") ? "Scrolling" : "Setting")
                    + ColorSet.good + " the selected hotbar slot of " + ColorSet.emphasis
                    + player.debug() + ColorSet.good + (operation.equals("add") ? " by " : " to ")
                    + ColorSet.emphasis + value.debug() + ColorSet.good + "!");
        }
    }
}
