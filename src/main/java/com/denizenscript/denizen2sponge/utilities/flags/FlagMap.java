package com.denizenscript.denizen2sponge.utilities.flags;

import com.denizenscript.denizen2core.tags.objects.MapTag;
import org.spongepowered.api.data.*;

public class FlagMap implements DataSerializable {

    public static final DataQuery FLAG = DataQuery.of("denizen_flag");

    public MapTag flags = new MapTag();

    public FlagMap(MapTag mt) {
        flags = mt;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        MemoryDataContainer mdc = new MemoryDataContainer();
        mdc.set(FLAG, flags.toString());
        return mdc;
    }
}
