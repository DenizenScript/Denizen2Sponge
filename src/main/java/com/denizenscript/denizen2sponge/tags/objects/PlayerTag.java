package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractTagObject {

    // <--[object]
    // @Type PlayerTag
    // @SubType EntityTag
    // @Group Entities
    // @Description Represents an online player on the server. Identified by UUID.
    // -->

    private Player internal;

    public PlayerTag(Player player) {
        internal = player;
    }

    public Player getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name PlayerTag.held_item
        // @Updated 2016/11/24
        // @Group Identification
        // @ReturnType ItemTag
        // @Returns the item held by the player.
        // @Example "Bob" .held_item may return "minecraft:iron_axe/1/".
        // -->
        handlers.put("held_item", (dat, obj) -> new ItemTag(((PlayerTag) obj).internal.getItemInHand(HandTypes.MAIN_HAND)
             .orElse(ItemStack.of(ItemTypes.NONE, 1))));
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
        // @Updated 2017/02/28
        // @Group Player Properties
        // @ReturnType IntegerTag
        // @Returns the food level of the entity.
        // -->
        handlers.put("food_level", (dat, obj) -> {
            Humanoid player = ((PlayerTag) obj).internal;
            Integer food = player.foodLevel().get();
            return new IntegerTag(food);
        });
        // <--[tag]
        // @Name PlayerTag.saturation
        // @Updated 2017/02/28
        // @Group Player Properties
        // @ReturnType NumberTag
        // @Returns the saturation level of the entity.
        // -->
        handlers.put("saturation", (dat, obj) -> {
            Humanoid player = ((PlayerTag) obj).internal;
            Double saturation = player.saturation().get();
            return new NumberTag(saturation);
        });
        // <--[tag]
        // @Name PlayerTag.exhaustion
        // @Updated 2017/02/28
        // @Group Player Properties
        // @ReturnType NumberTag
        // @Returns the exhaustion level of the entity.
        // -->
        handlers.put("exhaustion", (dat, obj) -> {
            Humanoid player = ((PlayerTag) obj).internal;
            Double exhaustion = player.exhaustion().get();
            return new NumberTag(exhaustion);
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

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new EntityTag(internal);
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }
}
