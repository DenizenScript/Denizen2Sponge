package org.mcmonkey.denizen2sponge.tags.handlers;

import org.mcmonkey.denizen2core.tags.AbstractTagBase;
import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2sponge.tags.objects.LocationTag;

public class LocationTagBase extends AbstractTagBase {

    // <--[tagbase]
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
        return LocationTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
