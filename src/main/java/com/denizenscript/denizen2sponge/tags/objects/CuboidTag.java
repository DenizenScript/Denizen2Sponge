package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.UtilCuboid;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CuboidTag extends AbstractTagObject {

    // <--[object]
    // @Type CuboidTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a sized cuboidal portion of a world. Identified in the format "x,y,z/x,y,z/world".
    // @Note Unlike LocationTag, CuboidTag must have a world!
    // -->

    private UtilCuboid internal;

    public CuboidTag(UtilLocation a, UtilLocation b) {
        internal = new UtilCuboid(a, b);
    }

    public UtilCuboid getInternal() {
        return internal;
    }

    public boolean contains(UtilLocation point) {
        return point.x >= internal.min.x && point.y >= internal.min.y && point.z >= internal.min.z
                && point.x <= internal.max.x && point.y <= internal.max.y && point.z <= internal.max.z;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name CuboidTag.min
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType LocationTag
        // @Returns the minimum location of this cuboid. IE, it's lowest-valued corner.
        // @Example "0,1,2/4,5,6/world" .min returns "0,1,2,world".
        // -->
        handlers.put("min", (dat, obj) -> new LocationTag(((CuboidTag) obj).internal.min));
        // <--[tag]
        // @Name CuboidTag.max
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType LocationTag
        // @Returns the maximum location of this cuboid. IE, it's highest-valued corner.
        // @Example "0,1,2/4,5,6/world" .max returns "4,5,6,world".
        // -->
        handlers.put("max", (dat, obj) -> new LocationTag(((CuboidTag) obj).internal.max));
        // <--[tag]
        // @Name CuboidTag.world
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType WorldTag
        // @Returns the world of the location.
        // @Example "0,1,2/4,5,6/world" .world returns "world".
        // -->
        handlers.put("world", (dat, obj) -> new WorldTag(((CuboidTag) obj).internal.min.world));
        // <--[tag]
        // @Name CuboidTag.contains[<LocationTag>]
        // @Updated 2016/11/24
        // @Group Mathematics
        // @ReturnType WorldTag
        // @Returns whether the cuboid contains the specified location.
        // @Example "0,1,2/4,5,6/world" .contains[1,2,3,world] returns "true".
        // -->
        handlers.put("contains", (dat, obj) -> new BooleanTag(((CuboidTag) obj).contains(
                LocationTag.getFor(dat.error, dat.getNextModifier()).getInternal())));
        // <--[tag]
        // @Name CuboidTag.block_locations[<ListTag>]
        // @Updated 2016/11/24
        // @Group Connected Information
        // @ReturnType ListTag
        // @Returns the location of all block locations in this cuboid. Optionally, specify a list of BlockType's to use.
        // @Note that partially covered blocks are counted.
        // @Example "0,1,2,world" .world returns "world".
        // -->
        handlers.put("block_locations", (dat, obj) -> {
            List<BlockType> valids = null;
            if (dat.hasNextModifier()) {
                AbstractTagObject mod = dat.getNextModifier();
                valids = new ArrayList<>();
                for (AbstractTagObject ato : ListTag.getFor(dat.error, mod).getInternal()) {
                    BlockTypeTag btt = BlockTypeTag.getFor(dat.error, ato);
                    valids.add(btt.getInternal());
                }
            }
            CuboidTag ct = (CuboidTag) obj;
            ListTag lt = new ListTag();
            int maxx = (int)Math.ceil(ct.internal.max.x);
            int maxy = (int)Math.ceil(ct.internal.max.y);
            int maxz = (int)Math.ceil(ct.internal.max.z);
            for (int x = (int)Math.floor(ct.internal.min.x); x < maxx; x++) {
                for (int y = (int)Math.floor(ct.internal.min.y); y < maxy; y++) {
                    for (int z = (int)Math.floor(ct.internal.min.z); z < maxz; z++) {
                        Location<World> loc = new Location<World>(ct.internal.min.world, x, y, z);
                        if (valids == null || valids.contains(loc.getBlockType())) {
                            lt.getInternal().add(new LocationTag(loc));
                        }
                    }
                }
            }
            return lt;
        });
    }

    public static CuboidTag getFor(Action<String> error, String text) {
        List<String> split = CoreUtilities.split(text, '/', 3);
        if (split.size() != 3) {
            error.run("Invalid Cuboid tag specifications!");
            return null;
        }
        List<String> mins = CoreUtilities.split(split.get(0), ',', 3);
        NumberTag x = NumberTag.getFor(error, mins.get(0));
        NumberTag y = NumberTag.getFor(error, mins.get(1));
        NumberTag z = NumberTag.getFor(error, mins.get(2));
        List<String> maxes = CoreUtilities.split(split.get(1), ',', 3);
        NumberTag x2 = NumberTag.getFor(error, maxes.get(0));
        NumberTag y2 = NumberTag.getFor(error, maxes.get(1));
        NumberTag z2 = NumberTag.getFor(error, maxes.get(2));
        String worldn = split.get(2);
        WorldTag world = WorldTag.getFor(error, worldn);
        return new CuboidTag(new UtilLocation(x.getInternal(), y.getInternal(), z.getInternal(), world.getInternal()),
                new UtilLocation(x2.getInternal(), y2.getInternal(), z2.getInternal(), world.getInternal()));
    }

    public static CuboidTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof CuboidTag) ? (CuboidTag) text : getFor(error, text.toString());
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
        String s = CoreUtilities.doubleToString(internal.min.x) + ","
                + CoreUtilities.doubleToString(internal.min.y) + ","
                + CoreUtilities.doubleToString(internal.min.z) + "/"
                + CoreUtilities.doubleToString(internal.max.x) + ","
                + CoreUtilities.doubleToString(internal.max.y) + ","
                + CoreUtilities.doubleToString(internal.max.z) + "/"
                + internal.min.world.getName();
        return s;
    }
}
