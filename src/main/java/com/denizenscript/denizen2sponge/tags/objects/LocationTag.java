package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Skull;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LocationTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
    // @Type LocationTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a position in a world. Identified in the format "x,y,z,world".
    // @Note Can also be "x,y,z" without a world. This is a vector.
    // -->

    // <--[explanation]
    // @Since 0.3.0
    // @Name Biome Types
    // @Group Useful Lists
    // @Description
    // A list of all default biome types can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/world/biome/BiomeTypes.html>biome types list<@/link>
    // -->

    private UtilLocation internal = new UtilLocation();

    public LocationTag(UtilLocation location) {
        this(location.x, location.y, location.z, location.world);
    }

    public LocationTag(Location<World> location) {
        this(location.getX(), location.getY(), location.getZ(), location.getExtent());
    }

    public LocationTag(Vector3i location, World world) {
        this(location.getX(), location.getY(), location.getZ(), world);
    }

    public LocationTag(Vector3d location, World world) {
        this(location.getX(), location.getY(), location.getZ(), world);
    }

    public LocationTag(Vector3d location) {
        this(location.getX(), location.getY(), location.getZ());
    }

    public LocationTag(double x, double y, double z) {
        this(x, y, z, null);
    }

    public LocationTag(double x, double y, double z, World world) {
        this.internal.x = x;
        this.internal.y = y;
        this.internal.z = z;
        this.internal.world = world;
    }

    public UtilLocation getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.block_type
        // @Updated 2017/02/07
        // @Group World Data
        // @ReturnType BlockTypeTag
        // @Returns the type of the block at the location.
        // @Example "0,1,2,world" .block_type may return "stone".
        // -->
        handlers.put("block_type", (dat, obj) -> new BlockTypeTag(((LocationTag) obj).internal.toLocation().getBlockType()));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.block
        // @Updated 2017/02/12
        // @Group Identification
        // @ReturnType LocationTag
        // @Returns the block coordinates of this location.
        // -->
        handlers.put("block", (dat, obj) -> new LocationTag(Math.floor(((LocationTag) obj).internal.x),
                Math.floor(((LocationTag) obj).internal.y), Math.floor(((LocationTag) obj).internal.z),
                ((LocationTag) obj).internal.world));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.x
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the X coordinate of the location.
        // @Example "0,1,2,world" .x returns "0".
        // -->
        handlers.put("x", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.x));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.y
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Y coordinate of the location.
        // @Example "0,1,2,world" .y returns "1".
        // -->
        handlers.put("y", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.y));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.z
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Z coordinate of the location.
        // @Example "0,1,2,world" .z returns "2".
        // -->
        handlers.put("z", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.z));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.vector_length
        // @Updated 2017/01/19
        // @Group Mathematics
        // @ReturnType NumberTag
        // @Returns the vector-length of the vector this location is interpretted as.
        // @Example "0,2,0" .vector_length returns "2".
        // -->
        handlers.put("vector_length", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            return new NumberTag(Math.sqrt(loc.x * loc.x + loc.y * loc.y + loc.z * loc.z));
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.vector_length_squared
        // @Updated 2017/01/19
        // @Group Mathematics
        // @ReturnType NumberTag
        // @Returns the square of the vector-length of the vector this location is interpretted as.
        // @Example "0,2,0" .vector_length_squared returns "4".
        // -->
        handlers.put("vector_length_squared", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            return new NumberTag(loc.x * loc.x + loc.y * loc.y + loc.z * loc.z);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.normalized
        // @Updated 2017/01/29
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns the normalized form of this vector location.
        // @Example "0,2,0" .normalized returns "0,1,0".
        // -->
        handlers.put("normalized", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double len = 1.0 / Math.sqrt(loc.x * loc.x + loc.y * loc.y + loc.z * loc.z);
            return new LocationTag(loc.x * len, loc.y * len, loc.z * len, loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.rotate_around_x[<NumberTag>]
        // @Updated 2017/04/03
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns this vector location rotated around the x axis by the specified angle in radians.
        // -->
        handlers.put("rotate_around_x", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double angle = NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(loc.x, loc.y * Math.cos(angle) - loc.z * Math.sin(angle), loc.y * Math.sin(angle) + loc.z * Math.cos(angle), loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.rotate_around_y[<NumberTag>]
        // @Updated 2017/04/03
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns this vector location rotated around the y axis by the specified angle in radians.
        // -->
        handlers.put("rotate_around_y", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double angle = NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(loc.z * Math.sin(angle) + loc.x * Math.cos(angle), loc.y, loc.z * Math.cos(angle) - loc.x * Math.sin(angle), loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.rotate_around_z[<NumberTag>]
        // @Updated 2017/04/03
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns this vector location rotated around the z axis by the specified angle in radians.
        // -->
        handlers.put("rotate_around_z", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double angle = NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(loc.x * Math.cos(angle) - loc.y * Math.sin(angle), loc.x * Math.sin(angle) + loc.y * Math.cos(angle), loc.z, loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.world
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType WorldTag
        // @Returns the world of the location.
        // @Example "0,1,2,world" .world returns "world".
        // -->
        handlers.put("world", (dat, obj) -> new WorldTag(((LocationTag) obj).internal.world));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.add[<LocationTag>]
        // @Updated 2016/11/24
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns the location with the specified location vector added to it.
        // -->
        handlers.put("add", (dat, obj) -> {
            UtilLocation t = ((LocationTag) obj).internal;
            UtilLocation a = LocationTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(t.x + a.x, t.y + a.y, t.z + a.z, t.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.subtract[<LocationTag>]
        // @Updated 2017/02/07
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns the location with the specified location vector subtracted from it.
        // -->
        handlers.put("subtract", (dat, obj) -> {
            UtilLocation t = ((LocationTag) obj).internal;
            UtilLocation a = LocationTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(t.x - a.x, t.y - a.y, t.z - a.z, t.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.multiply[<NumberTag>]
        // @Updated 2017/01/19
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns the location vector, multiplied by a scalar value.
        // @Example "0,2,0" .multiply[2] returns "0,4,0".
        // -->
        handlers.put("multiply", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double scalar = NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(loc.x * scalar, loc.y * scalar, loc.z * scalar, loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.divide[<NumberTag>]
        // @Updated 2017/01/19
        // @Group Mathematics
        // @ReturnType LocationTag
        // @Returns the location vector, divided by a scalar value.
        // @Example "0,2,0" .divide[2] returns "0,1,0".
        // -->
        handlers.put("divide", (dat, obj) -> {
            UtilLocation loc = ((LocationTag) obj).internal;
            double scalar = 1.0 / NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal();
            return new LocationTag(loc.x * scalar, loc.y * scalar, loc.z * scalar, loc.world);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.sign_contents
        // @Updated 2017/01/29
        // @Group World Data
        // @ReturnType ListTag<FormattedTextTag>
        // @Returns a list of all text on the sign at this location.
        // @Example "0,2,0,world" .sign_contents might return "First Line||Third Line|".
        // -->
        handlers.put("sign_contents", (dat, obj) -> {
            Optional<List<Text>> contents = ((LocationTag) obj).internal.toLocation().get(Keys.SIGN_LINES);
            if (!contents.isPresent()) {
                if (!dat.hasFallback()) {
                    dat.error.run("No sign contents present for this location!");
                }
                return new NullTag();
            }
            ListTag list = new ListTag();
            for (Text t : contents.get()) {
                list.getInternal().add(new FormattedTextTag(t));
            }
            return list;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.data
        // @Updated 2016/08/28
        // @Group World Data
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the block at the location specified.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((LocationTag) obj).internal.toLocation()));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.get[<TextTag>]
        // @Updated 2016/08/28
        // @Group World Data
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the block at the location specified.
        // -->
        handlers.put("get", (dat, obj) -> {
            String keyName = dat.getNextModifier().toString();
            Key key = DataKeys.getKeyForName(keyName);
            if (key == null) {
                dat.error.run("Invalid key '" + keyName + "'!");
                return new NullTag();
            }
            return DataKeys.getValue(((LocationTag) obj).internal.toLocation(), key, dat.error);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.nearby_entities[<MapTag>]
        // @Updated 2016/08/26
        // @Group World Data
        // @ReturnType ListTag<EntityTag>
        // @Returns a list of entities of a specified type (or any type if unspecified) near the location.
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
            UtilLocation loc = ((LocationTag) obj).getInternal();
            Set<Entity> ents = loc.world.getIntersectingEntities(new AABB(
                    loc.x - range, loc.y - range, loc.z - range, loc.x + range, loc.y + range, loc.z + range));
            for (Entity ent : ents) {
                if ((requiredTypeTag == null || ent.getType().equals(requiredTypeTag.getInternal()))
                        && LengthSquared(ent.getLocation().sub(loc.toVector3d())) < range * range) {
                    list.getInternal().add(new EntityTag(ent));
                }
            }
            return list;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.nearby_blocks[<MapTag>]
        // @Updated 2017/01/29
        // @Group World Data
        // @ReturnType ListTag<LocationTag>
        // @Returns a list of block locations of a specified type (or any type if unspecified) near the location.
        // Input is type:<BlockTypeTag>|range:<NumberTag>
        // -->
        handlers.put("nearby_blocks", (dat, obj) -> {
            ListTag list = new ListTag();
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            BlockTypeTag requiredTypeTag = null;
            if (map.getInternal().containsKey("type")) {
                requiredTypeTag = BlockTypeTag.getFor(dat.error, map.getInternal().get("type"));
            }
            double range = NumberTag.getFor(dat.error, map.getInternal().get("range")).getInternal();
            UtilLocation loc = ((LocationTag) obj).getInternal();
            int low = (int) Math.floor(-range);
            int high = (int) Math.ceil(range);
            for (int x = low; x < high; x++) {
                for (int y = low; y < high; y++) {
                    for (int z = low; z < high; z++) {
                        Location<World> le = new Location<>(loc.world, loc.x + x, loc.y + y, loc.z + z);
                        if ((requiredTypeTag == null || le.getBlock().getType().equals(requiredTypeTag.getInternal()))
                                && LengthSquared(le.sub(loc.toVector3d())) < range * range) {
                            list.getInternal().add(new LocationTag(le));
                        }
                    }
                }
            }
            return list;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.biome
        // @Updated 2017/04/04
        // @Group World Data
        // @ReturnType TextTag
        // @Returns the biome type of this location.
        // Related information: <@link explanation Biome Types>biome types<@/link>
        // -->
        handlers.put("biome", (dat, obj) -> new TextTag(CoreUtilities.toLowerCase(((LocationTag) obj).internal.toLocation().getBiome().getName())));
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.find_safe_location[<MapTag>]
        // @Updated 2017/04/05
        // @Group Location Search
        // @ReturnType MapTag
        // @Returns whether a location that entities can safely teleport to within
        // the specified tolerance exists, and such location.
        // Output is is_valid:<BooleanTag>|location:<LocationTag>
        // Input is height:<IntegerTag>|width:<IntegerTag>
        // -->
        handlers.put("find_safe_location", (dat, obj) -> {
            MapTag inputMap = MapTag.getFor(dat.error, dat.getNextModifier());
            int height = inputMap.getInternal().containsKey("height") ?
                    (int) IntegerTag.getFor(dat.error, inputMap.getInternal().get("height")).getInternal() : TeleportHelper.DEFAULT_HEIGHT;
            int width = inputMap.getInternal().containsKey("width") ?
                    (int) IntegerTag.getFor(dat.error, inputMap.getInternal().get("width")).getInternal() : TeleportHelper.DEFAULT_WIDTH;
            Optional<Location<World>> opt = Sponge.getGame().getTeleportHelper().getSafeLocation(((LocationTag) obj).internal.toLocation(), height, width);
            MapTag outputMap = new MapTag();
            if (!opt.isPresent()) {
                outputMap.getInternal().put("is_valid", new BooleanTag(false));
                outputMap.getInternal().put("location", obj);
                return outputMap;
            }
            outputMap.getInternal().put("is_valid", new BooleanTag(true));
            outputMap.getInternal().put("location", new LocationTag(opt.get().getBlockPosition(), opt.get().getExtent()));
            return outputMap;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.inventory
        // @Updated 2017/08/31
        // @Group Properties
        // @ReturnType InventoryTag
        // @Returns the inventory the tile entity at this location is holding.
        // -->
        handlers.put("inventory", (dat, obj) -> {
            Optional<TileEntity> te = ((LocationTag) obj).internal.toLocation().getTileEntity();
            if (!te.isPresent()) {
                if (!dat.hasFallback()) {
                    dat.error.run("The block at this location is not a valid tile entity, so it can't contain an inventory!");
                }
                return new NullTag();
            }
            return new InventoryTag(((TileEntityCarrier) te.get()).getInventory());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.skull_type
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the type of skull that this location is holding.
        // -->
        handlers.put("skull_type", (dat, obj) -> {
            Optional<TileEntity> te = ((LocationTag) obj).internal.toLocation().getTileEntity();
            if (!te.isPresent() || !(te.get() instanceof Skull)) {
                if (!dat.hasFallback()) {
                    dat.error.run("The block at this location is not a skull tile entity!");
                }
                return new NullTag();
            }
            return new TextTag(((Skull) te.get()).skullType().get().getId());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.represented_player_name
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's name of the skull that this location is holding.
        // -->
        handlers.put("represented_player_name", (dat, obj) -> {
            Optional<TileEntity> te = ((LocationTag) obj).internal.toLocation().getTileEntity();
            if (!te.isPresent() || !(te.get() instanceof Skull) || ((Skull) te.get()).skullType().get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("The block at this location is not a player skull tile entity!");
                }
                return new NullTag();
            }
            return new TextTag(te.get().get(Keys.REPRESENTED_PLAYER).get().getName().get());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.represented_player_uuid
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's unique id of the skull that this location is holding.
        // -->
        handlers.put("represented_player_uuid", (dat, obj) -> {
            Optional<TileEntity> te = ((LocationTag) obj).internal.toLocation().getTileEntity();
            if (!te.isPresent() || !(te.get() instanceof Skull) || ((Skull) te.get()).skullType().get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("The block at this location is not a player skull tile entity!");
                }
                return new NullTag();
            }
            return new TextTag(te.get().get(Keys.REPRESENTED_PLAYER).get().getUniqueId().toString());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name LocationTag.represented_player_skin
        // @Updated 2017/10/16
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's skin of the skull that this location is holding.
        // -->
        handlers.put("represented_player_skin", (dat, obj) -> {
            Optional<TileEntity> te = ((LocationTag) obj).internal.toLocation().getTileEntity();
            if (!te.isPresent() || !(te.get() instanceof Skull) || ((Skull) te.get()).skullType().get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("The block at this location is not a player skull tile entity!");
                }
                return new NullTag();
            }
            ProfileProperty p = te.get().get(Keys.REPRESENTED_PLAYER).get().getPropertyMap().get("textures").iterator().next();
            return new TextTag(p.getValue() + "|" + p.getSignature().get());
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name LocationTag.line_of_sight[<LocationTag>]
        // @Updated 2018/02/08
        // @Group Block Ray
        // @ReturnType BooleanTag
        // @Returns whether the specified location is in the line of sight of this location.
        // -->
        handlers.put("line_of_sight", (dat, obj) -> {
            Location<World> loc1 = ((LocationTag) obj).internal.toLocation();
            Location<World> loc2 = LocationTag.getFor(dat.error, dat.getNextModifier()).internal.toLocation();
            Vector3d direction = loc2.getPosition().sub(loc1.getPosition());
            double length = direction.length();
            if (length == 0) {
                return new BooleanTag(true);
            }
            BlockRayHit<World> brh = BlockRay.from(loc1).direction(direction).distanceLimit(length)
                    .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .build().end().get();
            return new BooleanTag(brh.getBlockPosition().equals(loc2.getBlockPosition()));
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name LocationTag.highest_location
        // @Updated 2018/02/08
        // @Group Location Search
        // @ReturnType LocationTag
        // @Returns the location on top of the highest solid block on this location's column.
        // -->
        handlers.put("highest_location", (dat, obj) -> new LocationTag(((LocationTag) obj).internal.toLocation().asHighestLocation()));
    }

    public static double LengthSquared(Location<World> loc) {
        return loc.getX() * loc.getX() + loc.getY() * loc.getY() + loc.getZ() * loc.getZ();
    }

    public static LocationTag getFor(Action<String> error, String text) {
        List<String> split = CoreUtilities.split(text, ',', 4);
        NumberTag x = NumberTag.getFor(error, split.get(0));
        NumberTag y = NumberTag.getFor(error, split.get(1));
        NumberTag z = NumberTag.getFor(error, split.get(2));
        if (split.size() == 3) {
            return new LocationTag(x.getInternal(), y.getInternal(), z.getInternal());
        }
        WorldTag world = WorldTag.getFor(error, split.get(3));
        return new LocationTag(x.getInternal(), y.getInternal(), z.getInternal(), world.getInternal());
    }

    public static LocationTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof LocationTag) ? (LocationTag) text : getFor(error, text.toString());
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
    public String getTagTypeName() {
        return "LocationTag";
    }

    @Override
    public String toString() {
        String s = CoreUtilities.doubleToString(internal.x) + ","
                + CoreUtilities.doubleToString(internal.y) + ","
                + CoreUtilities.doubleToString(internal.z);
        if (internal.world != null) {
            s += "," + internal.world.getName();
        }
        return s;
    }
}
