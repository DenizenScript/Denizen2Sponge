package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerTag extends AbstractTagObject {

    // <--[object]
    // @Type PlayerTag
    // @SubType EntityTag
    // @Group Entities
    // @Description Represents an online or offline player on the server. Identified by UUID.
    // @Note Sub-type becomes TextTag if not online!
    // -->

    private User internal;

    public PlayerTag(User player) {
        internal = player;
    }

    public User getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name PlayerTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the player.
        // @Example "Bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((PlayerTag) obj).internal.getName()));
        // <--[tag]
        // @Name PlayerTag.food_level
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the food level of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("food_level", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new IntegerTag(pl.foodLevel().get());
        });
        // <--[tag]
        // @Name PlayerTag.exhaustion
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the exhaustion of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("exhaustion", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new NumberTag(pl.exhaustion().get());
        });
        // <--[tag]
        // @Name PlayerTag.saturation
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the saturation of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("saturation", (dat, obj) ->{
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new NumberTag(pl.saturation().get());
        });
        // <--[tag]
        // @Name PlayerTag.gamemode
        // @Updated 2017/03/28
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the gamemode of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("gamemode", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new TextTag(pl.gameMode().get().toString());
        });
        // <--[tag]
        // @Name PlayerTag.block_on_cursor[<NumberTag>]
        // @Updated 2017/03/30
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the block the player has their cursor on, up to a maximum distance. If no distance is specified, the default hand-reach distance is used. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("block_on_cursor", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new LocationTag(BlockRay.from(pl)
                    .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .distanceLimit(dat.hasNextModifier() ? NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal() :
                            (Utilities.getHandReach(pl))).build().end().get().getLocation());
        });
        // <--[tag]
        // @Name PlayerTag.entities_on_cursor[<MapTag>]
        // @Updated 2017/04/04
        // @Group Current Information
        // @ReturnType ListTag<EntityTag>
        // @Returns a list of entities of a specified type (or any type if unspecified) intersecting with
        // the line of sight of the player. If no range is specified, it defaults to the player's hand reach.
        // Input is type:<EntityTypeTag>|range:<NumberTag>
        // ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("entities_on_cursor", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            ListTag list = new ListTag();
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            EntityTypeTag requiredTypeTag = null;
            if (map.getInternal().containsKey("type")) {
                requiredTypeTag = EntityTypeTag.getFor(dat.error, map.getInternal().get("type"));
            }
            double range;
            if (map.getInternal().containsKey("range")) {
                range = NumberTag.getFor(dat.error, map.getInternal().get("range")).getInternal();
            }
            else {
                range = (Utilities.getHandReach(pl));
            }
            Set<EntityUniverse.EntityHit> entHits = pl.getWorld().getIntersectingEntities(pl, range);
            for (EntityUniverse.EntityHit entHit : entHits) {
                Entity ent = entHit.getEntity();
                if ((requiredTypeTag == null || ent.getType().equals(requiredTypeTag.getInternal())) && !ent.equals(pl)) {
                    list.getInternal().add(new EntityTag(ent));
                }
            }
            return list;
        });
        // <--[tag]
        // @Name PlayerTag.sneaking
        // @Updated 2017/04/05
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether the player is sneaking or not. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("sneaking", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new BooleanTag(pl.get(Keys.IS_SNEAKING).get());
        });
        // <--[tag]
        // @Name PlayerTag.sprinting
        // @Updated 2017/04/05
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether the player is sprinting or not. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("sprinting", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new BooleanTag(pl.get(Keys.IS_SPRINTING).get());
        });
        // <--[tag]
        // @Name PlayerTag.ip
        // @Updated 2017/04/08
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the current IP of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("ip", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new TextTag(pl.getConnection().getAddress().getAddress().getHostName());
        });
        // <--[tag]
        // @Name PlayerTag.latency
        // @Updated 2017/04/17
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the current latency of the player, in milliseconds. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("latency", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new IntegerTag(pl.getConnection().getLatency());
        });
        // <--[tag]
        // @Name PlayerTag.selected_slot
        // @Updated 2017/04/20
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the index of the hotbar slot that is currently selected by the player.
        // -->
        handlers.put("selected_slot", (dat, obj) -> new IntegerTag(((PlayerInventory) ((PlayerTag) obj).internal.getInventory()).getHotbar().getSelectedSlotIndex() + 1));
        // <--[tag]
        // @Name PlayerTag.inventory
        // @Updated 2017/08/31
        // @Group Properties
        // @ReturnType InventoryTag
        // @Returns the inventory this player is carrying. Equipment is not included.
        // -->
        handlers.put("inventory", (dat, obj) -> new InventoryTag(((UserInventory) ((PlayerTag) obj).internal.getInventory()).getMain()));
    }

    public static PlayerTag getFor(Action<String> error, String text) {
        try {
            Optional<Player> oplayer = Sponge.getServer().getPlayer(UUID.fromString(text));
            if (!oplayer.isPresent()) {
                error.run("Invalid PlayerTag UUID input!");
                return null;
            }
            return new PlayerTag(oplayer.get());
        }
        catch (IllegalArgumentException e) { // TODO: better impl of this backup logic
            Optional<Player> oplayer = Sponge.getServer().getPlayer(text);
            if (!oplayer.isPresent()) {
                error.run("Invalid PlayerTag named input!");
                return null;
            }
            return new PlayerTag(oplayer.get());
        }
    }

    public static PlayerTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof PlayerTag) ? (PlayerTag) text : getFor(error, text.toString());
    }

    public Player getOnline(Action<String> error) {
        Optional<Player> pl = internal.getPlayer();
        if (pl.isPresent()) {
            return pl.get();
        }
        error.run("Player is not online, tag is not valid!");
        return null;
    }

    public Player getOnline(TagData data) {
        Optional<Player> pl = internal.getPlayer();
        if (pl.isPresent()) {
            return pl.get();
        }
        if (data.hasFallback()) {
            data.error.run("Player is not online, tag is not valid!");
        }
        return null;
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        Optional<Player> pl = internal.getPlayer();
        if (pl.isPresent()) {
            return new EntityTag(pl.get());
        }
        // Might be odd. Perhaps worthwhile to drop a warning here...
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }


    @Override
    public String getTagTypeName() {
        return "PlayerTag";
    }
    @Override
    public String debug() {
        return toString() + "/" + internal.getName();
    }
}
