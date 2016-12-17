package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.CuboidTag;

public class CuboidTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base cuboid[<CuboidTag>]
    // @Group Sponge Base Types
    // @ReturnType CuboidTag
    // @Returns the input as a CuboidTag.
    // -->

    @Override
    public String getName() {
        return "cuboid";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return CuboidTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
