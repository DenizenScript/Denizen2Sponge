package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Equipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.*;

public class EntityTag extends AbstractTagObject {

    // <--[object]
    // @Type EntityTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an entity on the server. Identified by UUID.
    // -->

    private Entity internal;

    public EntityTag(Entity ent) {
        internal = ent;
    }

    public Entity getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    public String friendlyName() {
        if (internal instanceof Player) {
            return ((Player) internal).getName() + "/" + internal.getUniqueId();
        }
        else {
            return internal.getType().getId() + "/" + internal.getUniqueId();
        }
    }

    static {
        // <--[tag]
        // @Name EntityTag.entity_type
        // @Updated 2017/02/07
        // @Group Identification
        // @ReturnType EntiyTypeTag
        // @Returns the type of this entity.
        // -->
        handlers.put("entity_type", (dat, obj) -> new EntityTypeTag(((EntityTag) obj).internal.getType()));
        // <--[tag]
        // @Name EntityTag.friendly_name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the "friendly name" of the entity, for debug output.
        // -->
        handlers.put("friendly_name", (dat, obj) -> new TextTag(((EntityTag) obj).friendlyName()));
        // <--[tag]
        // @Name EntityTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the entity.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((EntityTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Name EntityTag.location
        // @Updated 2016/08/26
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the location of the entity.
        // -->
        handlers.put("location", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getLocation()));
        // <--[tag]
        // @Name EntityTag.rotation
        // @Updated 2017/02/09
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the rotation of the entity, as a rotation vector.
        // -->
        handlers.put("rotation", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getRotation()));
        // <--[tag]
        // @Name EntityTag.velocity
        // @Updated 2016/08/26
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the velocity of the entity (The vector it is is currently moving in).
        // -->
        handlers.put("velocity", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getVelocity()));
        // <--[tag]
        // @Name EntityTag.eye_height
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the height of the entity's eye, from its feet up.
        // -->
        handlers.put("eye_height", (dat, obj) -> new NumberTag((((EntityTag) obj).internal.getProperty(EyeHeightProperty.class).get().getValue())));
        // <--[tag]
        // @Name EntityTag.eye_location
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the position in the world of the entity's eye.
        // -->
        handlers.put("eye_location", (dat, obj) -> new LocationTag((((EntityTag) obj).internal.getProperty(EyeLocationProperty.class).get().getValue())));
        // <--[tag]
        // @Name EntityTag.health
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the current health value of the entity (0 is dead, max_health is fully alive).
        // -->
        handlers.put("health", (dat, obj) -> new NumberTag(((Living) ((EntityTag) obj).internal).health().get()));
        // <--[tag]
        // @Name EntityTag.max_health
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the maximum health value this entity may currently have
        // (IE, if the entity health is equal to this value, they are fully alive).
        // -->
        handlers.put("max_health", (dat, obj) -> new NumberTag(((Living) ((EntityTag) obj).internal).maxHealth().get()));
        // <--[tag]
        // @Name EntityTag.health_percentage
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns a percentage of health the entity currently has. 0 is dead, 1 is fully alive. Can be a decimal in between.
        // -->
        handlers.put("health_percentage", (dat, obj) -> {
            Living ent = ((Living) ((EntityTag) obj).internal);
            return new NumberTag(ent.health().get() / ent.maxHealth().get());
        });
        // <--[tag]
        // @Name EntityTag.helmet
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the helmet currently worn by the entity.
        // -->
        handlers.put("helmet", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getHelmet().orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.chestplate
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the chestplate currently worn by the entity.
        // -->
        handlers.put("chestplate", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getChestplate().orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.leggings
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the leggings currently worn by the entity.
        // -->
        handlers.put("leggings", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getLeggings().orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.boots
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the boots currently worn by the entity.
        // -->
        handlers.put("boots", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getBoots().orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.held_item
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the item currently held the entity in its main hand.
        // -->
        handlers.put("held_item", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.held_item_offhand
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the item currently held the entity in its off hand.
        // -->
        handlers.put("held_item_offhand", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getItemInHand(HandTypes.OFF_HAND).orElse(ItemStack.of(ItemTypes.NONE, 1))));
        // <--[tag]
        // @Name EntityTag.data
        // @Updated 2016/08/28
        // @Group Current Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((EntityTag) obj).internal));
        // <--[tag]
        // @Name EntityTag.get[<TextTag>]
        // @Updated 2016/08/28
        // @Group Current Information
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the entity.
        // -->
        handlers.put("get", (dat, obj) -> {
            String keyName = dat.getNextModifier().toString();
            Key key = DataKeys.getKeyForName(keyName);
            if (key == null) {
                dat.error.run("Invalid key '" + keyName + "'!");
                return new NullTag();
            }
            return DataKeys.getValue(((EntityTag) obj).internal, key, dat.error);
        });
        // <--[tag]
        // @Name EntityTag.has_flag[<TextTag>]
        // @Updated 2016/10/26
        // @Group Flag Data
        // @ReturnType BooleanTag
        // @Returns whether the entity has a flag with the specified key.
        // -->
        handlers.put("has_flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            return new BooleanTag(flags.getInternal().containsKey(flagName));
        });
        // <--[tag]
        // @Name EntityTag.flag[<TextTag>]
        // @Updated 2016/10/26
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. May become TextTag regardless of input original type.
        // -->
        handlers.put("flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            AbstractTagObject ato = flags.getInternal().get(flagName);
            if (ato == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on this entity!");
                }
                return new NullTag();
            }
            return ato;
        });
        // <--[tag]
        // @Name EntityTag.passengers
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType ListTag
        // @Returns a list of passengers mounted on this entity.
        // -->
        handlers.put("passengers", (dat, obj) -> {
            ListTag list = new ListTag();
            List<Entity> ents = ((EntityTag) obj).internal.getPassengers();
            for (Entity ent : ents) {
                list.getInternal().add(new EntityTag(ent));
            }
            return list;
        });
        // <--[tag]
        // @Name EntityTag.vehicle
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType EntityTag
        // @Returns the entity that this entity is mounted on, if any.
        // -->
        handlers.put("vehicle", (dat, obj) -> {
            Optional<Entity> opt = ((EntityTag) obj).internal.getVehicle();
            if (!opt.isPresent()) {
                return new NullTag();
            }
            return new EntityTag(opt.get());
        });
        // <--[tag]
        // @Name EntityTag.base_vehicle
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType EntityTag
        // @Returns the entity that is the base of the stack that this entity is currently part of.
        // -->
        handlers.put("base_vehicle", (dat,obj) -> new EntityTag(((EntityTag) obj).internal.getBaseVehicle()));
        // <--[tag]
        // @Name EntityTag.on_ground
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this entity is on the ground or not.
        // -->
        handlers.put("on_ground", (dat,obj) -> new BooleanTag(((EntityTag) obj).internal.isOnGround()));
        // <--[tag]
        // @Name EntityTag.scale
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the scale of this entity (currently unused).
        // -->
        handlers.put("scale", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getScale()));
        // <--[tag]
        // @Name EntityTag.nearby_entities[<MapTag>]
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType ListTag
        // @Returns a list of entities of a specified type (or any type if unspecified) near this entity.
        // Input is type:<EntityTypeTag>|range:<NumberTag>
        // -->
        handlers.put("nearby_entities", (dat, obj) -> {
            ListTag list = new ListTag();
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            EntityTypeTag requiredTypeTag = null;
            if (map.getInternal().containsKey("type")) {
                requiredTypeTag = EntityTypeTag.getFor(dat.error, map.getInternal().get("type"));
            }
            double range = NumberTag.getFor(dat.error, map.getInternal().get("range")).getInternal();
            Entity source = ((EntityTag) obj).internal;
            Collection<Entity> ents = source.getNearbyEntities(range);
            for (Entity ent : ents) {
                if ((requiredTypeTag == null || ent.getType().equals(requiredTypeTag.getInternal())) && !ent.equals(source)) {
                    list.getInternal().add(new EntityTag(ent));
                }
            }
            return list;
        });
        // <--[tag]
        // @Name EntityTag.bounding_box
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType CuboidTag
        // @Returns the bounding box of this entity, as a cuboid.
        // -->
        handlers.put("bounding_box", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            return new CuboidTag(ent.getBoundingBox().get(), ent.getWorld());
        });
    }

    public static EntityTag getFor(Action<String> error, String text) {
        UUID id = UUID.fromString(text);
        for (World world : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = world.getEntity(id);
            if (e.isPresent()) {
                return new EntityTag(e.get());
            }
        }
        error.run("Invalid EntityTag UUID input!");
        return null;
    }

    public static EntityTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof EntityTag) ? (EntityTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }

    @Override
    public String debug() {
        return toString() + "/" + internal.getType().getId();
    }
}
