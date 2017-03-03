package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Tuple;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.HashMap;

/**
 * Helper class for equipping an entity.
 */
public class Equipment {

    public static final HashMap<String, Action<Tuple<Living, ItemTag>>> equippers = new HashMap<>();

    static {
        equippers.put("head", (tuple) -> ((ArmorEquipable) tuple.one).setHelmet(tuple.two.getInternal()));
        equippers.put("chestplate", (tuple) -> ((ArmorEquipable) tuple.one).setChestplate(tuple.two.getInternal()));
        equippers.put("leggings", (tuple) -> ((ArmorEquipable) tuple.one).setLeggings(tuple.two.getInternal()));
        equippers.put("boots", (tuple) -> ((ArmorEquipable) tuple.one).setBoots(tuple.two.getInternal()));
        equippers.put("hand", (tuple) -> ((ArmorEquipable) tuple.one).setItemInHand(HandTypes.MAIN_HAND, tuple.two.getInternal()));
        equippers.put("offhand", (tuple) -> ((ArmorEquipable) tuple.one).setItemInHand(HandTypes.OFF_HAND, tuple.two.getInternal()));
    }
}
