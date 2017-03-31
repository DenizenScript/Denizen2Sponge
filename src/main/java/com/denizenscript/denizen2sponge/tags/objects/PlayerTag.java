package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.blockray.BlockRay;

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
        // @Returns the food level of the player.
        // -->
        handlers.put("food_level", (dat, obj) -> new IntegerTag(((PlayerTag) obj).internal.foodLevel().get()));
        // <--[tag]
        // @Name PlayerTag.exhaustion
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the exhaustion of the player.
        // -->
        handlers.put("exhaustion", (dat, obj) -> new NumberTag(((PlayerTag) obj).internal.exhaustion().get()));
        // <--[tag]
        // @Name PlayerTag.saturation
        // @Updated 2017/03/24
        // @Group Properties
        // @ReturnType NumberTag
        // @Returns the saturation of the player.
        // -->
        handlers.put("saturation", (dat, obj) -> new NumberTag(((PlayerTag) obj).internal.saturation().get()));
        // <--[tag]
        // @Name PlayerTag.gamemode
        // @Updated 2017/03/28
        // @Group Properties
        // @ReturnType TextTag
        // @Returns the gamemode of the player.
        // -->
        handlers.put("gamemode", (dat, obj) -> new TextTag(((PlayerTag) obj).internal.gameMode().get().toString()));
        // <--[tag]
        // @Name PlayerTag.block_on_cursor[<NumberTag>]
        // @Updated 2017/03/30
        // @Group Current Information
        // @ReturnType LocationTag
        // @Returns the block the player has their cursor on, up to a maximum distance. If no distance is specified, the default hand-reach distance is used.
        // -->
        handlers.put("block_on_cursor", (dat, obj) -> new LocationTag(BlockRay.from(((PlayerTag) obj).internal)
                .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .distanceLimit(dat.hasNextModifier() ? NumberTag.getFor(dat.error, dat.getNextModifier()).getInternal() :
                (Utilities.getHandReach(((PlayerTag) obj).internal))).build().end().get().getLocation()));
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

    public void checkValid(Action<String> error) {
        if (!internal.isOnline()) {
            error.run("That player is no longer online!");
            throw new RuntimeException("That player is no longer online!"); // Just in case
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
        checkValid(data.error);
        return new EntityTag(internal);
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }

    @Override
    public String debug() {
        return toString() + "/" + internal.getName();
    }
}
