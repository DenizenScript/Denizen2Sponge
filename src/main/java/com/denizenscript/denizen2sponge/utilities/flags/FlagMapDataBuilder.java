package com.denizenscript.denizen2sponge.utilities.flags;

import com.denizenscript.denizen2core.Denizen2Core;
import com.denizenscript.denizen2core.tags.objects.MapTag;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class FlagMapDataBuilder extends AbstractDataBuilder<FlagMapData> implements DataManipulatorBuilder<FlagMapData, ImmutableFlagMapData> {

    protected FlagMapDataBuilder(Class<FlagMapData> requiredClass, int supportedVersion) {
        super(requiredClass, supportedVersion);
    }
    @Override
    public FlagMapData create() {
        return new FlagMapDataImpl();
    }

    @Override
    public Optional<FlagMapData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<FlagMapData> buildContent(DataView container) throws InvalidDataException {
        Integer version = (Integer) container.get(Queries.CONTENT_VERSION).get();
        if (version != 1) {
            return Optional.empty();
        }
        Optional<DataView> dv = container.getView(FlagHelper.FLAGMAP.getQuery());
        if (!dv.isPresent()) {
            return Optional.empty();
        }
        Optional<String> str = dv.get().getString(FlagMap.FLAG);
        if (!str.isPresent()) {
            return Optional.empty();
        }
        String val = str.get();
        MapTag mt = (MapTag) Denizen2Core.loadFromSaved((e) -> { throw new InvalidDataException("Denizen2: " + e); }, val);
        return Optional.of(new FlagMapDataImpl(new FlagMap(mt)));
    }
}
