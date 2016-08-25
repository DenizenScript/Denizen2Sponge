package org.mcmonkey.denizen2sponge.tags.objects;

import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2core.tags.objects.NumberTag;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2core.utilities.Action;
import org.mcmonkey.denizen2core.utilities.CoreUtilities;
import org.mcmonkey.denizen2core.utilities.Function2;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.List;

public class LocationTag extends AbstractTagObject {

    // <--[object]
    // @Type LocationTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a position in a world.
    // -->

    public double x;
    public double y;
    public double z;
    public World world;

    public LocationTag(Location<World> location) {
        this(location.getX(), location.getY(), location.getZ(), location.getExtent());
    }

    public LocationTag(double x, double y, double z) {
        this(x, y, z, null);
    }

    public LocationTag(double x, double y, double z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name LocationTag.x
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the X coordinate of the location.
        // @Example "0,1,2,world" .x returns "0".
        // -->
        handlers.put("x", (dat, obj) -> new NumberTag(((LocationTag) obj).x));
        // <--[tag]
        // @Name LocationTag.y
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Y coordinate of the location.
        // @Example "0,1,2,world" .y returns "1".
        // -->
        handlers.put("y", (dat, obj) -> new NumberTag(((LocationTag) obj).y));
        // <--[tag]
        // @Name LocationTag.z
        // @Group Identification
        // @ReturnType NumberTag
        // @Returns the Z coordinate of the location.
        // @Example "0,1,2,world" .z returns "2".
        // -->
        handlers.put("z", (dat, obj) -> new NumberTag(((LocationTag) obj).z));
        // <--[tag]
        // @Name LocationTag.world
        // @Group Identification
        // @ReturnType WorldTag
        // @Returns the world of the location.
        // @Example "0,1,2,world" .world returns "world".
        // -->
        handlers.put("world", (dat, obj) -> new WorldTag(((LocationTag) obj).world));
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
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return CoreUtilities.doubleToString(x) + ","
                + CoreUtilities.doubleToString(y) + ","
                + CoreUtilities.doubleToString(z) + ","
                + world;
    }
}
