package com.denizenscript.denizen2sponge.utilities.flags;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

public class FlagHelper {

    public static final TypeToken<FlagMap> FLAGMAP_TOKEN = new TypeToken<FlagMap>() {
    };

    public static final TypeToken<Value<FlagMap>> FLAGMAP_VALUE_TOKEN = new TypeToken<Value<FlagMap>>() {
    };

    public static Key<Value<FlagMap>> FLAGMAP = KeyFactory.makeSingleKey(
            FLAGMAP_TOKEN,
            FLAGMAP_VALUE_TOKEN,
            DataQuery.of("denizen:flagmap"),
            "denizen:flagmap",
            "denizen:flagmap"
    );

    public static FlagMapDataBuilder FMDB;

    public static void register() {
        FMDB = new FlagMapDataBuilder(FlagMapData.class, 1);
        Sponge.getDataManager().register(FlagMapDataImpl.class, ImmFlagMapDataImpl.class, FMDB);
    }
}
