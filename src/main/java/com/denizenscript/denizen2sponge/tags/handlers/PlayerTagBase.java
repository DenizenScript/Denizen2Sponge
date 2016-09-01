package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.PlayerTag;

public class PlayerTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base player[<PlayerTag>]
    // @Group Sponge Base Types
    // @ReturnType PlayerTag
    // @Returns the input as a PlayerTag.
    // -->

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return PlayerTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
