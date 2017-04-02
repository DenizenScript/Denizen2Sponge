package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

import java.util.Optional;

public class HurtCommand extends AbstractCommand {

    // <--[explanation]
    // @Name Damage Types
    // @Group Useful Lists
    // @Description
    // A list of all default damage types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/event/cause/entity/damage/DamageTypes.html>damage types list<@/link>
    // These can be used with the hurt command.
    // -->

    // <--[command]
    // @Name hurt
    // @Arguments <entity> <amount> [absolute? boolean] [type]
    // @Short damages the entity for the specified amount.
    // @Updated 2017/03/31
    // @Group Entities
    // @Minimum 2
    // @Maximum 4
    // @Description
    // Damages the entity for the specified amount. If no boolean is specified, the damage will be absolute by default.
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
        return "<entity> <amount> [absolute? boolean] [type]";
    }

    @Override
    public int getMinimumArguments() {
        return 2;
    }

    @Override
    public int getMaximumArguments() {
        return 4;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        NumberTag dam = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        DamageSource.Builder build = DamageSource.builder();
        if (entry.arguments.size() > 2) {
            BooleanTag abs = BooleanTag.getFor(queue.error, entry.getArgumentObject(queue, 2));
            if (abs.getInternal()) {
                build.absolute();
            }
        }
        else {
            build.absolute();
        }
        if (entry.arguments.size() > 3) {
            String typeName = entry.getArgumentObject(queue, 3).toString().toLowerCase();
            Optional<DamageType> type = Sponge.getRegistry().getType(DamageType.class, typeName);
            if (!type.isPresent()) {
                queue.handleError(entry, "Invalid damage type: '" + typeName + "'!");
                return;
            }
            build.type(type.get());
        }
        else {
            build.type(DamageTypes.GENERIC);
        }
        if (queue.shouldShowGood()) {
            queue.outGood("hurting " + ColorSet.emphasis + ent.debug() + ColorSet.good
                    + " for " + ColorSet.emphasis + dam.debug() + ColorSet.good + " points of damage!");
        }
        ent.getInternal().damage(dam.getInternal(), build.build(), Denizen2Sponge.getGenericCause());
    }
}
