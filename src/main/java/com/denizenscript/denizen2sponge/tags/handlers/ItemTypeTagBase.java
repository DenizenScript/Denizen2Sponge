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
        return ItemTypeTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
