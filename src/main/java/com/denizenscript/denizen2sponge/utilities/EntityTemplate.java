package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;

import java.util.Map;

public class EntityTemplate {

    public EntityType type;

    public MapTag properties;

    public EntityTemplate(EntityType entType) {
        type = entType;
        properties = new MapTag();
    }

    public EntityTemplate(EntityTemplate base) {
        type = base.type;
        properties = new MapTag(base.properties.getInternal());
    }

    public void addProperties(MapTag prop) {
        EntityArchetype arch = EntityArchetype.of(type);
        for (Map.Entry<String, AbstractTagObject> entry : prop.getInternal().entrySet()) {
            Key k = DataKeys.getKeyForName(entry.getKey());
            if (k == null) {
                throw new ErrorInducedException("Key '" + entry.getKey() + "' does not seem to exist.");
            }
            if (!arch.supports(k)) {
                throw new ErrorInducedException("Entity type '" + Utilities.getIdWithoutDefaultPrefix(type.getId())
                        + "' does not support key '" + entry.getKey() + "'.");
            }
            properties.getInternal().put(entry.getKey(), entry.getValue());
        }
    }

    public EntityTemplate copy() {
        return new EntityTemplate(this);
    }
}
