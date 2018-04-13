package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;

public class BlockTypeTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base block_type[<BlockTypeTag>]
    // @Group Sponge Base Types
    // @ReturnType BlockTypeTag
    // @Returns the input as a BlockTypeTag.
    // -->

    @Override
    public String getName() {
        return "block_type";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid block_type tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return BlockTypeTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
