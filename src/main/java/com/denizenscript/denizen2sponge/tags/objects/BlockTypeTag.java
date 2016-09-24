package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;

import java.util.HashMap;
import java.util.Optional;

public class BlockTypeTag extends AbstractTagObject {

    // <--[object]
    // @Type BlockTypeTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents a block type. Identified by block type ID.
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
        // @Name BlockTypeTag.id
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the ID of the item type.
        // @Example "minecraft:dirt" .id returns "minecraft:dirt".
        // -->
        handlers.put("id", (dat, obj) -> new TextTag(((BlockTypeTag) obj).internal.getId()));
        // <--[tag]
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
        Optional<BlockType> optItemType = Sponge.getRegistry().getType(BlockType.class, text);
        if (!optItemType.isPresent()) {
            error.run("Invalid BlockTypeTag input!");
            return null;
        }
        return new BlockTypeTag(optItemType.get());
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
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return internal.getId();
    }
}
