package com.denizenscript.denizen2sponge.tags.handlers;

import com.denizenscript.denizen2core.tags.AbstractTagBase;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class TextsTagBase extends AbstractTagBase {

    // <--[tagbase]
    // @Base texts
    // @Group Sponge Base Types
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
        // @Name TextsBaseTag.for_plain[<TextTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the plain text as a FormattedTextTag.
        // -->
        handlers.put("for_plain", (dat, obj) -> new FormattedTextTag(Text.of(dat.getNextModifier().toString())));
        // <--[tag]
        // @Name TextsBaseTag.for_old_colors[<TextTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the old-style colored text as a FormattedTextTag.
        // -->
        handlers.put("for_old_colors", (dat, obj) -> new FormattedTextTag(
                TextSerializers.formattingCode(Denizen2Sponge.colorChar).deserialize((dat.getNextModifier().toString()))));
        // <--[tag]
        // @Name TextsBaseTag.for_input[<MapTag>]
        // @Updated 2016/09/21
        // @Group Text Formatting
        // @ReturnType FormattedTextTag
        // @Returns the input map tag converted to a FormattedTextTag.
        // @Other Valid inputs:
        // text:hello -> the base text will be 'hello'.
        // color:blue -> the color will be blue.
        // style:italics|bold -> the style will be bold-italic. Also allowed: obfuscated, reset, underline, strike.
        // hover_text:<FormattedTextTag> -> hovering over the the text will display 'hello_world'
        // click_type:suggest -> clicking will suggest a command. Also allowed: execute, open_url.
        // click_data:/dance -> clicking will use the command '/dance'.
        // @Example <texts.for_input[text:hello|color:blue|style:<escape[bold|italics]>|hover_text:<texts.for_plain[hi]>|click_type:suggest|click_data:/dance]>
        // -->
        handlers.put("for_input", (dat, obj) -> {
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            if (!map.getInternal().containsKey("text")) {
                dat.error.run("Missing TEXT setter in for_input tag, cannot created FormattedTextTag!");
            }
            Text.Builder build = Text.builder(map.getInternal().get("text").toString());
            if (map.getInternal().containsKey("color")) {
                build.color(Sponge.getRegistry().getType(TextColor.class, map.getInternal().get("color").toString().toUpperCase()).get());
            }
            if (map.getInternal().containsKey("style")) {
                ListTag reqs = ListTag.getFor(dat.error, map.getInternal().get("style"));
                boolean bold = false;
                boolean italics = false;
                boolean obfu = false;
                for (AbstractTagObject ato : reqs.getInternal()) {
                    String str = ato.toString();
                    if (str.equals("bold")) {
                        build.style(TextStyles.BOLD);
                    }
                    else if (str.equals("italics")) {
                        build.style(TextStyles.ITALIC);
                    }
                    else if (str.equals("obfuscated")) {
                        build.style(TextStyles.OBFUSCATED);
                    }
                    else if (str.equals("reset")) {
                        build.style(TextStyles.RESET);
                    }
                    else if (str.equals("underline")) {
                        build.style(TextStyles.UNDERLINE);
                    }
                    else  if (str.equals("strike")) {
                        build.style(TextStyles.STRIKETHROUGH);
                    }
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

    public class TextsBaseTag extends AbstractTagObject {

        @Override
        public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
            return handlers;
        }

        @Override
        public AbstractTagObject handleElseCase(TagData data) {
            return new TextTag(getName()).handle(data);
        }
    }
}
