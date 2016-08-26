package org.mcmonkey.denizen2sponge.tags.objects;

import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2core.utilities.Action;
import org.mcmonkey.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractTagObject {

    // TODO: EntityTag subtag!

    // TODO: OfflinePlayerTag?

    // <--[object]
    // @Type PlayerTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an online on the server.
    // -->

    private Player player;

    public PlayerTag(Player player) {
        this.player = player;
    }

    public Player getInternal() {
        return player;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name PlayerTag.name
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the player.
        // @Example "Bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((PlayerTag) obj).player.getName()));
        // <--[tag]
        // @Name PlayerTag.uuid
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the player.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((PlayerTag) obj).player.getUniqueId().toString()));
    }

    public static PlayerTag getFor(Action<String> error, String text) {
        Optional<Player> oplayer = Sponge.getServer().getPlayer(UUID.fromString(text));
        if (!oplayer.isPresent()) {
            error.run("Invalid PlayerTag input!");
            return null;
        }
        return new PlayerTag(oplayer.get());
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
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return player.getUniqueId().toString();
    }
}
