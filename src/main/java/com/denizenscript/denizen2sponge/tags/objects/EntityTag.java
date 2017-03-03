package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractTagObject {

    // <--[object]
    // @Type EntityTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an entity on the server. Identified by UUID.
    // -->

    private Entity internal;

    public EntityTag(Entity ent) {
        internal = ent;
    }

    public Entity getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    public String friendlyName() {
        if (internal instanceof Player) {
            return ((Player) internal).getName() + "/" + internal.getUniqueId();
        }
        else {
            return internal.getType().getId() + "/" + internal.getUniqueId();
        }
    }

    static {
        // <--[tag]
        // @Name EntityTag.entity_type
        // @Updated 2017/02/07
        // @Group Identification
        // @ReturnType EntiyTypeTag
        // @Returns the type of this entity.
        // -->
        handlers.put("entity_type", (dat, obj) -> new EntityTypeTag(((EntityTag) obj).internal.getType()));
        // <--[tag]
        // @Name EntityTag.friendly_name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the "friendly name" of the entity, for debug output.
        // -->
        handlers.put("friendly_name", (dat, obj) -> new TextTag(((EntityTag) obj).friendlyName()));
        // <--[tag]
        // @Name EntityTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the entity.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((EntityTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Name EntityTag.location
        // @Updated 2016/08/26
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the location of the entity.
        // -->
        handlers.put("location", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getLocation()));
        // <--[tag]
        // @Name EntityTag.rotation
        // @Updated 2017/02/09
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the rotation of the entity, as a rotation vector.
        // -->
        handlers.put("rotation", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getRotation()));
        // <--[tag]
        // @Name EntityTag.velocity
        // @Updated 2016/08/26
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the velocity of the entity (The vector it is is currently moving in).
        // -->
        handlers.put("velocity", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getVelocity()));
        // <--[tag]
        // @Name EntityTag.data
        // @Updated 2016/08/28
        // @Group General Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((EntityTag) obj).internal));
        // <--[tag]
        // @Name EntityTag.get[<TextTag>]
        // @Updated 2016/08/28
        // @Group General Information
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the entity.
        // -->
        handlers.put("get", (dat, obj) -> {
            String keyName = dat.getNextModifier().toString();
            Key key = DataKeys.getKeyForName(keyName);
            if (key == null) {
                dat.error.run("Invalid key '" + keyName + "'!");
                return new NullTag();
            }
            return DataKeys.getValue(((EntityTag) obj).internal, key, dat.error);
        });
        // <--[tag]
        // @Name EntityTag.has_flag[<TextTag>]
        // @Updated 2016/10/26
        // @Group Flag Data
        // @ReturnType BooleanTag
        // @Returns whether the entity has a flag with the specified key.
        // -->
        handlers.put("has_flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            return new BooleanTag(flags.getInternal().containsKey(flagName));
        });
        // <--[tag]
        // @Name EntityTag.flag[<TextTag>]
        // @Updated 2016/10/26
        // @Group Flag Data
        // @ReturnType Dynamic
        // @Returns the flag of the specified key from the entity. May become TextTag regardless of input original type.
        // -->
        handlers.put("flag", (dat, obj) -> {
            String flagName = CoreUtilities.toLowerCase(dat.getNextModifier().toString());
            MapTag flags;
            Entity e = ((EntityTag) obj).internal;
            Optional<FlagMap> fm = e.get(FlagHelper.FLAGMAP);
            if (fm.isPresent()) {
                flags = fm.get().flags;
            }
            else {
                flags = new MapTag();
            }
            AbstractTagObject ato = flags.getInternal().get(flagName);
            if (ato == null) {
                dat.error.run("Invalid flag specified, not present on this entity!");
                return new NullTag();
            }
            return ato;
        });
        // <--[tag]
        // @Name EntityTag.eye_height
        // @Updated 2017/02/28
        // @Group General Information
        // @ReturnType NumberTag
        // @Returns the eye height of the entity.
        // -->
        handlers.put("eye_height", (dat, obj) -> {
            Entity entity = ((EntityTag) obj).internal;
            Double height = entity.getProperty(EyeHeightProperty.class).orElse(null).getValue();
            return new NumberTag(height);
        });
        // <--[tag]
        // @Name EntityTag.eye_location
        // @Updated 2017/02/28
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the eye location of the entity.
        // -->
        handlers.put("eye_location", (dat, obj) -> {
            Entity entity = ((EntityTag) obj).internal;
            Location<World> location = new Location<>(entity.getWorld(), entity.getProperty(EyeLocationProperty.class).orElse(null).getValue());
            return new LocationTag(location);
        });
        // <--[tag]
        // @Name EntityTag.health
        // @Updated 2017/02/28
        // @Group Entity Properties
        // @ReturnType NumberTag
        // @Returns the current health of the entity.
        // -->
        handlers.put("health", (dat, obj) -> {
            Living entity = (Living) ((EntityTag) obj).internal;
            Double health = entity.health().get();
            return new NumberTag(health);
        });
        // <--[tag]
        // @Name EntityTag.max_health
        // @Updated 2017/02/28
        // @Group Entity Properties
        // @ReturnType NumberTag
        // @Returns the maximum health of the entity.
        // -->
        handlers.put("max_health", (dat, obj) -> {
            Living entity = (Living) ((EntityTag) obj).internal;
            Double maxHealth = entity.maxHealth().get();
            return new NumberTag(maxHealth);
        });
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
        return new TextTag(toString());
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }
}
