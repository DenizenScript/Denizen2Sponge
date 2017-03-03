package com.denizenscript.denizen2sponge.commands.entity;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Tuple;
import com.denizenscript.denizen2core.utilities.debugging.ColorSet;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import com.denizenscript.denizen2sponge.utilities.Equipment;
import org.spongepowered.api.entity.living.Living;

import java.util.Map;

public class EquipCommand extends AbstractCommand {

    // <--[command]
    // @Name equip
    // @Arguments <entity> <equipment map>
    // @Short equips an entity according to a given equipment map.
    // @Updated 2017/03/03
    // @Group Entities
    // @Minimum 2
    // @Maximum 2
    // @Description
    // Equips an entity according to a given equipment map.
    // Valid equipment keys: HEAD, CHESTPLATE, LEGGINGS, BOOTS, HAND, OFF_HAND
    // TODO: Saddle, Horse_Armor
    // TODO: Explain more!
    // @Example
    // # This example equips a player with a full set of iron armor.
    // - equip <player> head:iron_helmet|chestplate:iron_chestplate|boots:iron_boots|leggings:iron_leggings
    // -->

    @Override
    public String getName() {
        return "equip";
    }

    @Override
    public String getArguments() {
        return "<entity> <equipment map>";
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
        MapTag map = MapTag.getFor(queue.error, entry.getArgumentObject(queue, 1));
        for (Map.Entry<String, AbstractTagObject> mapentry : map.getInternal().entrySet()) {
            ItemTag itm = ItemTag.getFor(queue.error, mapentry.getValue());
            Equipment.equippers.get(CoreUtilities.toLowerCase(mapentry.getKey())).run(new Tuple<>((Living) ent.getInternal(), itm));
        }
        if (queue.shouldShowGood()) {
            queue.outGood("Equipped " + ColorSet.emphasis + map + ColorSet.good + " on the entity!");
        }
    }
}
