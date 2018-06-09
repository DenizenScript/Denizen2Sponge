package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2sponge.Denizen2Sponge;

public class AmpersandTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base &[<TextTag>]
    // @Group Sponge Helper Types
    // @ReturnType TextTag
    // @Returns the color code corresponding to the given symbol. Valid symbols: 0123456789 abcdef klmno r
    // @Note This also supports combinations of color codes, EG "1o" for oblong text with color 1.
    // @Note You can use an empty input modifier - like <&[]> - to get the raw color code symbol.
    // -->

    @Override
    public String getName() {
        return "&";
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        if (!data.hasNextModifier()) {
            data.error.run("Invalid '&' (color code) tag-base: expected a modifier! See documentation for this tag!");
            return null;
        }
        String c = data.getNextModifier().toString();
        if (c.length() == 0) {
            return new TextTag(String.valueOf(Denizen2Sponge.colorChar)).handle(data.shrink());
        }
        StringBuilder res = new StringBuilder(c.length() * 2);
        for (int i = 0; i < c.length(); i++) {
            String sub = c.substring(i, i + 1);
            if (!"0123456789abcdefklmnorABCDEFKLMNOR".contains(sub)) {
                data.error.run("Invalid color code specified: " + sub);
            }
            res.append(Denizen2Sponge.colorChar).append(sub);
        }
        return new TextTag(res.toString()).handle(data.shrink());
    }
}
