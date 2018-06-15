package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;

public class ItemTypeTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
    // @Type ItemTypeTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents an item type. Identified by item type ID.
    // -->

    private ItemType internal;

    public ItemTypeTag(ItemType internal) {
        this.internal = internal;
    }

    public ItemType getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTypeTag.block_type
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType BlockTypeTag
        // @Returns the block type equivalent of this item type.
        // -->
        handlers.put("block_type", (dat, obj) -> new BlockTypeTag(((ItemTypeTag) obj).internal.getBlock().orElseGet(() -> {
            dat.error.run("This item type does not have a block type equivalent!");
            return null;
        })));
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTypeTag.id
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the ID of the item type.
        // @Example "minecraft:dirt" .id returns "minecraft:dirt".
        // -->
        handlers.put("id", (dat, obj) -> new TextTag(((ItemTypeTag) obj).internal.getId()));
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTypeTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the item type.
        // @Example "minecraft:dirt" .name returns "dirt".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(CoreUtilities.after(((ItemTypeTag) obj).internal.getName(), ":")));
    }

    public static ItemTypeTag getFor(Action<String> error, String text) {
        ItemType itemType = (ItemType) Utilities.getTypeWithDefaultPrefix(ItemType.class, text);
        if (itemType == null) {
            error.run("Invalid ItemTypeTag input!");
            return null;
        }
        return new ItemTypeTag(itemType);
    }

    public static ItemTypeTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof ItemTypeTag) ? (ItemTypeTag) text : getFor(error, text.toString());
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
        return "ItemTypeTag";
    }

    @Override
    public String toString() {
        return internal.getId();
    }
}
