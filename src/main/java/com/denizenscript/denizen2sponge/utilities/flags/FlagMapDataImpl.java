package com.denizenscript.denizen2sponge.utilities.flags;

import com.denizenscript.denizen2core.tags.objects.MapTag;
import com.denizenscript.denizen2core.utilities.debugging.Debug;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class FlagMapDataImpl extends AbstractSingleData<FlagMap, FlagMapData, ImmutableFlagMapData> implements FlagMapData {

    public FlagMapDataImpl(FlagMap value) {
        super(value, FlagHelper.FLAGMAP);
    }

    public FlagMapDataImpl() {
        this(new FlagMap(new MapTag()));
    }

    public Value<FlagMap> defaultFlagMap() {
        return getValueGetter();
    }

    @Override
    public FlagMap getValue() {
        return super.getValue();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    protected Value<FlagMap> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(FlagHelper.FLAGMAP, getValue(), new FlagMap(new MapTag()));
    }

    @Override
    public ImmutableFlagMapData asImmutable() {
        return new ImmFlagMapDataImpl(getValue(), FlagHelper.FLAGMAP);
    }

    @Override
    public Optional<FlagMapData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Debug.info("FlagMapDataImpl -> fill");
        FlagMapData merged = overlap.merge(this, dataHolder.get(FlagMapData.class).orElse(null));
        setValue(merged.getValue());
        return Optional.of(this);
    }

    @Override
    public Optional<FlagMapData> from(DataContainer container) {
        Debug.info("FlagMapDataImpl -> from");
        if(container.contains(FlagHelper.FLAGMAP)) {
            // Loads the structure defined in toContainer
            setValue(container.getSerializable(FlagHelper.FLAGMAP.getQuery(), FlagMap.class).get());
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public FlagMapData copy() {
        return new FlagMapDataImpl(getValue());
    }

    @Override
    public DataContainer toContainer() {
        Debug.info("FlagMapDataImpl -> toContainer");
        DataContainer container = super.toContainer();
        container.set(FlagHelper.FLAGMAP.getQuery(), getValue());
        return container;
    }
}
