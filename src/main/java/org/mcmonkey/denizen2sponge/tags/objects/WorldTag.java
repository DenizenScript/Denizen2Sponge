package org.mcmonkey.denizen2sponge.tags.objects;

import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2core.utilities.Action;
import org.mcmonkey.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;

public class WorldTag extends AbstractTagObject {

    // <--[object]
    // @Type WorldTag
    // @SubType TextTag
    // @Group Mathematics
    // @Description Represents a world on the server.
    // -->

    private World world;

    public WorldTag(World world) {
        this.world = world;
    }

    public World getInternal() {
        return world;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name WorldTag.name
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the world.
        // @Example "world" .name returns "world".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((WorldTag) obj).world.getName()));
        // <--[tag]
        // @Name WorldTag.uuid
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the world.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((WorldTag) obj).world.getUniqueId().toString()));
    }

    public static WorldTag getFor(Action<String> error, String text) {
        Optional<World> optWorld = Sponge.getServer().getWorld(text);
        if (!optWorld.isPresent()) {
            error.run("Invalid WorldTag input!");
            return null;
        }
        return new WorldTag(optWorld.get());
    }

    public static WorldTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof WorldTag) ? (WorldTag) text : getFor(error, text.toString());
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
        return world.getName();
    }
}
