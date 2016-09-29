package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public class EntityKeys {

    private static Collection<Key> keys;

    public static void updateKeys() {
        // TODO: Prevent calling this several times a tick?
        keys = Sponge.getRegistry().getAllOf(Key.class);
    }

    public static Key getKeyForName(String name) {
        name = CoreUtilities.toLowerCase(name);
        for (Key key : keys) {
            if (name.equals(CoreUtilities.after(key.getId(), ":"))
                    || name.equals(key.getId())) {
                return key;
            }
        }
        return null;
    }


    public static AbstractTagObject getValue(Entity entity, Key key, Action<String> error) {
        if (!entity.supports(key)) {
            error.run("The entity type '" + entity.getType().getName()
                    + "' does not support the property '" + key.getId() + "'!");
            return new NullTag();
        }
        Class clazz = key.getElementToken().getRawType();
        if (Boolean.class.isAssignableFrom(clazz)) {
            return new BooleanTag(entity.getOrElse((Key<BaseValue<Boolean>>) key, false));
        }
        else if (CatalogType.class.isAssignableFrom(clazz)) {
            return new TextTag(entity.getValue((Key<BaseValue<CatalogType>>) key).orElseThrow(() -> new ErrorInducedException("Value not present!")).get().toString());
        }
        else if (Double.class.isAssignableFrom(clazz)) {
            return new NumberTag(entity.getOrElse((Key<BaseValue<Double>>) key, 0.0));
        }
        else if (Enum.class.isAssignableFrom(clazz)) {
            return new TextTag(entity.getValue((Key<BaseValue<Enum>>) key).orElseThrow(() -> new ErrorInducedException("Empty enum value!")).get().name());
        }
        else if (Integer.class.isAssignableFrom(clazz)) {
            return new IntegerTag(entity.getOrElse((Key<BaseValue<Integer>>) key, 0));
        }
        else if (Vector3d.class.isAssignableFrom(clazz)) {
            return new LocationTag(entity.getOrElse((Key<BaseValue<Vector3d>>) key, new Vector3d(0, 0, 0)));
        }
        else if (Text.class.isAssignableFrom(clazz)) {
            return new FormattedTextTag(entity.getOrElse((Key<BaseValue<Text>>) key, Text.EMPTY));
        }
        else {
            error.run("The value type '" + clazz.getName() + "' is not supported yet!");
            return new NullTag();
        }
    }

    public static void tryApply(Entity entity, Key key, AbstractTagObject value, Action<String> error) {
        if (!entity.supports(key)) {
            error.run("The entity type '" + entity.getType().getName()
                    + "' does not support the property '" + key.getId() + "'!");
            return;
        }
        Class clazz = key.getElementToken().getRawType();
        if (Boolean.class.isAssignableFrom(clazz)) {
            entity.offer(key, BooleanTag.getFor(error, value).getInternal());
        }
        else if (CatalogType.class.isAssignableFrom(clazz)) {
            String val = value.toString();
            Optional optCatalogType = Sponge.getRegistry().getType(clazz, val);
            if (!optCatalogType.isPresent()) {
                error.run("Invalid value '" + val + "' for property '" + key.getId() + "'!");
                return;
            }
            entity.offer(key, optCatalogType.get());
        }
        else if (Double.class.isAssignableFrom(clazz)) {
            entity.offer(key, NumberTag.getFor(error, value).getInternal());
        }
        else if (Enum.class.isAssignableFrom(clazz)) {
            entity.offer(key, Enum.valueOf(clazz, value.toString().toUpperCase()));
        }
        else if (Integer.class.isAssignableFrom(clazz)) {
            entity.offer(key, (int) IntegerTag.getFor(error, value).getInternal());
        }
        else if (Vector3d.class.isAssignableFrom(clazz)) {
            entity.offer(key, LocationTag.getFor(error, value).getInternal().toVector3d());
        }
        else if (Text.class.isAssignableFrom(clazz)) {
            entity.offer(key, FormattedTextTag.getFor(error, value).getInternal());
        }
        else {
            error.run("The value type '" + clazz.getName() + "' is not supported yet!");
        }
    }
}
