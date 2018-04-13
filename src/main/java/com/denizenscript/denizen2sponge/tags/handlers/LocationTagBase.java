package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;

public class LocationTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base location[<LocationTag>]
    // @Group Sponge Base Types
    // @ReturnType LocationTag
    // @Returns the input as a LocationTag.
    // -->

    @Override
    public String getName() {
        return "location";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid location tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return LocationTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
