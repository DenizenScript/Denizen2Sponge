package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractTagObject {

    // <--[object]
    // @Since 0.3.0
    // @Type PlayerTag
    // @SubType EntityTag
    // @Group Entities
    // @Description Represents an online or offline player on the server. Identified by UUID.
    // @Note Sub-type becomes TextTag if not online!
    // -->

    // <--[explanation]
    // @Since 0.3.0
    // @Name Default Statistics
    // @Group Useful Lists
    // @Description
    // A list of all default general statistics can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/statistic/Statistics.html>default general statistic list<@/link>
    // These can be used alone in the statistic tag for player objects.
    // A list of all default statistic types can be found here:
    // <@link url https://jd.spongepowered.org/7.0.0-SNAPSHOT/org/spongepowered/api/statistic/StatisticTypes.html>default statistic type list <@/link>
    // These can be used with a modifier in the statistic tag.
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
        // @Since 0.4.0
        // @Name PlayerTag.cooldown[<ItemTypeTag>]
        // @Updated 2018/01/07
        // @Group Properties
        // @ReturnType DurationTag
        // @Returns the cooldown on an item type for the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("cooldown", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            ItemTypeTag item = ItemTypeTag.getFor(dat.error, dat.getNextModifier());
            return new DurationTag(pl.getCooldownTracker().getCooldown(item.getInternal()).orElse(0) * (1.0 / 20.0));
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name PlayerTag.cooldown_fraction[<ItemTypeTag>]
        // @Updated 2018/01/07
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the cooldown fraction on an item type for the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("cooldown_fraction", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            ItemTypeTag item = ItemTypeTag.getFor(dat.error, dat.getNextModifier());
            return new NumberTag(pl.getCooldownTracker().getFractionRemaining(item.getInternal()).orElse(0));
        });
        // <--[tag]
        // @Since 0.3.0
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
        // @Since 0.3.0
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
        // @Since 0.3.0
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
        // @Since 0.4.0
        // @Name PlayerTag.has_advancement[<TextTag>]
        // @Updated 2018/02/07
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether the player has completed an advancement. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("has_advancement", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            String id = dat.getNextModifier().toString();
            Advancement advancement = (Advancement) Utilities.getTypeWithDefaultPrefix(Advancement.class, id);
            if (advancement == null) {
                if (!dat.hasFallback()) {
                    dat.error.run("There's no registered advancement that matches the specified id!");
                }
                return new NullTag();
            }
            return new BooleanTag(pl.getProgress(advancement).achieved());
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name PlayerTag.has_cooldown[<ItemTypeTag>]
        // @Updated 2018/01/07
        // @Group Properties
        // @ReturnType BooleanTag
        // @Returns whether an item type has cooldown for the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("has_cooldown", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            ItemTypeTag item = ItemTypeTag.getFor(dat.error, dat.getNextModifier());
            return new BooleanTag(pl.getCooldownTracker().hasCooldown(item.getInternal()));
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name PlayerTag.inventory
        // @Updated 2017/08/31
        // @Group Properties
        // @ReturnType InventoryTag
        // @Returns the inventory this player is carrying. Equipment is not included.
        // -->
        handlers.put("inventory", (dat, obj) -> new InventoryTag(((UserInventory) ((PlayerTag) obj).internal.getInventory()).getMain()));
        // <--[tag]
        // @Since 0.3.0
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
        // @Since 0.3.0
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
        // @Since 0.3.0
        // @Name PlayerTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the player.
        // @Example "Bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((PlayerTag) obj).internal.getName()));
        // <--[tag]
        // @Since 0.3.0
        // @Name PlayerTag.saturation
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the saturation of the player. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("saturation", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new NumberTag(pl.saturation().get());
        });
        // <--[tag]
        // @Since 0.3.0
        // @Name PlayerTag.selected_slot
        // @Updated 2017/04/20
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the index of the hotbar slot that is currently selected by the player.
        // -->
        handlers.put("selected_slot", (dat, obj) -> new IntegerTag(((PlayerInventory) ((PlayerTag) obj).internal.getInventory()).getHotbar().getSelectedSlotIndex() + 1));
        // <--[tag]
        // @Since 0.3.0
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
        // @Since 0.3.0
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
        // @Since 0.3.0
        // @Name PlayerTag.statistic[<MapTag>]
        // @Updated 2017/09/25
        // @Group Properties
        // @ReturnType IntegerTag
        // @Returns the current value of the player's specified statistic. It also accepts
        // statistic types with an entity, block or item as a modifier. Note that the statistics
        // that accept modifiers are in a different list than the general ones.
        // Input is type:<TextTag> for general statistics and
        // type:<TextTag>|entity:<EntityTypeTag>/block:<BlockTypeTag>/item:<ItemTypeTag>
        // for specific ones, choosing the correct modifier.
        // -->
        handlers.put("statistic", (dat, obj) -> {
            User pl = ((PlayerTag) obj).internal;
            MapTag map = MapTag.getFor(dat.error, dat.getNextModifier());
            String name = TextTag.getFor(dat.error, map.getInternal().get("type")).getInternal();
            Statistic statistic;
            if (map.getInternal().containsKey("entity")) {
                Optional<StatisticType> type = Sponge.getRegistry().getType(StatisticType.class, name);
                if (!type.isPresent()) {
                    if (!dat.hasFallback()) {
                        dat.error.run("The specified statistic type does not exist!");
                    }
                    return new NullTag();
                }
                EntityTypeTag ent = EntityTypeTag.getFor(dat.error, map.getInternal().get("entity"));
                statistic = Sponge.getRegistry().getEntityStatistic(type.get(), ent.getInternal()).get();
            }
            else if (map.getInternal().containsKey("block")) {
                Optional<StatisticType> type = Sponge.getRegistry().getType(StatisticType.class, name);
                if (!type.isPresent()) {
                    if (!dat.hasFallback()) {
                        dat.error.run("The specified statistic type does not exist!");
                    }
                    return new NullTag();
                }
                BlockTypeTag block = BlockTypeTag.getFor(dat.error, map.getInternal().get("block"));
                statistic = Sponge.getRegistry().getBlockStatistic(type.get(), block.getInternal()).get();
            }
            else if (map.getInternal().containsKey("item")) {
                Optional<StatisticType> type = Sponge.getRegistry().getType(StatisticType.class, name);
                if (!type.isPresent()) {
                    if (!dat.hasFallback()) {
                        dat.error.run("The specified statistic type does not exist!");
                    }
                    return new NullTag();
                }
                ItemTypeTag item = ItemTypeTag.getFor(dat.error, map.getInternal().get("item"));
                statistic = Sponge.getRegistry().getItemStatistic(type.get(), item.getInternal()).get();
            }
            else {
                Optional<Statistic> opt = Sponge.getRegistry().getType(Statistic.class, name);
                if (!opt.isPresent()) {
                    if (!dat.hasFallback()) {
                        dat.error.run("The specified general statistic does not exist!");
                    }
                    return new NullTag();
                }
                statistic = opt.get();
            }
            return new IntegerTag(pl.getStatisticData().get(statistic).orElse((long) 0));
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name PlayerTag.tablist_header
        // @Updated 2018/02/04
        // @Group Properties
        // @ReturnType FormattedTextTag
        // @Returns whether the player's tablist header. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("tablist_header", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new FormattedTextTag(pl.getTabList().getHeader().orElse(Text.of("")));
        });
        // <--[tag]
        // @Since 0.4.0
        // @Name PlayerTag.tablist_footer
        // @Updated 2018/02/04
        // @Group Properties
        // @ReturnType FormattedTextTag
        // @Returns whether the player's tablist footer. ONLINE-PLAYERS-ONLY.
        // -->
        handlers.put("tablist_footer", (dat, obj) -> {
            Player pl = ((PlayerTag) obj).getOnline(dat);
            if (pl == null) {
                return new NullTag();
            }
            return new FormattedTextTag(pl.getTabList().getFooter().orElse(Text.of("")));
        });
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
