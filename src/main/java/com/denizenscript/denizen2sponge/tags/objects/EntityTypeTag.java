package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;
import java.util.Optional;

public class EntityTypeTag extends AbstractTagObject {

    // <--[object]
    // @Type EntityTypeTag
    // @SubType TextTag
    // @Group Items
    // @Description Represents an entity type. Identified by entity type ID.
    // -->

    // <--[explanation]
    // @Name Entity Types
    // @Group Useful Lists
    // @Description
    // A list of all default entity types can be found here:
    // <@link url https://jd.spongepowered.org/6.0.0-SNAPSHOT/org/spongepowered/api/entity/EntityTypes.html>entity types list<@/link>
    // These can be used with the spawn command as well as with some event switches.
    // -->

    private EntityType internal;

    public EntityTypeTag(EntityType internal) {
        this.internal = internal;
    }

    public EntityType getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name EntityTypeTag.id
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the ID of the entity type.
        // @Example "minecraft:creeper" .id returns "minecraft:creeper".
        // -->
        handlers.put("id", (dat, obj) -> new TextTag(((EntityTypeTag) obj).internal.getId()));
        // <--[tag]
        // @Name EntityTypeTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the entity type.
        // @Example "minecraft:creeper" .name returns "creeper".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(CoreUtilities.after(((EntityTypeTag) obj).internal.getName(), ":")));
    }

    public static EntityTypeTag getFor(Action<String> error, String text) {
        Optional<EntityType> optItemType = Sponge.getRegistry().getType(EntityType.class, text);
        if (!optItemType.isPresent()) {
            error.run("Invalid EntityTypeTag input!");
            return null;
        }
        return new EntityTypeTag(optItemType.get());
    }

    public static EntityTypeTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof EntityTypeTag) ? (EntityTypeTag) text : getFor(error, text.toString());
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
    public String getTagTypeName() {
        return "EntityTypeTag";
    }

    @Override
    public String toString() {
        return internal.getId();
    }
}
