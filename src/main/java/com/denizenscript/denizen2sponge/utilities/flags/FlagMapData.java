package com.denizenscript.denizen2sponge.utilities.flags;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.Value;

public interface FlagMapData extends DataManipulator<FlagMapData, ImmutableFlagMapData> {

    Value<FlagMap> defaultFlagMap();

    FlagMap getValue();
}
