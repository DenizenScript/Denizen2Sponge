package com.denizenscript.denizen2sponge.utilities;

@FunctionalInterface
public interface Function3<A, B, C, R> {
    R apply(A a, B b, C c);
}
