package com.nao20010128nao.Wisecraft.misc;

public interface OneArgThrowableFunction<R,A>{
    R call(A a) throws Throwable;
}
