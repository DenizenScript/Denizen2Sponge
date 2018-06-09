package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.ListTag;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

public class TextsTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Since 0.3.0
    // @Base texts
    // @Group Sponge Helper Types
    // @ReturnType TextsBaseTag
    // @Returns a generic handler for texts.
    // -->

    @Override
    public String getName() {
        return "texts";
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name TextsBaseTag.for_plain[<TextTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the plain text as a FormattedTextTag.
        // -->
        handlers.put("for_plain", (dat, obj) -> new FormattedTextTag(Text.of(dat.getNextModifier().toString())));
        // <--[tag]
        // @Since 0.3.0
        // @Name TextsBaseTag.for_old_colors[<TextTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the old-style colored text as a FormattedTextTag.
        // -->
        handlers.put("for_old_colors", (dat, obj) -> new FormattedTextTag(
                TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize((dat.getNextModifier().toString()))));
        // <--[tag]
        // @Since 0.5.0
        // @Name TextsBaseTag.for_ampersand_colors[<TextTag>]
        // @Updated 2018/06/09
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the ampersand colored text as a FormattedTextTag.
        // -->
        handlers.put("for_ampersand_colors", (dat, obj) -> new FormattedTextTag(
                TextSerializers.formattingCode('&').deserialize((dat.getNextModifier().toString()))));
        // <--[tag]
        // @Since 0.3.0
        // @Name TextsBaseTag.for_input[<MapTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the input map tag converted to a FormattedTextTag.
        // @Note Valid inputs:
        // text:hello -> the base text will be 'hello'.
        // color:blue -> the color will be blue.
        // style:italic|bold -> the style will be bold-italic. Also allowed: obfuscated, reset, underline, strikethrough.
        // hover_text:<FormattedTextTag> -> hovering over the text will display 'hello_world'.
        // click_type:suggest -> clicking will suggest a command. Also allowed: execute, open_url.
        // click_data:/dance -> clicking will use the command '/dance'.
        // @Example <texts.for_input[text:hello|color:blue|style:<escape[bold|italic]>|hover_text:<texts.for_plain[hi]>|click_type:suggest|click_data:/dance]>
        // -->
        handlers.put("for_input", (dat, obj) -> {
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            if (!map.getInternal().containsKey("text")) {
                dat.error.run("Missing TEXT setter in for_input tag, cannot create FormattedTextTag!");
            }
            Text.Builder build = Text.builder(map.getInternal().get("text").toString());
            if (map.getInternal().containsKey("color")) {
                Optional<TextColor> color = Sponge.getRegistry().getType(TextColor.class, map.getInternal().get("color").toString());
                if (!color.isPresent()) {
                    dat.error.run("The color specified in for_input tag is invalid, cannot create FormattedTextTag!");
                }
                build.color(color.get());
            }
            if (map.getInternal().containsKey("style")) {
                ListTag reqs = ListTag.getFor(dat.error, map.getInternal().get("style"));
                for (AbstractTagObject ato : reqs.getInternal()) {
                    Object style = Utilities.getTypeWithDefaultPrefix(TextStyle.Base.class, ato.toString());
                    if (style == null) {
                        dat.error.run("The style specified in for_input tag is invalid, cannot create FormattedTextTag!");
                    }
                    build.style((TextStyle.Base) style);
                }
            }
            if (map.getInternal().containsKey("hover_text")) {
                build.onHover(TextActions.showText(FormattedTextTag.getFor(dat.error, map.getInternal().get("hover_text")).getInternal()));
            }
            if (map.getInternal().containsKey("click_type") && map.getInternal().containsKey("click_data")) {
                String s = CoreUtilities.toLowerCase(map.getInternal().get("click_type").toString());
                if (s.equals("suggest")) {
                    build.onClick(TextActions.suggestCommand(map.getInternal().get("click_data").toString()));
                }
                else if (s.equals("execute")) {
                    build.onClick(TextActions.runCommand(map.getInternal().get("click_data").toString()));
                }
                if (s.equals("open_url")) {
                    try {
                        build.onClick(TextActions.openUrl(new URL(map.getInternal().get("click_data").toString())));
                    }
                    catch (MalformedURLException ex) {
                        dat.error.run("Invalid URL in for_input tag: " + ex.getMessage());
                    }
                }
            }
            return new FormattedTextTag(build.build());
        });
    }

    @Override
    public AbstractTagObject handle(TagData data) {
        return new TextsTagBase.TextsBaseTag().handle(data.shrink());
    }

    public static class TextsBaseTag extends AbstractTagObject {

        @Override
        public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
            return handlers;
        }

        @Override
        public AbstractTagObject handleElseCase(TagData data) {
            return new TextTag("texts");
        }

        @Override
        public String getTagTypeName() {
            return "TextsBaseTag";
        }
    }
}
