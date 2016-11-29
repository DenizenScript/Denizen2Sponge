package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2sponge.Denizen2Sponge;

public class ColorTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base color[<TextTag>]
    // @Group Sponge Helper Types
    // @ReturnType TextTag
    // @Returns the color code corresponding to the given symbol. Valid symbols: 0123456789 abcdef klmno r
    // -->

    @Override
    public String getName() {
        return "color";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        String c = data.getNextModifier().toString();
        if (!"0123456789abcdefklmnorABCDEFKLMNOR".contains(c)) {
            data.error.run("Invalid color code specified!");
        }
        return new TextTag(Denizen2Sponge.colorChar + c).handle(data.shrink());
    }
}
