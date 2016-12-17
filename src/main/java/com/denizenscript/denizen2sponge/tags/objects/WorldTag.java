package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;

public class WorldTag extends AbstractTagObject {

    // <--[object]
    // @Type WorldTag
    // @SubType TextTag
    // @Group Areas
    // @Description Represents a world on the server. Identified by name.
    // -->

    private World internal;

    public WorldTag(World world) {
        internal = world;
    }

    public World getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name WorldTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the world.
        // @Example "world" .name returns "world".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((WorldTag) obj).internal.getName()));
        // <--[tag]
        // @Name WorldTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the world.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((WorldTag) obj).internal.getUniqueId().toString()));
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
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return internal.getName();
    }
}
