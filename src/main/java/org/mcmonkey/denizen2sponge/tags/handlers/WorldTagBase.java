package org.mcmonkey.denizen2sponge.tags.handlers;

import org.mcmonkey.denizen2core.tags.AbstractTagBase;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2sponge.tags.objects.WorldTag;

public class WorldTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base world[<WorldTag>]
    // @Group Sponge Base Types
    // @ReturnType WorldTag
    // @Returns the input as a WorldTag.
    // -->

    @Override
    public String getName() {
        return "world";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return WorldTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
