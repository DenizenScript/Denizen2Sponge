package org.mcmonkey.denizen2sponge.tags.handlers;

import org.mcmonkey.denizen2core.tags.AbstractTagBase;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2sponge.tags.objects.PlayerTag;

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
