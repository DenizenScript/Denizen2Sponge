package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
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
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/event/cause/entity/damage/DamageTypes.html>damage types list<@/link>
    // These can be used with the hurt command.
    // -->

    // <--[command]
    // @Name hurt
    // @Arguments <entity> <amount>
    // @Short damages the entity for the specified amount.
    // @Updated 2017/04/01
    // @Group Entity
    // @Minimum 2
    // @Maximum 2
    // @Named absolute (BooleanTag) Sets whether the damage is absolute or not.
    // @Named type (TextTag) Sets of what type the damage will be.
    // @Description
    // Damages the entity for the specified amount. This damage is absolute by default.
    // Related information: <@link explanation Damage Types>damage types<@/link>.
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
        EntityTag ent = EntityTag.getFor(queue.error, entry.getArgumentObject(queue, 0));
        NumberTag dam = NumberTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        DamageSource.Builder build = DamageSource.builder();
        if (entry.namedArgs.containsKey("absolute")) {
            BooleanTag abs = BooleanTag.getFor(queue.error, entry.getNamedArgumentObject(queue, "absolute"));
            if (abs.getInternal()) {
                build.absolute();
            }
        }
        else {
            build.absolute();
        }
        if (entry.namedArgs.containsKey("type")) {
            String typeName = CoreUtilities.toLowerCase(entry.getNamedArgumentObject(queue, "type").toString());
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
            queue.outGood("Hurting " + ColorSet.emphasis + ent.debug() + ColorSet.good
                    + " for " + ColorSet.emphasis + dam.debug() + ColorSet.good + " points of damage!");
        }
        ent.getInternal().damage(dam.getInternal(), build.build());
    }
}
