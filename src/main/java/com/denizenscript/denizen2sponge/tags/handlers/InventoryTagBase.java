package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;

public class InventoryTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base inventory[<InventoryTag>]
    // @Group Sponge Base Types
    // @ReturnType InventoryTag
    // @Returns the input as an InventoryTag.
    // -->

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid inventory tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return InventoryTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
