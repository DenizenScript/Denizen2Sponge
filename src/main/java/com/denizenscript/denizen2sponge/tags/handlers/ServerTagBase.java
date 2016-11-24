package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.CuboidTag;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ServerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base server
    // @Group Sponge Base Types
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
        // @Name ServerBaseTag.block_type_is_valid[<ListTag>]
        // @Updated 2016//11/24
        // @Group Data Safety
        // @ReturnType BooleanTag
        // @Returns whether the specified text is a valid block type, and can be read as a BlockTypeTag.
        // -->
        handlers.put("block_type_is_valid", (dat, obj) -> new BooleanTag(Sponge.getRegistry().getType(
                BlockType.class, dat.getNextModifier().toString()).isPresent()));
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
