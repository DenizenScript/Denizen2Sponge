package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.EntityTypeTag;

public class EntityTypeTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base entity_type[<EntityTypeTag>]
    // @Group Sponge Base Types
    // @ReturnType EntityTypeTag
    // @Returns the input as a EntityTypeTag.
    // -->

    @Override
    public String getName() {
        return "entity_type";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid entity_type tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return EntityTypeTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
