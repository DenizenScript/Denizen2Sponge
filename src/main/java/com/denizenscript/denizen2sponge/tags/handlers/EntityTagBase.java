package com.denizenscript.denizen2sponge.tags.handlers;

import org.mcmonkey.denizen2core.tags.AbstractTagBase;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.EntityTag;

public class EntityTagBase extends AbstractTagBase {

    // <--[tagbase]
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
