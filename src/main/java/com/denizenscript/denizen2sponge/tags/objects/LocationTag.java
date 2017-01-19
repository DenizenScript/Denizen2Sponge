package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.flowpowered.math.vector.Vector3d;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.List;

public class LocationTag extends AbstractTagObject {

    // <--[object]
    // @Type LocationTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a position in a world. Identified in the format "x,y,z,world".
    // @Note Can also be "x,y,z" without a world. This is a vector.
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
        // @Name LocationTag.data
        // @Updated 2016/08/28
        // @Group General Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the block at the location specified.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((LocationTag) obj).internal.toLocation()));
        // <--[tag]
        // @Name LocationTag.get[<TextTag>]
        // @Updated 2016/08/28
        // @Group General Information
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
