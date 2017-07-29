package com.nao20010128nao.Wisecraft.misc;

import com.annimon.stream.function.Predicate;

import java.util.*;
import java.util.concurrent.atomic.*;

public class IteratorUtils {
    public static <T> Predicate<T> booleanArrayToPredicate(boolean[] values){
        AtomicInteger pos=new AtomicInteger(0);
        return v->values[pos.getAndIncrement()];
    }
    public static <T> Predicate<T> booleanListToPredicate(List<Boolean> values){
        AtomicInteger pos=new AtomicInteger(0);
        return v->values.get(pos.getAndIncrement());
    }
}
