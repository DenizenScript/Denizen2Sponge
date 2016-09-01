package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2sponge.tags.objects.BlockTypeTag;
import org.mcmonkey.denizen2core.tags.AbstractTagBase;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;

public class BlockTypeTagBase extends AbstractTagBase {

    // <--[tagbase]
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
        return BlockTypeTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
