package com.denizenscript.denizen2sponge.utilities;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

public class UtilLocation {

    public double x;
    public double y;
    public double z;
    public World world;

    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }

    public Vector3i toVector3i() {
        return new Vector3i(x, y, z);
    }
}
