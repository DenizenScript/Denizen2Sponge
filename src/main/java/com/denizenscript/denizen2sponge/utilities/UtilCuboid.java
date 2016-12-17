package com.denizenscript.denizen2sponge.utilities;

public class UtilCuboid {

    public UtilLocation min = new UtilLocation();

    public UtilLocation max = new UtilLocation();

    public UtilCuboid(UtilLocation a, UtilLocation b) {
        if (!a.world.getName().equals(b.world.getName())) {
            throw new RuntimeException("A.world != B.world for UtilCuboid!");
        }
        min.x = Math.min(a.x, b.x);
        min.y = Math.min(a.y, b.y);
        min.z = Math.min(a.z, b.z);
        min.world = a.world;
        max.x = Math.max(a.x, b.x);
        max.y = Math.max(a.y, b.y);
        max.z = Math.max(a.z, b.z);
        max.world = b.world;
    }
}
