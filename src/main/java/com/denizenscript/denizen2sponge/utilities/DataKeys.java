package com.denizenscript.denizen2sponge.utilities;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.objects.*;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.CoreUtilities;
import com.denizenscript.denizen2core.utilities.ErrorInducedException;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import com.denizenscript.denizen2sponge.tags.objects.FormattedTextTag;
import com.denizenscript.denizen2sponge.tags.objects.LocationTag;
import com.denizenscript.denizen2sponge.utilities.flags.FlagHelper;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMap;
import com.denizenscript.denizen2sponge.utilities.flags.FlagMapDataImpl;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.text.Text;

import java.util.*;

public class DataKeys {

    private static Collection<Key> keys;
    private static int lastUpdate;

    private static void updateKeys() {
        int currentTime = Sponge.getServer().getRunningTimeTicks();
        if (currentTime != lastUpdate) {
            lastUpdate = currentTime;
            keys = new HashSet<>(Sponge.getRegistry().getAllOf(Key.class));
            keys.add(FlagHelper.FLAGMAP); // TODO: Make this not needed! GetAllOf shouldn't require manual key registration!
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

    public static MapTag getAllKeys(DataHolder dataHolder) {
        MapTag temp = new MapTag();
        updateKeys();
        for (Key key : keys) {
            if (!dataHolder.supports(key)) {
                continue;
            }
            if (dataHolder.getOrNull(key) == null) {
                // Nope nope nope!
                continue;
            }
            AbstractTagObject ato = getValue(dataHolder, key, (s) -> {
                Debug.error("Failed to read key '" + key.getId() + "': " + s);
            });
            if (ato != null && !(ato instanceof NullTag)) {
                temp.getInternal().put(key.getId(), ato);
            }
        }
        return temp;
    }

    public static Object convertObjectUsing(Action<String> error, TypeToken type, AbstractTagObject value) {
        if (List.class.isAssignableFrom(type.getRawType())) {
            TypeToken st = type.resolveType(List.class.getTypeParameters()[0]);
            ArrayList toRet = new ArrayList();
            for (AbstractTagObject obj : ListTag.getFor(error, value).getInternal()) {
                toRet.add(convertObjectUsing(error, st, obj));
            }
            return toRet;
        }
        else if (type.isSubtypeOf(Boolean.class)) {
            return BooleanTag.getFor(error, value).getInternal();
        }
        else if (type.isSubtypeOf(CatalogType.class)) {
            String val = value.toString();
            Optional optCatalogType = Sponge.getRegistry().getType(type.getRawType(), val);
            if (!optCatalogType.isPresent()) {
                error.run("Invalid value '" + val + "' requested for enumeration '" + type.getRawType().getCanonicalName() + "'!");
                return null;
            }
            return optCatalogType.get();
        }
        else if (type.isSubtypeOf(Double.class)) {
            return NumberTag.getFor(error, value).getInternal();
        }
        else if (type.isSubtypeOf(Enum.class)) {
            return Enum.valueOf(type.getRawType(), value.toString().toUpperCase());
        }
        else if (type.isSubtypeOf(Integer.class)) {
            return (int) IntegerTag.getFor(error, value).getInternal();
        }
        else if (type.isSubtypeOf(Vector3d.class)) {
            return LocationTag.getFor(error, value).getInternal().toVector3d();
        }
        else if (type.isSubtypeOf(Text.class)) {
            return FormattedTextTag.getFor(error, value).getInternal();
        }
        else if (type.isSubtypeOf(FlagMap.class)) {
            return new FlagMap(MapTag.getFor(error, value));
        }
        else {
            error.run("The value type '" + type.getRawType().getCanonicalName() + "' is not supported yet, cannot apply!");
            return null;
        }
    }

    public static AbstractTagObject taggifyObject(Action<String> error, Object input) {
        if (input instanceof List) {
            ListTag toRet = new ListTag();
            for (Object obj : (List) input) {
                toRet.getInternal().add(taggifyObject(error, obj));
            }
            return toRet;
        }
        if (input instanceof AbstractTagObject) {
            return (AbstractTagObject) input;
        }
        if (input instanceof Boolean) {
            return new BooleanTag((Boolean) input);
        }
        if (input instanceof CatalogType) {
            return new TextTag(input.toString());
        }
        if (input instanceof Double) {
            return new NumberTag((Double) input);
        }
        if (input instanceof Enum) {
            return new TextTag(input.toString());
        }
        if (input instanceof Integer) {
            return new IntegerTag((Integer) input);
        }
        if (input instanceof Vector3d) {
            return new LocationTag((Vector3d) input);
        }
        if (input instanceof Text) {
            return new FormattedTextTag((Text) input);
        }
        if (input instanceof FlagMap) {
            return new MapTag(((FlagMap) input).flags.getInternal());
        }
        error.run("The value type '" + input.getClass().getName() + "' is not supported yet, cannot taggify!");
        return new NullTag();
    }

    private static ArrayList EMPTY_LIST = new ArrayList();

    // TODO: Rotation support!

    public static AbstractTagObject getValue(DataHolder dataHolder, Key key, Action<String> error) {
        Class clazz = key.getElementToken().getRawType();
        if (!dataHolder.supports(key)) {
            if (FlagMap.class.isAssignableFrom(clazz)) {
                return new MapTag();
            }
            error.run("This data holder does not support the key '" + key.getId() + "'!");
            return new NullTag();
        }
        if (List.class.isAssignableFrom(clazz)) {
            return taggifyObject(error, dataHolder.getOrElse(key, EMPTY_LIST));
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return new BooleanTag(dataHolder.getOrElse((Key<BaseValue<Boolean>>) key, false));
        }
        else if (CatalogType.class.isAssignableFrom(clazz)) {
            return new TextTag(dataHolder.getValue((Key<BaseValue<CatalogType>>) key).orElseThrow(() -> new ErrorInducedException("Value not present!")).get().getId());
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
        else if (FlagMap.class.isAssignableFrom(clazz)) {
            return new MapTag(dataHolder.getOrElse((Key<BaseValue<FlagMap>>) key, new FlagMap(new MapTag())).flags.getInternal());
        }
        else {
            error.run("The value type '" + clazz.getName() + "' is not supported yet, cannot get its value!");
            return new NullTag();
        }
    }

    public static void tryApply(DataHolder entity, Key key, AbstractTagObject value, Action<String> error) {
        if (key == null) {
            error.run("The given key is null - an invalid key name may have been given.");
            return;
        }
        if (!entity.supports(key)) {
            if (key.getElementToken().isSubtypeOf(FlagMap.class)) {
                entity.offer(new FlagMapDataImpl(new FlagMap(new MapTag())));
            }
            else {
                error.run("This data holder does not support the key '" + key.getId() + "'!");
                return;
            }
        }
        Object offerMe = convertObjectUsing(error, key.getElementToken(), value);
        if (offerMe == null) {
            error.run("Failed to apply key with null value!");
            return;
        }
        if (offerMe instanceof FlagMap) {
            entity.offer(new FlagMapDataImpl((FlagMap) offerMe));
        }
        else {
            entity.offer(key, offerMe);
        }
    }

    public static ImmutableDataHolder with(ImmutableDataHolder entity, Key key, AbstractTagObject value, Action<String> error) {
        Class clazz = key.getElementToken().getRawType();
        if (!entity.supports(key)) {
            if (key.getElementToken().isSubtypeOf(FlagMap.class)) {
                entity = (ImmutableDataHolder) entity.with(new FlagMapDataImpl(new FlagMap(new MapTag()))).get();
            }
            else {
                error.run("This data holder does not support the key '" + key.getId() + "'!");
                return null;
            }
        }
        Object offerMe = convertObjectUsing(error, key.getElementToken(), value);
        if (offerMe == null) {
            error.run("Failed to apply key with null value!");
            return null;
        }
        if (offerMe instanceof FlagMap) {
            return (ImmutableDataHolder) entity.with(new FlagMapDataImpl((FlagMap) offerMe)).get();
        }
        else {
            return (ImmutableDataHolder) entity.with(key, offerMe).get();
        }
    }
}
