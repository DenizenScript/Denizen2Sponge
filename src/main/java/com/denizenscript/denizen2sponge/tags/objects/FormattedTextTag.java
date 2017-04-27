package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;

public class FormattedTextTag extends AbstractTagObject {

    // <--[object]
    // @Type FormattedTextTag
    // @SubType TextTag
    // @Group Sponge Base Types
    // @Description Represents formatted text. (EG, colors, hover events, and/or ...).
    // -->

    private Text internal;

    public FormattedTextTag(Text text) {
        internal = text;
    }

    public Text getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name FormattedTextTag.plain
        // @Updated 2016/09/21
        // @Group Conversion
        // @ReturnType TextTag
        // @Returns a plain version of this formatted text.
        // -->
        handlers.put("plain", (dat, obj) -> new TextTag(((FormattedTextTag) obj).internal.toPlain()));
        // <--[tag]
        // @Name FormattedTextTag.append[<FormattedTextTag>]
        // @Updated 2016/09/21
        // @Group Modification
        // @ReturnType TextTag
        // @Returns the text followed by another piece of text.
        // -->
        handlers.put("append", (dat, obj) -> new FormattedTextTag(Text.join(((FormattedTextTag) obj).internal,
                FormattedTextTag.getFor(dat.error, dat.getNextModifier()).internal)));
    }

    public static FormattedTextTag getFor(Action<String> error, String text) {
        try {
            Text t = TextSerializers.JSON.deserialize(text);
            return new FormattedTextTag(t);
        }
        catch (TextParseException ex) {
            error.run("Failed to parse FormattedText input: " + ex.getMessage());
            return null;
        }
    }

    public static FormattedTextTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof FormattedTextTag) ? (FormattedTextTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return TextSerializers.JSON.serialize(internal);
    }


    @Override
    public String getTagTypeName() {
        return "FormattedTextTag";
    }
    @Override
    public String debug() {
        return "Formatted[" + internal.toPlain() + "]";
    }
}
