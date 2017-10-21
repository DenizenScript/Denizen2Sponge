package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;

public class EntityTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base entity[<EntityTag>]
    // @Group Sponge Base Types
    // @ReturnType EntityTag
    // @Returns the input as an EntityTag.
    // -->

    @Override
    public String getName() {
        return "entity";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return EntityTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
