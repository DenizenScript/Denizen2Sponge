package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.denizenscript.denizen2sponge.utilities.UtilLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.slot.FuelSlot;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.World;

import java.util.*;

public class InventoryTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
    // @Type InventoryTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents an inventory in the world.
    // -->

    private Inventory internal;

    public String remAs = null;

    public InventoryTag(Inventory player) {
        internal = player;
    }

    public Inventory getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Since 0.3.0
        // @Name InventoryTag.contains[<ItemTag>]
        // @Updated 2017/06/17
        // @Group General Information
        // @ReturnType BooleanTag
        // @Returns whether the inventory contains the specified quantity or more of the specified item.
        // @Example "block/0,1,2,world" .contains[diamond/3] might return "false".
        // -->
        handlers.put("contains", (dat, obj) -> BooleanTag.getForBoolean(((InventoryTag) obj).internal.contains(ItemTag.getFor(dat.checkedError, dat.getNextModifier(), dat.currentQueue).getInternal())));
        // <--[tag]
        // @Since 0.3.0
        // @Name InventoryTag.contains_any[<ItemTag>]
        // @Updated 2017/06/17
        // @Group General Information
        // @ReturnType BooleanTag
        // @Returns whether the inventory contains any quantity of the specified item.
        // @Example "block/0,1,2,world" .contains_any[diamond] might return "true".
        // -->
        handlers.put("contains_any", (dat, obj) -> BooleanTag.getForBoolean(((InventoryTag) obj).internal.containsAny(ItemTag.getFor(dat.checkedError, dat.getNextModifier(), dat.currentQueue).getInternal())));
        // <--[tag]
        // @Since 0.4.0
        // @Name InventoryTag.fuel
        // @Updated 2018/01/01
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns the item in the fuel slot of a furnace inventory.
        // @Example "block/0,1,2,world" .fuel might return "coal".
        // -->
        handlers.put("fuel", (dat, obj) -> {
            Inventory inventory = ((InventoryTag) obj).internal.query(QueryOperationTypes.INVENTORY_TYPE.of(FuelSlot.class));
            if (inventory instanceof EmptyInventory) {
                if (!dat.hasFallback()) {
                    dat.error.run("This inventory does not contain any fuel slots!");
                }
                return NullTag.NULL;
            }
            ItemStack item = inventory.peek().orElse(ItemStack.empty());
            return new ItemTag(item);
        });
        // <--[tag]
        // @Since 0.5.0
        // @Name InventoryTag.remembered_name
        // @Updated 2018/06/05
        // @Group General Information
        // @ReturnType TextTag
        // @Returns the inventory's remembered name (as is set by the <@link command rememberinventory>rememberinventory command<@/link>, if any.
        // @Example "shared/test" returns "test".
        // -->
        handlers.put("remembered_name", (dat, obj) -> new TextTag(((InventoryTag) obj).remAs));
        // <--[tag]
        // @Since 0.3.0
        // @Name InventoryTag.name
        // @Updated 2017/06/10
        // @Group General Information
        // @ReturnType TextTag
        // @Returns the inventory's name (in English). // TODO: Translation options? Also, output testing?
        // @Example "player/bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((InventoryTag) obj).internal.getName().get(Locale.ENGLISH)));
        // <--[tag]
        // @Since 0.4.0
        // @Name InventoryTag.result
        // @Updated 2018/01/01
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns the item in the result slot of a furnace inventory.
        // @Example "block/0,1,2,world" .result might return "iron_ingot".
        // -->
        handlers.put("result", (dat, obj) -> {
            Inventory inventory = ((InventoryTag) obj).internal.query(QueryOperationTypes.INVENTORY_TYPE.of(OutputSlot.class));
            if (inventory instanceof EmptyInventory) {
                if (!dat.hasFallback()) {
                    dat.error.run("This inventory does not contain any result slots!");
                }
                return NullTag.NULL;
            }
            ItemStack item = inventory.peek().orElse(ItemStack.empty());
            return new ItemTag(item);
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name InventoryTag.size
        // @Updated 2017/06/11
        // @Group General Information
        // @ReturnType IntegerTag
        // @Returns the inventory's size (in slots).
        // @Example "block/0,1,2,world" .size might return "36".
        // -->
        handlers.put("size", (dat, obj) -> new IntegerTag(((InventoryTag) obj).internal.size()));
        // <--[tag]
        // @Since 0.3.0
        // @Name InventoryTag.slot[<IntegerTag>]
        // @Updated 2017/09/05
        // @Group General Information
        // @ReturnType ItemTag
        // @Returns the item in the inventory's specified slot.
        // @Example "block/0,1,2,world" .slot[8] might return "diamond".
        // -->
        handlers.put("slot", (dat, obj) -> {
            Inventory inventory = ((InventoryTag) obj).internal.query(QueryOperationTypes.INVENTORY_TYPE.of(OrderedInventory.class));
            if (inventory instanceof EmptyInventory) {
                if (!dat.hasFallback()) {
                    dat.error.run("This inventory does not contain slots ordered by index!");
                }
                return NullTag.NULL;
            }
            int slot = (int) IntegerTag.getFor(dat.checkedError, dat.getNextModifier()).getInternal();
            if (slot < 1 || slot > inventory.capacity()) {
                if (!dat.hasFallback()) {
                    dat.error.run("Invalid slot index specified!");
                }
                return NullTag.NULL;
            }
            ItemStack item = ((OrderedInventory) inventory).peek(new SlotIndex(slot - 1)).orElse(ItemStack.empty());
            return new ItemTag(item);
        });
    }

    public static InventoryTag getFor(Action<String> error, String text) {
        List<String> split = CoreUtilities.split(text, '/');
        if (split.get(0).equals("player")) {
            Optional<Player> oplayer = Sponge.getServer().getPlayer(UUID.fromString(split.get(1)));
            if (!oplayer.isPresent()) {
                error.run("Invalid PlayerTag UUID input!");
                return null;
            }
            return new InventoryTag(oplayer.get().getInventory());
        }
        else if (split.get(0).equals("entity")) {
            UUID id = UUID.fromString(split.get(1));
            for (World world : Sponge.getServer().getWorlds()) {
                Optional<Entity> e = world.getEntity(id);
                if (e.isPresent() && e.get() instanceof Carrier) {
                    return new InventoryTag(((Carrier) e.get()).getInventory());
                }
            }
            error.run("Invalid EntityTag UUID input!");
            return null;
        }
        else if (split.get(0).equals("block")) {
            LocationTag lt = LocationTag.getFor(error, split.get(1));
            return new InventoryTag(((TileEntityCarrier) lt.getInternal().toLocation().getTileEntity().get()).getInventory());
        }
        else if (split.get(0).equals("shared")) {
            return Denizen2Sponge.rememberedInventories.get(split.get(1));
        }
        else {
            error.run("Inventory type not known to the system: " + split.get(0));
            return null;
        }
    }

    public static InventoryTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof InventoryTag) ? (InventoryTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString(false));
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean unks) {
        if (internal instanceof MainPlayerInventory) {
            return "player/" + ((PlayerInventory) (internal).parent()).getCarrier().get().getUniqueId().toString();
        }
        else if (internal instanceof CarriedInventory) {
            Object o = ((CarriedInventory) internal).getCarrier().orElse(null);
            if (o == null) {
                if (remAs != null) {
                    return "shared/" + remAs;
                }
                return "((UNKNOWN INVENTORY TYPE))";
            }
            if (o instanceof Entity) {
                return "entity/" + ((Entity) o).getUniqueId().toString();
            }
            else if (o instanceof TileEntityCarrier) {
                LocatableBlock lb = ((TileEntityCarrier) o).getLocatableBlock();
                return "block/" + new LocationTag(new UtilLocation(lb.getPosition(), lb.getWorld()));
            }
        }
        else if (remAs != null) {
            return "shared/" + remAs;
        }
        // TODO: Generic inventory save method
        if (!unks) {
            // TODO: Handle all inventory types somehow???
            throw new RuntimeException("Inventory type not known to the system!");
        }
        else {
            return "((UNKNOWN INVENTORY TYPE))";
        }
    }

    @Override
    public String savable() {
        return getTagTypeName() + saveMark() + toString(false);
    }

    @Override
    public String getTagTypeName() {
        return "InventoryTag";
    }

    @Override
    public String debug() {
        return toString(true);
    }
}
