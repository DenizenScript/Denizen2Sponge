package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.spongescripts.EntityScript;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.*;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.explosive.FusedExplosive;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.LeashHitch;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Ageable;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.BlockProjectileSource;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.*;

public class EntityTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
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

    public EntityScript getSourceScript() {
        Optional<FlagMap> fm = internal.get(FlagHelper.FLAGMAP);
        if (fm.isPresent()) {
            MapTag flags = fm.get().flags;
            if (flags.getInternal().containsKey("_d2_script")) {
                AbstractTagObject scriptObj = flags.getInternal().get("_d2_script");
                if (scriptObj instanceof ScriptTag) {
                    ScriptTag script = (ScriptTag) scriptObj;
                    if (script.getInternal() instanceof EntityScript) {
                        return (EntityScript) script.getInternal();
                    }
                }
            }
        }
        return null;
    }

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
        // @Since 0.3.0
        // @Name EntityTag.entity_type
        // @Updated 2017/02/07
        // @Group Identification
        // @ReturnType EntiyTypeTag
        // @Returns the type of this entity.
        // -->
        handlers.put("entity_type", (dat, obj) -> new EntityTypeTag(((EntityTag) obj).internal.getType()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.friendly_name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the "friendly name" of the entity, for debug output.
        // -->
        handlers.put("friendly_name", (dat, obj) -> new TextTag(((EntityTag) obj).friendlyName()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the entity.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((EntityTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.location
        // @Updated 2016/08/26
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the location of the entity.
        // -->
        handlers.put("location", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getLocation()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.rotation
        // @Updated 2017/02/09
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the rotation of the entity, as a rotation vector.
        // -->
        handlers.put("rotation", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getRotation()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.velocity
        // @Updated 2016/08/26
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the velocity of the entity (The vector it is is currently moving in).
        // -->
        handlers.put("velocity", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getVelocity()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.eye_height
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the height of the entity's eye, from its feet up.
        // -->
        handlers.put("eye_height", (dat, obj) -> new NumberTag((((EntityTag) obj).internal.getProperty(EyeHeightProperty.class).get().getValue())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.eye_location
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the position in the world of the entity's eye.
        // -->
        handlers.put("eye_location", (dat, obj) -> new LocationTag((((EntityTag) obj).internal.getProperty(EyeLocationProperty.class).get().getValue())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.health
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the current health value of the entity (0 is dead, max_health is fully alive).
        // -->
        handlers.put("health", (dat, obj) -> new NumberTag(((Living) ((EntityTag) obj).internal).health().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.max_health
        // @Updated 2017/03/03
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the maximum health value this entity may currently have
        // (IE, if the entity health is equal to this value, they are fully alive).
        // -->
        handlers.put("max_health", (dat, obj) -> new NumberTag(((Living) ((EntityTag) obj).internal).maxHealth().get()));
        // <--[tag]
        // @Since 0.3.0
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
        // @Since 0.3.0
        // @Name EntityTag.helmet
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the helmet currently worn by the entity.
        // -->
        handlers.put("helmet", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getHelmet().orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.chestplate
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the chestplate currently worn by the entity.
        // -->
        handlers.put("chestplate", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getChestplate().orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.leggings
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the leggings currently worn by the entity.
        // -->
        handlers.put("leggings", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getLeggings().orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.boots
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the boots currently worn by the entity.
        // -->
        handlers.put("boots", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getBoots().orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.held_item
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the item currently held the entity in its main hand.
        // -->
        handlers.put("held_item", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getItemInHand(HandTypes.MAIN_HAND).orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.held_item_offhand
        // @Updated 2017/03/03
        // @Group Equipment
        // @ReturnType ItemTag
        // @Returns the item currently held the entity in its off hand.
        // -->
        handlers.put("held_item_offhand", (dat, obj) -> new ItemTag(((ArmorEquipable) ((EntityTag) obj).internal).getItemInHand(HandTypes.OFF_HAND).orElse(ItemStack.empty())));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.data
        // @Updated 2016/08/28
        // @Group Current Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((EntityTag) obj).internal));
        // <--[tag]
        // @Since 0.3.0
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
                return NullTag.NULL;
            }
            return DataKeys.getValue(((EntityTag) obj).internal, key, dat.error);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.has_flag[<TextTag>]
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType BooleanTag
        // @Returns whether the entity has a flag with the specified key. (And it is not expired).
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
            return BooleanTag.getForBoolean(Utilities.flagIsValidAndNotExpired(dat.error, flags, flagName));
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.dead_flags
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType ListTag
        // @Returns the list of invalid (expired) flags on this entity.
        // -->
        handlers.put("dead_flags", (dat, obj) -> {
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            ListTag invalid = new ListTag();
            for (Map.Entry<String, AbstractTagObject> flag : flags.getInternal().entrySet()) {
                if (!Utilities.flagIsValidAndNotExpired(dat.error, flags, flag.getKey())) {
                    invalid.getInternal().add(new TextTag(flag.getKey()));
                }
            }
            return invalid;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.flag[<TextTag>]
        // @Updated 2017/10/13
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. (And it is not expired).
        // Optionally don't specify anything to get the entire flag map.
        // -->
        handlers.put("flag", (dat, obj) -> {
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            if (!dat.hasNextModifier()) {
                MapTag valid = new MapTag();
                for (Map.Entry<String, AbstractTagObject> flag : flags.getInternal().entrySet()) {
                    if (Utilities.flagIsValidAndNotExpired(dat.error, flags, flag.getKey())) {
                        MapTag mt = MapTag.getFor(dat.error, flag.getKey());
                        valid.getInternal().put(flag.getKey(), mt.getInternal().get("value"));
                    }
                }
                return valid;
            }
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            if (!Utilities.flagIsValidAndNotExpired(dat.error, flags, flagName)) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on this entity!");
                }
                return NullTag.NULL;
            }
            MapTag smap = MapTag.getFor(dat.error, flags.getInternal().get(flagName));
            if (smap == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on this entity!");
                }
                return NullTag.NULL;
            }
            return smap.getInternal().get("value");
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.passengers
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType ListTag<EntityTag>
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
        // @Since 0.3.0
        // @Name EntityTag.vehicle
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType EntityTag
        // @Returns the entity that this entity is mounted on, if any.
        // -->
        handlers.put("vehicle", (dat, obj) -> {
            Optional<Entity> opt = ((EntityTag) obj).internal.getVehicle();
            if (!opt.isPresent()) {
                return NullTag.NULL;
            }
            return new EntityTag(opt.get());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.base_vehicle
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType EntityTag
        // @Returns the entity that is the base of the stack that this entity is currently part of.
        // -->
        handlers.put("base_vehicle", (dat, obj) -> new EntityTag(((EntityTag) obj).internal.getBaseVehicle()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.on_ground
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this entity is on the ground or not.
        // -->
        handlers.put("on_ground", (dat, obj) -> BooleanTag.getForBoolean(((EntityTag) obj).internal.isOnGround()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.scale
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the scale of this entity (currently unused).
        // -->
        handlers.put("scale", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getScale()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.nearby_entities[<MapTag>]
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType ListTag<EntityTag>
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
        // @Since 0.3.0
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
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.max_air
        // @Updated 2017/04/17
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns the maximum air level this entity can have.
        // -->
        handlers.put("max_air", (dat, obj) -> new DurationTag(((EntityTag) obj).internal.get(Keys.MAX_AIR).get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.remaining_air
        // @Updated 2017/04/17
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns the remaining air level this entity can have.
        // -->
        handlers.put("remaining_air", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            Optional<Integer> opt = ent.get(Keys.REMAINING_AIR);
            if (!opt.isPresent()) {
                return new DurationTag(ent.get(Keys.MAX_AIR).get() * (1.0 / 20.0));
            }
            return new DurationTag(opt.get() * (1.0 / 20.0));
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.inventory
        // @Updated 2017/06/13
        // @Group Current Information
        // @ReturnType InventoryTag
        // @Returns the inventory this entity is carrying.
        // -->
        handlers.put("inventory", (dat, obj) -> new InventoryTag(((Carrier) ((EntityTag) obj).internal).getInventory()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.explosion_radius
        // @Updated 2017/09/28
        // @Group Explosive Entities
        // @ReturnType IntegerTag
        // @Returns the radius in blocks that the explosion will affect. Explosive entities only.
        // -->
        handlers.put("explosion_radius", (dat, obj) -> new IntegerTag(((Explosive) ((EntityTag) obj).internal).explosionRadius().get().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.falling_block
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType BlockTypeTag
        // @Returns the block this falling block is representing. Falling block entities only.
        // -->
        handlers.put("falling_block", (dat, obj) -> new BlockTypeTag(((FallingBlock) ((EntityTag) obj).internal).blockState().get().getType()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.can_drop_as_item
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this falling block can drop as an item if it can't be placed when it lands. Falling block entities only.
        // -->
        handlers.put("can_drop_as_item", (dat, obj) -> BooleanTag.getForBoolean(((FallingBlock) ((EntityTag) obj).internal).canDropAsItem().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.can_place_as_block
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this falling block can place as a block when it lands. Falling block entities only.
        // -->
        handlers.put("can_place_as_block", (dat, obj) -> BooleanTag.getForBoolean(((FallingBlock) ((EntityTag) obj).internal).canPlaceAsBlock().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.can_hurt_entities
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this falling block can hurt entities if it lands on them. Falling block entities only.
        // -->
        handlers.put("can_hurt_entities", (dat, obj) -> BooleanTag.getForBoolean(((FallingBlock) ((EntityTag) obj).internal).canHurtEntities().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.fall_damage_per_block
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns how much damage the falling block will deal per block fallen. Falling block entities only.
        // -->
        handlers.put("fall_damage_per_block", (dat, obj) -> new NumberTag(((FallingBlock) ((EntityTag) obj).internal).fallDamagePerBlock().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.fall_time
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns how long the block has been falling. Falling block entities only.
        // -->
        handlers.put("fall_time", (dat, obj) -> new DurationTag(((FallingBlock) ((EntityTag) obj).internal).fallTime().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.max_fall_damage
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the maximum damage this falling block can deal to entities when it lands on them. Falling block entities only.
        // -->
        handlers.put("max_fall_damage", (dat, obj) -> new NumberTag(((FallingBlock) ((EntityTag) obj).internal).maxFallDamage().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.experience_held
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType IntegerTag
        // @Returns how much experience this experience orb holds. Experience orb entities only.
        // -->
        handlers.put("experience_held", (dat, obj) -> new IntegerTag(((ExperienceOrb) ((EntityTag) obj).internal).experience().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.hanging_direction
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType TextTag
        // @Returns the current direction this hanging is facing. Hanging entities only.
        // -->
        handlers.put("hanging_direction", (dat, obj) -> new TextTag(((Hanging) ((EntityTag) obj).internal).direction().get().name()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.item
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType ItemTag
        // @Returns the item this dropped item is representing. Dropped item entities only.
        // -->
        handlers.put("item", (dat, obj) -> new ItemTag(((Item) ((EntityTag) obj).internal).item().get().createStack()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.shooter
        // @Updated 2017/09/28
        // @Group Current Information
        // @ReturnType EntityTag/LocationTag
        // @Returns the shooter source of this projectile. Projectile entities only.
        // -->
        handlers.put("shooter", (dat, obj) -> {
            ProjectileSource source = ((Projectile) ((EntityTag) obj).internal).getShooter();
            if (source instanceof BlockProjectileSource) {
                return new LocationTag(((BlockProjectileSource) source).getLocation());
            }
            else if (source instanceof  Entity) {
                return new EntityTag(((Entity) source));
            }
            else {
                if (!dat.hasFallback()) {
                    dat.error.run("The projectile source is unknown!");
                }
                return NullTag.NULL;
            }
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.max_speed
        // @Updated 2017/09/30
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the maximum speed that this boat is allowed to travel at. Boat entities only.
        // -->
        handlers.put("max_speed", (dat, obj) -> new NumberTag(((Boat) ((EntityTag) obj).internal).getMaxSpeed()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.occupied_deceleration
        // @Updated 2017/09/30
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the rate at which occupied boats decelerate. Boat entities only.
        // -->
        handlers.put("occupied_deceleration", (dat, obj) -> new NumberTag(((Boat) ((EntityTag) obj).internal).getOccupiedDeceleration()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.unoccupied_deceleration
        // @Updated 2017/09/30
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the rate at which unoccupied boats decelerate. Boat entities only.
        // -->
        handlers.put("unoccupied_deceleration", (dat, obj) -> new NumberTag(((Boat) ((EntityTag) obj).internal).getUnoccupiedDeceleration()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.move_on_land
        // @Updated 2017/09/30
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether the boat is able to move freely on land or not. Boat entities only.
        // -->
        handlers.put("can_move_on_land", (dat, obj) -> BooleanTag.getForBoolean(((Boat) ((EntityTag) obj).internal).canMoveOnLand()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.in_water
        // @Updated 2017/09/30
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether the boat is currently in water or not. Boat entities only.
        // -->
        handlers.put("in_water", (dat, obj) -> BooleanTag.getForBoolean(((Boat) ((EntityTag) obj).internal).isInWater()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.art_name
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType TextTag
        // @Returns the name of the art this painting holds. Painting entities only.
        // -->
        handlers.put("art_name", (dat, obj) -> new TextTag(((Painting) ((EntityTag) obj).internal).art().get().getName()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.art_size
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns how many blocks this art spans horizontally (X) and vertically (Y). Painting entities only.
        // -->
        handlers.put("art_size", (dat, obj) -> {
            Art art = ((Painting) ((EntityTag) obj).internal).art().get();
            return new LocationTag(art.getWidth(), art.getHeight(), 0);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.leashed_entity
        // @Updated 2017/10/04
        // @Group Current Information
        // @ReturnType EntityTag
        // @Returns the entity currently leashed by this hitch. Leash hitch entities only.
        // -->
        handlers.put("leashed_entity", (dat, obj) -> {
            Entity ent = ((LeashHitch) ((EntityTag) obj).internal).getLeashedEntity();
            if (ent == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("This hitch has no leashed entity!");
                }
                return NullTag.NULL;
            }
            return new EntityTag(ent);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.application_delay
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns the application delay of this cloud. Area effect cloud entities only.
        // -->
        handlers.put("application_delay", (dat, obj) -> new DurationTag(((AreaEffectCloud) ((EntityTag) obj).internal).applicationDelay().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.cloud_color
        // @Updated 2017/10/04
        // @Group Current Information
        // @ReturnType TextTag
        // @Returns the color of this cloud. Area effect cloud entities only.
        // -->
        handlers.put("cloud_color", (dat, obj) -> {
            Color color = ((AreaEffectCloud) ((EntityTag) obj).internal).color().get();
            return new LocationTag(color.getRed(), color.getGreen(), color.getBlue());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.duration
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns the current duration of this cloud. Area effect cloud entities only.
        // -->
        handlers.put("duration", (dat, obj) -> new DurationTag(((AreaEffectCloud) ((EntityTag) obj).internal).duration().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.duration_on_use
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns how much duration the cloud will lose after applying its effect to an entity. Area effect cloud entities only.
        // -->
        handlers.put("duration_on_use", (dat, obj) -> new DurationTag(((AreaEffectCloud) ((EntityTag) obj).internal).durationOnUse().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.particle_type
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType TextTag
        // @Returns the particle type of this cloud. Area effect cloud entities only.
        // -->
        handlers.put("particle_type", (dat, obj) -> new TextTag(((AreaEffectCloud) ((EntityTag) obj).internal).particleType().get().getName()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.radius
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns the current radius of this cloud. Area effect cloud entities only.
        // -->
        handlers.put("radius", (dat, obj) -> new NumberTag(((AreaEffectCloud) ((EntityTag) obj).internal).radius().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.radius_on_use
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns how much radius the cloud will lose after applying its effect to an entity. Area effect cloud entities only.
        // -->
        handlers.put("radius_on_use", (dat, obj) -> new NumberTag(((AreaEffectCloud) ((EntityTag) obj).internal).radiusOnUse().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.radius_per_tick
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType NumberTag
        // @Returns how much radius the cloud will lose every tick. Area effect cloud entities only.
        // -->
        handlers.put("radius_per_tick", (dat, obj) -> new NumberTag(((AreaEffectCloud) ((EntityTag) obj).internal).radiusPerTick().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.wait_time
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType DurationTag
        // @Returns how long until this cloud will affect entities with its effect. Area effect cloud entities only.
        // -->
        handlers.put("wait_time", (dat, obj) -> new DurationTag(((AreaEffectCloud) ((EntityTag) obj).internal).waitTime().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.age
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType IntegerTag
        // @Returns the current age of this entity. Works with area effect cloud entities too.
        // -->
        handlers.put("age", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            if (ent instanceof AreaEffectCloud) {
                return new IntegerTag(((AreaEffectCloud) ent).age().get());
            }
            else if (ent instanceof Ageable) {
                return new IntegerTag(((Ageable) ent).age().get());
            }
            else {
                if (!dat.hasFallback()) {
                    dat.error.run("This entity doesn't have an age property!");
                }
                return NullTag.NULL;
            }
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.is_adult
        // @Updated 2017/10/02
        // @Group Current Information
        // @ReturnType BooleanTag
        // @Returns whether this entity is an adult or not.
        // -->
        handlers.put("is_adult", (dat, obj) -> BooleanTag.getForBoolean(((Ageable) ((EntityTag) obj).internal).adult().get()));
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.target_block[<NumberTag>]
        // @Updated 2017/10/17
        // @Group Entity Target
        // @ReturnType LocationTag
        // @Returns the block the entity is looking at, up to a maximum distance.
        // If no distance is specified, the default hand-reach distance is used.
        // -->
        handlers.put("target_block", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            return new LocationTag(BlockRay.from(ent)
                    .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .distanceLimit(dat.hasNextModifier() ? NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal() :
                            (Utilities.getHandReach(ent))).build().end().get().getLocation());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.precise_target_location[<NumberTag>]
        // @Updated 2017/10/17
        // @Group Entity Target
        // @ReturnType LocationTag
        // @Returns the exact location the entity is looking at, up to a maximum distance.
        // If no distance is specified, the default hand-reach distance is used.
        // -->
        handlers.put("precise_target_location", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            BlockRayHit hit = BlockRay.from(ent).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .distanceLimit(dat.hasNextModifier() ? NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal() :
                            (Utilities.getHandReach(ent))).build().end().get();
            return new LocationTag(hit.getX(), hit.getY(), hit.getZ(), (World) hit.getExtent());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.precise_target_normal[<NumberTag>]
        // @Updated 2017/10/17
        // @Group Entity Target
        // @ReturnType LocationTag
        // @Returns the direction of the face of the block the entity is looking at, up to a maximum distance.
        // If no distance is specified, the default hand-reach distance is used.
        // -->
        handlers.put("precise_target_normal", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            return new LocationTag(BlockRay.from(ent)
                    .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .distanceLimit(dat.hasNextModifier() ? NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal() :
                            (Utilities.getHandReach(ent))).build().end().get().getNormal());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name EntityTag.target_entities[<MapTag>]
        // @Updated 2017/10/17
        // @Group Entity Target
        // @ReturnType ListTag<EntityTag>
        // @Returns a list of entities of a specified type (or any type if unspecified) intersecting with
        // the line of sight of the source entity. If no range is specified, it defaults to the hand reach.
        // Input is type:<EntityTypeTag>|range:<NumberTag>
        // -->
        handlers.put("target_entities", (dat, obj) -> {
            Entity ent = ((EntityTag) obj).internal;
            ListTag list = new ListTag();
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            EntityTypeTag requiredTypeTag = null;
            if (map.getInternal().containsKey("type")) {
                requiredTypeTag = EntityTypeTag.getFor(dat.error, map.getInternal().get("type"));
            }
            Set<EntityUniverse.EntityHit> entHits = ent.getWorld()
                    .getIntersectingEntities(ent, map.getInternal().containsKey("range") ?
                            NumberTag.getFor(dat.error, map.getInternal().get("range")).getInternal() :
                            (Utilities.getHandReach(ent)));
            for (EntityUniverse.EntityHit entHit : entHits) {
                Entity hit = entHit.getEntity();
                if ((requiredTypeTag == null || hit.getType().equals(requiredTypeTag.getInternal())) && !hit.equals(ent)) {
                    list.getInternal().add(new EntityTag(hit));
                }
            }
            return list;
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.fuse_duration
        // @Updated 2018/01/10
        // @Group Explosive Entities
        // @ReturnType DurationTag
        // @Returns the duration before an explosive entity detonates when primed.
        // -->
        handlers.put("fuse_duration", (dat, obj) -> new DurationTag(((FusedExplosive) ((EntityTag) obj).internal).getFuseData().fuseDuration().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.remaining_fuse_duration
        // @Updated 2018/01/10
        // @Group Explosive Entities
        // @ReturnType DurationTag
        // @Returns the remaining duration before a primed explosive entity detonates.
        // -->
        handlers.put("remaining_fuse_duration", (dat, obj) -> new DurationTag(((FusedExplosive) ((EntityTag) obj).internal).getFuseData().ticksRemaining().get() * (1.0 / 20.0)));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.is_primed
        // @Updated 2018/01/10
        // @Group Explosive Entities
        // @ReturnType BooleanTag
        // @Returns whether an explosive entity is primed.
        // @Warning This tag always returns false in Sponge during last testing.
        // -->
        handlers.put("is_primed", (dat, obj) -> BooleanTag.getForBoolean(((FusedExplosive) ((EntityTag) obj).internal).isPrimed()));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.has_ai
        // @Updated 2018/01/16
        // @Group Entity Behavior
        // @ReturnType BooleanTag
        // @Returns whether an entity has ai enabled.
        // -->
        handlers.put("has_ai", (dat, obj) -> BooleanTag.getForBoolean(((Agent) ((EntityTag) obj).internal).aiEnabled().get()));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.ai_target
        // @Updated 2018/01/16
        // @Group Entity Behavior
        // @ReturnType EntityTag
        // @Returns the entity that is being targeted by this entity's AI, if any.
        // -->
        handlers.put("ai_target", (dat, obj) -> {
            Optional<Entity> opt = ((Agent) ((EntityTag) obj).internal).getTarget();
            if (opt.isPresent()) {
                return new EntityTag(opt.get());
            }
            else {
                if (!dat.hasFallback()) {
                    dat.error.run("This entity doesn't have target!");
                }
                return NullTag.NULL;
            }
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.burning
        // @Updated 2018/02/17
        // @Group Entity Properties
        // @ReturnType BooleanTag
        // @Returns whether an entity is currently burning.
        // -->
        handlers.put("burning", (dat, obj) -> new BooleanTag(((EntityTag) obj).internal.get(Keys.FIRE_TICKS).orElse(0) > 0));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.burn_time
        // @Updated 2018/02/17
        // @Group Entity Properties
        // @ReturnType DurationTag
        // @Returns how long an entity is burning for.
        // -->
        handlers.put("burn_time", (dat, obj) -> new DurationTag(((EntityTag) obj).internal.get(Keys.FIRE_TICKS).orElse(0) / 20.0));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.absorption
        // @Updated 2018/02/17
        // @Group Entity Properties
        // @ReturnType NumberTag
        // @Returns how much absorption this entity has.
        // -->
        handlers.put("absorption", (dat, obj) -> new NumberTag(((EntityTag) obj).internal.get(Keys.ABSORPTION).orElse(0.0)));
        // <--[tag]
        // @Since 0.4.0
        // @Name EntityTag.glowing
        // @Updated 2018/02/18
        // @Group Entity Properties
        // @ReturnType BooleanTag
        // @Returns whether an entity is currently glowing.
        // -->
        handlers.put("glowing", (dat, obj) -> new BooleanTag(((EntityTag) obj).internal.get(Keys.GLOWING).get()));
        // <--[tag]
        // @Since 0.5.0
        // @Name EntityTag.is_script
        // @Updated 2018/05/29
        // @Group General Information
        // @ReturnType BooleanTag
        // @Returns whether the entity was sourced from a script.
        // -->
        handlers.put("is_script", (dat, obj) -> new BooleanTag(((EntityTag) obj).getSourceScript() != null));
        // <--[tag]
        // @Since 0.5.0
        // @Name EntityTag.script
        // @Updated 2018/05/29
        // @Group General Information
        // @ReturnType ScriptTag
        // @Returns the script this entity was spawned from, if any.
        // -->
        handlers.put("script", (dat, obj) -> {
            EntityScript src = ((EntityTag) obj).getSourceScript();
            if (src == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Entity was not sourced from a script.");
                }
                return new NullTag();
            }
            return new ScriptTag(src);
        });
    }

    public static EntityTag getFor(Action<String> error, String text) {
        try {
            UUID uuid = CoreUtilities.tryGetUUID(text);
            if (uuid == null) {
                error.run("Invalid EntityTag UUID input (input is not a valid UUID)!");
                return null;
            }
            for (World world : Sponge.getServer().getWorlds()) {
                Optional<Entity> e = world.getEntity(uuid);
                if (e.isPresent()) {
                    return new EntityTag(e.get());
                }
            }
            error.run("Invalid EntityTag UUID input (that UUID cannot be matched to a real entity)!");
            return null;
        }
        catch (Exception e) {
            error.run("Invalid EntityTag UUID input (input is not a valid UUID)!");
            return null;
        }
    }

    public static EntityTag getFor(Action<String> error, AbstractTagObject ato) {
        if (ato instanceof EntityTag) {
            return (EntityTag) ato;
        }
        if (ato instanceof PlayerTag) {
            return new EntityTag(((PlayerTag) ato).getOnline(error));
        }
        return getFor(error, ato.toString());
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
    public String getTagTypeName() {
        return "EntityTag";
    }

    @Override
    public String debug() {
        return toString() + "/" + internal.getType().getId();
    }
}
