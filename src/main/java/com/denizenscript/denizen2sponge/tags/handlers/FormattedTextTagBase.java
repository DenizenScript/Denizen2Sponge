package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;

public class FormattedTextTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base formatted_text[<FormattedTextTag>]
    // @Group Sponge Base Types
    // @ReturnType FormattedTextTag
    // @Returns the input as a FormattedTextTag.
    // -->

    @Override
    public String getName() {
        return "formatted_text";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid formatted_text tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        return FormattedTextTag.getFor(data.error, data.getNextModifier()).handle(data.shrink());
    }
}
