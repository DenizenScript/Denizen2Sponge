package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.BooleanTag;
import com.denizenscript.denizen2core.tags.objects.IntegerTag;
import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2core.tags.objects.NumberTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public class DataKeys {

    private static Collection<Key> keys;
    private static int lastUpdate;

    private static void updateKeys() {
        int currentTime = Sponge.getServer().getRunningTimeTicks();
        if (currentTime != lastUpdate) {
            lastUpdate = currentTime;
            keys = Sponge.getRegistry().getAllOf(Key.class);
        }
    }

    public static Key getKeyForName(String name) {
        updateKeys();
        name = CoreUtilities.toLowerCase(name);
        for (Key key : keys) {
            if (name.equals(CoreUtilities.after(key.getId(), ":"))
                    || name.equals(key.getId())) {
                return key;
            }
        }
        return null;
    }

    public static AbstractTagObject getValue(DataHolder dataHolder, Key key, Action<String> error) {
        if (!dataHolder.supports(key)) {
            // TODO: improve this error maybe?
            error.run("This data holder does not support the key '" + key.getId() + "'!");
            return new NullTag();
        }
        Class clazz = key.getElementToken().getRawType();
        if (Boolean.class.isAssignableFrom(clazz)) {
            return new BooleanTag(dataHolder.getOrElse((Key<BaseValue<Boolean>>) key, false));
        }
        else if (CatalogType.class.isAssignableFrom(clazz)) {
            return new TextTag(dataHolder.getValue((Key<BaseValue<CatalogType>>) key).orElseThrow(() -> new ErrorInducedException("Value not present!")).get().toString());
        }
        else if (Double.class.isAssignableFrom(clazz)) {
            return new NumberTag(dataHolder.getOrElse((Key<BaseValue<Double>>) key, 0.0));
        }
        else if (Enum.class.isAssignableFrom(clazz)) {
            return new TextTag(dataHolder.getValue((Key<BaseValue<Enum>>) key).orElseThrow(() -> new ErrorInducedException("Empty enum value!")).get().name());
        }
        else if (Integer.class.isAssignableFrom(clazz)) {
            return new IntegerTag(dataHolder.getOrElse((Key<BaseValue<Integer>>) key, 0));
        }
        else if (Vector3d.class.isAssignableFrom(clazz)) {
            return new LocationTag(dataHolder.getOrElse((Key<BaseValue<Vector3d>>) key, new Vector3d(0, 0, 0)));
        }
        else if (Text.class.isAssignableFrom(clazz)) {
            return new FormattedTextTag(dataHolder.getOrElse((Key<BaseValue<Text>>) key, Text.EMPTY));
        }
        else {
            error.run("The value type '" + clazz.getName() + "' is not supported yet!");
            return new NullTag();
        }
    }

    public static void tryApply(DataHolder entity, Key key, AbstractTagObject value, Action<String> error) {
        if (!entity.supports(key)) {
            // TODO: improve this error maybe?
            error.run("This data holder does not support the key '" + key.getId() + "'!");
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
