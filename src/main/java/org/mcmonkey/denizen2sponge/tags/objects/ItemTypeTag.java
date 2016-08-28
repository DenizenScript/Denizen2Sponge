package org.mcmonkey.denizen2sponge.tags.objects;

import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2core.utilities.Action;
import org.mcmonkey.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Optional;

public class ItemTypeTag extends AbstractTagObject {

    // <--[object]
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
        // @Name ItemTypeTag.id
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the ID of the item type.
        // @Example "minecraft:dirt" .id returns "minecraft:dirt".
        // -->
        handlers.put("id", (dat, obj) -> new TextTag(((ItemTypeTag) obj).internal.getId()));
        // <--[tag]
        // @Name ItemTypeTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the item type.
        // @Example "minecraft:dirt" .name returns "dirt".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((ItemTypeTag) obj).internal.getName()));
    }

    public static ItemTypeTag getFor(Action<String> error, String text) {
        Optional<ItemType> optItemType = Sponge.getRegistry().getType(ItemType.class, text);
        if (!optItemType.isPresent()) {
            error.run("Invalid ItemTypeTag input!");
            return null;
        }
        return new ItemTypeTag(optItemType.get());
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
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return internal.getId();
    }
}
