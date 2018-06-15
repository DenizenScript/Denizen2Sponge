package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;

import java.util.HashMap;
import java.util.Optional;

public class BlockTypeTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
    // @Type BlockTypeTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents a block type. Identified by block type ID.
    // -->

    // <--[explanation]
    // @Since 0.3.0
    // @Name Block Types
    // @Group Useful Lists
    // @Description
    // A list of all default block types can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/block/BlockTypes.html>block types list<@/link>
    // These can be used with the setblock command as well as with some event switches.
    // -->

    private BlockType internal;

    public BlockTypeTag(BlockType internal) {
        this.internal = internal;
    }

    public BlockType getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name BlockTypeTag.id
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the ID of the item type.
        // @Example "minecraft:dirt" .id returns "minecraft:dirt".
        // -->
        handlers.put("id", (dat, obj) -> new TextTag(((BlockTypeTag) obj).internal.getId()));
        // <--[tag]
        // @Since 0.3.0
        // @Name BlockTypeTag.item_type
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType ItemTypeTag
        // @Returns the item type equivalent of this block type.
        // -->
        handlers.put("item_type", (dat, obj) -> new ItemTypeTag(((BlockTypeTag) obj).internal.getItem().orElseGet(() -> {
            dat.error.run("This block type does not have an item type equivalent!");
            return null;
        })));
        // <--[tag]
        // @Since 0.3.0
        // @Name BlockTypeTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the item type.
        // @Example "minecraft:dirt" .name returns "dirt".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(CoreUtilities.after(((BlockTypeTag) obj).internal.getName(), ":")));
    }

    public static BlockTypeTag getFor(Action<String> error, String text) {
        BlockType blockType = (BlockType) Utilities.getTypeWithDefaultPrefix(BlockType.class, text);
        if (blockType == null) {
            error.run("Invalid BlockTypeTag input!");
            return null;
        }
        return new BlockTypeTag(blockType);
    }

    public static BlockTypeTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof BlockTypeTag) ? (BlockTypeTag) text : getFor(error, text.toString());
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
    public String getTagTypeName() {
        return "BlockTypeTag";
    }

    @Override
    public String toString() {
        return internal.getId();
    }
}
