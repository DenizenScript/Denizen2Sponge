package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.InventoryTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;

public class InventoryTagBase extends AbstractTagBase {

    // <--[tagbase]
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
        return InventoryTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
