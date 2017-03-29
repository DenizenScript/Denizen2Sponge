package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

public class HurtCommand extends AbstractCommand {

    // <--[command]
    // @Name hurt
    // @Arguments <entity> <amount>
    // @Short damages the entity for the specified amount.
    // @Updated 2017/03/29
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Damages the entity for the specified amount. (This will override any damage modifiers!)
    // @Example
    // # This example hurts the player for 4 points (2 hearts) of damage
    // - hurt <player> 4
    // -->

    @Override
    public String getName() {
        return "hurt";
    }

    @Override
    public String getArguments() {
        return "<entity> <amount>";
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
        // TODO: Ways to specify damage type and whether it is absolute or not.
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        NumberTag dam = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        if (queue.shouldShowGood()) {
            queue.outGood("hurting " + ColorSet.emphasis + ent.debug() + ColorSet.good
                    + " for " + ColorSet.emphasis + dam.debug() + ColorSet.good + " points of damage!");
        }
        ent.getInternal().damage(dam.getInternal(), DamageSource.builder().absolute().type(DamageTypes.GENERIC).build(), Denizen2Sponge.getGenericCause());
    }
}
