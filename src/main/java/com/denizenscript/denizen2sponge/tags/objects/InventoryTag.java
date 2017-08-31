package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
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
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.World;

import java.util.*;

public class InventoryTag extends AbstractTagObject {

    // <--[object]
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
        // @Name InventoryTag.name
        // @Updated 2017/06/10
        // @Group General Information
        // @ReturnType TextTag
        // @Returns the inventory's name (in English).
        // @Example "player/bob" .name might return "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((InventoryTag) obj).internal.getName().get(Locale.ENGLISH)));
        // @Name InventoryTag.size
        // @Updated 2017/06/11
        // @Group General Information
        // @ReturnType IntegerTag
        // @Returns the inventory's size (in slots).
        // @Example "block/0,1,2,world" .size might return "36".
        // -->
        handlers.put("size", (dat, obj) -> new IntegerTag(((InventoryTag) obj).internal.size()));
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
        if (remAs != null) {
            return "shared/" + remAs;
        }
        if (internal instanceof PlayerInventory) {
            return "player/" + ((PlayerInventory) internal).getCarrier().get().getUniqueId().toString();
        }
        else if (internal instanceof CarriedInventory) {
            Object o = ((CarriedInventory) internal).getCarrier().orElse(null);
            if (o == null) {
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
        return toString();
    }
}
