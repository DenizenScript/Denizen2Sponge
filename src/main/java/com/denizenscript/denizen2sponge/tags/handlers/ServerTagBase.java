package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.tags.objects.CuboidTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.effect.sound.PitchModulation;
import org.spongepowered.api.world.World;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ServerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base server
    // @Group Sponge Helper Types
    // @ReturnType ServerBaseTag
    // @Returns a generic handler for server data.
    // -->

    @Override
    public String getName() {
        return "server";
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name ServerBaseTag.worlds
        // @Updated 2017/01/19
        // @Group Server Lists
        // @ReturnType ListTag
        // @Returns a list of all loaded worlds on the server.
        // -->
        handlers.put("worlds", (dat, obj) -> {
            ListTag list = new ListTag();
            for (World world : Sponge.getServer().getWorlds()) {
                list.getInternal().add(new WorldTag(world));
            }
            return list;
        });
        // <--[tag]
        // @Name ServerBaseTag.tps
        // @Updated 2017/04/04
        // @Group Server Information
        // @ReturnType NumberTag
        // @Returns the current ticks per second on the server.
        // -->
        handlers.put("tps", (dat, obj) -> new NumberTag(Sponge.getServer().getTicksPerSecond()));
        // <--[tag]
        // @Name ServerBaseTag.block_type_is_valid[<ListTag>]
        // @Updated 2016//11/24
        // @Group Data Safety
        // @ReturnType BooleanTag
        // @Returns whether the specified text is a valid block type, and can be read as a BlockTypeTag.
        // -->
        handlers.put("block_type_is_valid", (dat, obj) -> new BooleanTag(Sponge.getRegistry().getType(
                BlockType.class, dat.getNextModifier().toString()).isPresent()));
        // <--[tag]
        // @Name ServerBaseTag.pitch[<TextTag>]
        // @Updated 2017//04/24
        // @Group Sound Helper
        // @ReturnType NumberTag
        // @Returns the specified pitch as a NumberTag that can be used with the PlaySound command.
        // -->
        handlers.put("pitch", (dat, obj) -> {
            try {
                Field f = PitchModulation.class.getField(dat.getNextModifier().toString());
                return new NumberTag(f.getDouble(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                dat.error.run("Invalid pitch specified!");
                return new NullTag();
            }
        });
        // <--[tag]
        // @Name ServerBaseTag.cuboid_wrapping[<ListTag>]
        // @Updated 2016//11/24
        // @Group Mathematics
        // @ReturnType CuboidTag
        // @Returns a CuboidTag that contains and minimally wraps all locations in the list of locations.
        // -->
        handlers.put("cuboid_wrapping", (dat, obj) -> {
            ListTag lt = ListTag.getFor(dat.error, dat.getNextModifier());
            if (lt.getInternal().size() == 0) {
                if (!dat.hasFallback()) {
                    dat.error.run("Empty list tag, cannot wrap a cuboid around nothing!");
                }
                return new NullTag();
            }
            LocationTag one = LocationTag.getFor(dat.error, lt.getInternal().get(0));
            CuboidTag ct = new CuboidTag(one.getInternal(), one.getInternal());
            for (int i = 1; i < lt.getInternal().size(); i++) {
                LocationTag c = LocationTag.getFor(dat.error, lt.getInternal().get(i));
                ct.getInternal().min.x = Math.min(ct.getInternal().min.x, c.getInternal().x);
                ct.getInternal().min.y = Math.min(ct.getInternal().min.y, c.getInternal().y);
                ct.getInternal().min.z = Math.min(ct.getInternal().min.z, c.getInternal().z);
                ct.getInternal().max.x = Math.max(ct.getInternal().max.x, c.getInternal().x);
                ct.getInternal().max.y = Math.max(ct.getInternal().max.y, c.getInternal().y);
                ct.getInternal().max.z = Math.max(ct.getInternal().max.z, c.getInternal().z);
            }
            return ct;
        });
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return new ServerTagBase.ServerBaseTag().handle(data.shrink());
    }

    public class ServerBaseTag extends AbstractTagObject {

        @Override
        public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
            return handlers;
        }

        @Override
        public AbstractTagObject handleElseCase(TagData data) {
            return new TextTag(getName());
        }
    }
}
