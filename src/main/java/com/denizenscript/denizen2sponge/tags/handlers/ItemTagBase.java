package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.ItemTag;

public class ItemTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base item[<ItemTag>]
    // @Group Sponge Base Types
    // @ReturnType ItemTag
    // @Returns the input as a ItemTag.
    // -->

    @Override
    public String getName() {
        return "item";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid item tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return ItemTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
