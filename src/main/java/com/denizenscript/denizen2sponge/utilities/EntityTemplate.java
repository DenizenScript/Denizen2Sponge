package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;

public class EntityTemplate {

    public EntityType type;

    public HashMap<String, AbstractTagObject> properties;

    public HashMap<String, HashMap<String, AbstractTagObject>> tasks;

    public EntityTemplate(EntityType entType) {
        type = entType;
        properties = new HashMap<>();
        tasks = new HashMap<>();
    }

    public EntityTemplate(EntityTemplate base) {
        type = base.type;
        properties = new HashMap<>(base.properties);
        tasks = new HashMap<>(base.tasks);
    }
}
