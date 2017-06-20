package com.nao20010128nao.Wisecraft.misc;

import java.util.*;

public class SextetWalker<A, B, C, D, E, F> extends ArrayList<Sextet<A, B, C, D, E, F>> {
    public Sextet<A, B, C, D, E, F> findByA(A o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getA())) return i;
        return null;
    }

    public Sextet<A, B, C, D, E, F> findByB(B o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getB())) return i;
        return null;
    }

    public Sextet<A, B, C, D, E, F> findByC(C o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getC())) return i;
        return null;
    }

    public Sextet<A, B, C, D, E, F> findByD(D o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getD())) return i;
        return null;
    }

    public Sextet<A, B, C, D, E, F> findByE(E o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getE())) return i;
        return null;
    }

    public Sextet<A, B, C, D, E, F> findByF(F o) {
        for (Sextet<A, B, C, D, E, F> i : this) if (CompatUtils.equals(o, i.getF())) return i;
        return null;
    }
}
