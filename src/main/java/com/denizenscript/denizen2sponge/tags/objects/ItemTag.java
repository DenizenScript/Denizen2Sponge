package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.commands.CommandQueue;
import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.spongescripts.ItemScript;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullType;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
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

    public ItemScript getSourceScript() {
        Optional<FlagMap> fm = internal.get(FlagHelper.FLAGMAP);
        if (fm.isPresent()) {
            MapTag flags = fm.get().flags;
            if (flags.getInternal().containsKey("_d2_script")) {
                AbstractTagObject scriptObj = flags.getInternal().get("_d2_script");
                if (scriptObj instanceof ScriptTag) {
                    ScriptTag script = (ScriptTag) scriptObj;
                    if (script.getInternal() instanceof ItemScript) {
                        return (ItemScript) script.getInternal();
                    }
                }
            }
        }
        return null;
    }

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.data
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // TODO: Create and reference an explanation of basic item keys.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((ItemTag) obj).internal));
        // <--[tag]
        // @Since 0.5.0
        // @Name ItemTag.is_script
        // @Updated 2018/05/21
        // @Group General Information
        // @ReturnType BooleanTag
        // @Returns whether the item was sourced from a script.
        // -->
        handlers.put("is_script", (dat, obj) -> new BooleanTag(((ItemTag) obj).getSourceScript() != null));
        // <--[tag]
        // @Since 0.5.0
        // @Name ItemTag.script
        // @Updated 2018/05/21
        // @Group General Information
        // @ReturnType ScriptTag
        // @Returns the script this item tag was created with, if any.
        // -->
        handlers.put("script", (dat, obj) -> {
            ItemScript src = ((ItemTag) obj).getSourceScript();
            if (src == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Item was not sourced from a script.");
                }
                return new NullTag();
            }
            return new ScriptTag(src);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.flag[<TextTag>]
        // @Updated 2016/11/24
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. May become TextTag regardless of input original type.
        // Optionally don't specify anything to get the entire flag map.
        // -->
        handlers.put("flag", (dat, obj) -> {
            MapTag flags;
            ItemStack e = ((ItemTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            if (!dat.hasNextModifier()) {
                return flags;
            }
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            AbstractTagObject ato = flags.getInternal().get(flagName);
            if (ato == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid flag specified, not present on this item!");
                }
                return new NullTag();
            }
            return ato;
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.get[<TextTag>]
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the entity.
        // TODO: Create and reference an explanation of basic item keys.
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
        // @Since 0.3.0
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
        // @Since 0.3.0
        // @Name ItemTag.item_type
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType ItemTypeTag
        // @Returns the type of the item.
        // -->
        handlers.put("item_type", (dat, obj) -> new ItemTypeTag(((ItemTag) obj).internal.getType()));
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.max_stack_quantity
        // @Updated 2017/04/04
        // @Group Identification
        // @ReturnType IntegerTag
        // @Returns the maximum amount of items of this type in a stack.
        // -->
        handlers.put("max_stack_quantity", (dat, obj) -> new IntegerTag(((ItemTag) obj).internal.getMaxStackQuantity()));
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.quantity
        // @Updated 2017/04/04
        // @Group Identification
        // @ReturnType IntegerTag
        // @Returns the amount of items in this stack.
        // -->
        handlers.put("quantity", (dat, obj) -> new IntegerTag(((ItemTag) obj).internal.getQuantity()));
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.represented_player_name
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's name of this skull item.
        // -->
        handlers.put("represented_player_name", (dat, obj) -> {
            ItemStack item = ((ItemTag) obj).internal;
            Optional<SkullType> type = item.get(Keys.SKULL_TYPE);
            if (!type.isPresent() || type.get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("This item is not a player skull!");
                }
                return new NullTag();
            }
            return new TextTag(item.get(Keys.REPRESENTED_PLAYER).get().getName().get());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.represented_player_skin
        // @Updated 2017/10/16
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's skin of this skull item.
        // -->
        handlers.put("represented_player_skin", (dat, obj) -> {
            ItemStack item = ((ItemTag) obj).internal;
            Optional<SkullType> type = item.get(Keys.SKULL_TYPE);
            if (!type.isPresent() || type.get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("This item is not a player skull!");
                }
                return new NullTag();
            }
            ProfileProperty p = item.get(Keys.REPRESENTED_PLAYER).get().getPropertyMap().get("textures").iterator().next();
            return new TextTag(p.getValue() + "|" + p.getSignature().get());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.represented_player_uuid
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the represented player's unique id of this skull item.
        // -->
        handlers.put("represented_player_uuid", (dat, obj) -> {
            ItemStack item = ((ItemTag) obj).internal;
            Optional<SkullType> type = item.get(Keys.SKULL_TYPE);
            if (!type.isPresent() || type.get() != SkullTypes.PLAYER) {
                if (!dat.hasFallback()) {
                    dat.error.run("This item is not a player skull!");
                }
                return new NullTag();
            }
            return new TextTag(item.get(Keys.REPRESENTED_PLAYER).get().getUniqueId().toString());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.skull_type
        // @Updated 2017/10/15
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the type of skull this item is.
        // -->
        handlers.put("skull_type", (dat, obj) -> {
            ItemStack item = ((ItemTag) obj).internal;
            Optional<SkullType> type = item.get(Keys.SKULL_TYPE);
            if (!type.isPresent()) {
                if (!dat.hasFallback()) {
                    dat.error.run("This item is not a skull!");
                }
                return new NullTag();
            }
            return new TextTag(type.get().getId());
        });
        // <--[tag]
        // @Since 0.3.0
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
        // @Since 0.3.0
        // @Name ItemTag.with_quantity[<IntegerTag>]
        // @Updated 2016/12/04
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified quantity.
        // -->
        handlers.put("with_quantity", (dat, obj) -> {
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            its.setQuantity((int) IntegerTag.getFor(dat.error, dat.getNextModifier()).getInternal());
            return new ItemTag(its);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name ItemTag.with[<MapTag>]
        // @Updated 2016/11/24
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns a copy of the item, with the specified data adjustments.
        // TODO: Create and reference an explanation of basic item keys.
        // -->
        handlers.put("with", (dat, obj) -> {
            ItemStack its = ((ItemTag) obj).internal.createSnapshot().createStack();
            MapTag toApply = MapTag.getFor(dat.error, dat.getNextModifier());
            for (Map.Entry<String, AbstractTagObject> a : toApply.getInternal().entrySet()) {
                Key k = DataKeys.getKeyForName(a.getKey());
                if (k == null) {
                    dat.error.run("Key '" + a.getKey() + "' does not seem to exist.");
                }
                DataKeys.tryApply(its, k, a.getValue(), dat.error);
            }
            return new ItemTag(its);
        });
        // <--[tag]
        // @Since 0.3.0
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
    }

    public static ItemTag getFor(Action<String> error, String text, CommandQueue queue) {
        List<String> split = CoreUtilities.split(text, '/', 3);
        int q = 1;
        if (split.size() > 1) {
            q = (int) IntegerTag.getFor(error, split.get(1)).getInternal();
        }
        Optional<ItemType> optItemType = Sponge.getRegistry().getType(ItemType.class, text);
        ItemStack its;
        if (optItemType.isPresent()) {
            its = ItemStack.of(optItemType.get(), q);
        }
        else {
            String tlow = CoreUtilities.toLowerCase(text);
            if (Denizen2Sponge.itemScripts.containsKey(tlow)) {
                its = Denizen2Sponge.itemScripts.get(tlow).getItemCopy(queue);
            }
            else {
                error.run("Invalid item type '" + text + "'");
                return null;
            }
        }
        if (split.size() > 2) {
            MapTag toApply = MapTag.getFor(error, split.get(2));
            for (Map.Entry<String, AbstractTagObject> a : toApply.getInternal().entrySet()) {
                Key k = DataKeys.getKeyForName(a.getKey());
                if (k == null) {
                    error.run("Key '" + a.getKey() + "' does not seem to exist.");
                }
                DataKeys.tryApply(its, k, a.getValue(), error);
            }
        }
        return new ItemTag(its);
    }

    public static ItemTag getFor(Action<String> error, AbstractTagObject ato, CommandQueue queue) {
        return (ato instanceof ItemTag) ? (ItemTag) ato : getFor(error, ato.toString());
    }

    public static ItemTag getFor(Action<String> error, String text) {
        return getFor(error, text, null);
    }

    public static ItemTag getFor(Action<String> error, AbstractTagObject ato) {
        return getFor(error, ato, null);
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
        return internal.getType().getId() + "/" + internal.getQuantity() + "/" + DataKeys.getAllKeys(internal).toString();
    }
}
