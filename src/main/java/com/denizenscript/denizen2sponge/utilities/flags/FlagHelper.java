package com.denizenscript.denizen2sponge.utilities.flags;

import com.denizenscript.denizen2sponge.Denizen2Sponge;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

public class FlagHelper {

    public static final TypeToken<Value<FlagMap>> FLAGMAP_VALUE_TOKEN = new TypeToken<Value<FlagMap>>() {
    };

    public static Key<Value<FlagMap>> FLAGMAP = Key.builder()
            .type(FLAGMAP_VALUE_TOKEN)
            .query(DataQuery.of("flagmap"))
            .id("flagmap")
            .name("Denizen2SpongeFlagMap")
            .build();

    public static FlagMapDataBuilder FMDB;

    public static void register() {
        FMDB = new FlagMapDataBuilder(FlagMapDataImpl.class, 1);
        DataRegistration.<FlagMapDataImpl, ImmFlagMapDataImpl>builder()
                .dataClass(FlagMapDataImpl.class)
                .immutableClass(ImmFlagMapDataImpl.class)
                .builder(FMDB)
                .manipulatorId("flagmap")
                .dataName("flagmap")
                .buildAndRegister(Denizen2Sponge.plugin);
    }
}
