package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2sponge.tags.objects.WorldTag;
import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;

public class WorldTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
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
        if (!data.hasNextModifier()) {
            data.error.run("Invalid world tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return WorldTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
