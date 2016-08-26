package org.mcmonkey.denizen2sponge.tags.objects;

import org.mcmonkey.denizen2core.tags.AbstractTagObject;
import org.mcmonkey.denizen2core.tags.TagData;
import org.mcmonkey.denizen2core.tags.objects.TextTag;
import org.mcmonkey.denizen2core.utilities.Action;
import org.mcmonkey.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractTagObject {

    // <--[object]
    // @Type EntityTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an entity on the server.
    // -->

    private Entity internal;

    public EntityTag(Entity player) {
        internal = player;
    }

    public Entity getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name EntityTag.uuid
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the entity.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((EntityTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Name EntityTag.location
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the location of the entity.
        // -->
        handlers.put("location", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getLocation()));
        // <--[tag]
        // @Name EntityTag.velocity
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the velocity of the entity (The vector it is is currently moving in.)
        // -->
        handlers.put("velocity", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getVelocity()));
    }

    public static EntityTag getFor(Action<String> error, String text) {
        UUID id = UUID.fromString(text);
        for (World world : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = world.getEntity(id);
            if (e.isPresent()) {
                return new EntityTag(e.get());
            }
        }
        error.run("Invalid EntityTag UUID input!");
        return null;
    }

    public static EntityTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof EntityTag) ? (EntityTag) text : getFor(error, text.toString());
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
        return internal.getUniqueId().toString();
    }
}
