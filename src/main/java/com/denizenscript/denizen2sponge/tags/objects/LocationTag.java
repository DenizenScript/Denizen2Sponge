package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.flowpowered.math.vector.Vector3d;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LocationTag extends AbstractTagObject {

    // <--[object]
    // @Type LocationTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a position in a world. Identified in the format "x,y,z,world".
    // @Note Can also be "x,y,z" without a world. This is a vector.
    // -->

    // <--[explanation]
    // @Name Biome Types
    // @Group Useful Lists
    // @Description
    // A list of all default biome types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/world/biome/BiomeTypes.html>biome types list<@/link>
    // -->

    private UtilLocation internal = new UtilLocation();

    public LocationTag(UtilLocation location) {
        this(location.x, location.y, location.z, location.world);
    }

    public LocationTag(Location<World> location) {
        this(location.getX(), location.getY(), location.getZ(), location.getExtent());
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
        // @Name LocationTag.block_type
        // @Updated 2017/02/07
        // @Group World Data
        // @ReturnType BlockTypeTag
        // @Returns the type of the block at the location.
        // @Example "0,1,2,world" .block_type may return "stone".
        // -->
        handlers.put("block_type", (dat, obj) -> new BlockTypeTag(((LocationTag) obj).internal.toLocation().getBlockType()));
        // <--[tag]
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
        // @Name LocationTag.x
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the X coordinate of the location.
        // @Example "0,1,2,world" .x returns "0".
        // -->
        handlers.put("x", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.x));
        // <--[tag]
        // @Name LocationTag.y
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Y coordinate of the location.
        // @Example "0,1,2,world" .y returns "1".
        // -->
        handlers.put("y", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.y));
        // <--[tag]
        // @Name LocationTag.z
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Z coordinate of the location.
        // @Example "0,1,2,world" .z returns "2".
        // -->
        handlers.put("z", (dat, obj) -> new NumberTag(((LocationTag) obj).internal.z));
        // <--[tag]
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
        // @Name LocationTag.rotate_around_x
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
        // @Name LocationTag.rotate_around_y
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
        // @Name LocationTag.rotate_around_z
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
        // @Name LocationTag.world
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType WorldTag
        // @Returns the world of the location.
        // @Example "0,1,2,world" .world returns "world".
        // -->
        handlers.put("world", (dat, obj) -> new WorldTag(((LocationTag) obj).internal.world));
        // <--[tag]
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
        // @Name LocationTag.sign_contents
        // @Updated 2017/01/29
        // @Group World Data
        // @ReturnType ListTag
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
        // @Name LocationTag.data
        // @Updated 2016/08/28
        // @Group World Data
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the block at the location specified.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((LocationTag) obj).internal.toLocation()));
        // <--[tag]
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
        // @Name LocationTag.nearby_entities[<MapTag>]
        // @Updated 2016/08/26
        // @Group World Data
        // @ReturnType ListTag
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
        // @Name LocationTag.nearby_blocks[<MapTag>]
        // @Updated 2017/01/29
        // @Group World Data
        // @ReturnType ListTag
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
            int low = (int)Math.floor(-range);
            int high = (int)Math.ceil(range);
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
        // @Name LocationTag.biome
        // @Updated 2017/04/04
        // @Group World Data
        // @ReturnType TextTag
        // @Returns the biome type of this location.
        // Related information: <@link explanation Biome Types>biome types<@/link>
        // -->
        handlers.put("biome", (dat, obj) -> new TextTag(CoreUtilities.toLowerCase(((LocationTag) obj).internal.toLocation().getBiome().getName())));
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
