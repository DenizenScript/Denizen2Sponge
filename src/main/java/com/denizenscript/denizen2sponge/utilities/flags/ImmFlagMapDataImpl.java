package com.denizenscript.denizen2sponge.utilities.flags;

import com.denizenscript.denizen2core.tags.objects.MapTag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmFlagMapDataImpl extends AbstractImmutableSingleData<FlagMap, ImmFlagMapDataImpl, FlagMapDataImpl> implements ImmutableDataManipulator<ImmFlagMapDataImpl, FlagMapDataImpl> {

    public ImmFlagMapDataImpl(FlagMap value, Key<? extends BaseValue<FlagMap>> usedKey) {
        super(value, usedKey);
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(FlagHelper.FLAGMAP, this.getValue(), new FlagMap(new MapTag())).asImmutable();
    }

    @Override
    public FlagMapDataImpl asMutable() {
        return new FlagMapDataImpl(this.getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        container.set(FlagHelper.FLAGMAP.getQuery(), getValue());
        return container;
    }
}
