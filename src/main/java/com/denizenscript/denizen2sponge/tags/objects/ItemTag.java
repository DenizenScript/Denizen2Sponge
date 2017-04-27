package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

public class ItemTag extends AbstractTagObject {

    // <--[object]
    // @Type ItemTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents an item.
    // -->

    private ItemStack internal;

    public ItemTag(ItemStack itm) {
        internal = itm;
    }

    public ItemStack getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name ItemTag.item_type
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType ItemTypeTag
        // @Returns the type of the item.
        // -->
        handlers.put("item_type", (dat, obj) -> new ItemTypeTag(((ItemTag) obj).internal.getItem()));
        // <--[tag]
        // @Name ItemTag.quantity
        // @Updated 2017/04/04
        // @Group Identification
        // @ReturnType IntegerTag
        // @Returns the amount of items in this stack.
        // -->
        handlers.put("quantity", (dat, obj) -> new IntegerTag(((ItemTag) obj).internal.getQuantity()));
        // <--[tag]
        // @Name ItemTag.max_stack_quantity
        // @Updated 2017/04/04
        // @Group Identification
        // @ReturnType IntegerTag
        // @Returns the maximum amount of items of this type in a stack.
        // -->
        handlers.put("max_stack_quantity", (dat, obj) -> new IntegerTag(((ItemTag) obj).internal.getMaxStackQuantity()));
        // <--[tag]
        // @Name ItemTag.data
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((ItemTag) obj).internal));
        // <--[tag]
        // @Name ItemTag.get[<TextTag>]
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the entity.
        // -->
        handlers.put("get", (dat, obj) -> {
            String keyName = dat.getNextModifier().toString();
            Key key = DataKeys.getKeyForName(keyName);
            if (key == null) {
                dat.error.run("Invalid key '" + keyName + "'!");
                return new NullTag();
            }
            return DataKeys.getValue(((ItemTag) obj).internal, key, dat.error);
        });
        // <--[tag]
        // @Name ItemTag.without_flags[<ListTag>]
        // @Updated 2017/02/13
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified flags removed.
        // -->
        handlers.put("without_flags", (dat, obj) -> {
            MapTag flags;
            ItemStack e = ((ItemTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = new MapTag(fm.get().flags.getInternal());
            }
            else {
                flags = new MapTag();
            }
            ListTag toRemove = ListTag.getFor(dat.error, dat.getNextModifier());
            for (AbstractTagObject k : toRemove.getInternal()) {
                flags.getInternal().remove(k.toString());
            }
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            its.offer(new FlagMapDataImpl(new FlagMap(flags)));
            return new ItemTag(its);
        });
        // <--[tag]
        // @Name ItemTag.with_flags[<MapTag>]
        // @Updated 2016/12/04
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified flag adjustments.
        // -->
        handlers.put("with_flags", (dat, obj) -> {
            MapTag flags;
            ItemStack e = ((ItemTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = new MapTag(fm.get().flags.getInternal());
            }
            else {
                flags = new MapTag();
            }
            MapTag toApply = MapTag.getFor(dat.error, dat.getNextModifier());
            flags.getInternal().putAll(toApply.getInternal());
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            its.offer(new FlagMapDataImpl(new FlagMap(flags)));
            return new ItemTag(its);
        });
        // <--[tag]
        // @Name ItemTag.with_quantity[<IntegerTag>]
        // @Updated 2016/12/04
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified quantity.
        // -->
        handlers.put("with_quantity", (dat, obj) -> {
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            its.setQuantity((int)IntegerTag.getFor(dat.error, dat.getNextModifier()).getInternal());
            return new ItemTag(its);
        });
        // <--[tag]
        // @Name ItemTag.with[<MapTag>]
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified data adjustments.
        // -->
        handlers.put("with", (dat, obj) -> {
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            MapTag toApply = MapTag.getFor(dat.error, dat.getNextModifier());
            for (Map.Entry<String, AbstractTagObject> a : toApply.getInternal().entrySet()) {
                DataKeys.tryApply(its, DataKeys.getKeyForName(a.getKey()), a.getValue(), dat.error);
            }
            return new ItemTag(its);
        });
        // <--[tag]
        // @Name ItemTag.has_flag[<TextTag>]
        // @Updated 2016/11/24
        // @Group Flag Data
        // @ReturnType BooleanTag
        // @Returns whether the entity has a flag with the specified key.
        // -->
        handlers.put("has_flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            ItemStack e = ((ItemTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            return new BooleanTag(flags.getInternal().containsKey(flagName));
        });
        // <--[tag]
        // @Name ItemTag.flag[<TextTag>]
        // @Updated 2016/11/24
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. May become TextTag regardless of input original type.
        // -->
        handlers.put("flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            ItemStack e = ((ItemTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            AbstractTagObject ato = flags.getInternal().get(flagName);
            if (ato == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on this item!");
                }
                return new NullTag();
            }
            return ato;
        });
    }

    public static ItemTag getFor(Action<String> error, String text) {
        List<String> split = CoreUtilities.split(text, '/', 3);
        ItemTypeTag type = ItemTypeTag.getFor(error, split.get(0));
        int q = 1;
        if (split.size() > 1) {
            q = (int)IntegerTag.getFor(error, split.get(1)).getInternal();
        }
        ItemStack its = ItemStack.of(type.getInternal(), q);
        if (split.size() > 2) {
            MapTag toApply = MapTag.getFor(error, split.get(2));
            for (Map.Entry<String, AbstractTagObject> a : toApply.getInternal().entrySet()) {
                DataKeys.tryApply(its, DataKeys.getKeyForName(a.getKey()), a.getValue(), error);
            }
        }
        return new ItemTag(its);
    }

    public static ItemTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof ItemTag) ? (ItemTag) text : getFor(error, text.toString());
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
        return "ItemTag";
    }
    @Override
    public String toString() {
        return internal.getItem().getId() + "/" + internal.getQuantity() + "/" + DataKeys.getAllKeys(internal).toString();
    }
}
