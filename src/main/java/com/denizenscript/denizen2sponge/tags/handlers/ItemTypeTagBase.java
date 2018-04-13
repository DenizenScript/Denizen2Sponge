package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.ItemTypeTag;

public class ItemTypeTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base item_type[<ItemTypeTag>]
    // @Group Sponge Base Types
    // @ReturnType ItemTypeTag
    // @Returns the input as a ItemTypeTag.
    // -->

    @Override
    public String getName() {
        return "item_type";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid item_type tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return ItemTypeTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
