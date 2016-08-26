package org.mcmonkey.denizen2sponge.utilities;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.World;

public class UtilLocation {

    public double x;
    public double y;
    public double z;
    public World world;

    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }
}
