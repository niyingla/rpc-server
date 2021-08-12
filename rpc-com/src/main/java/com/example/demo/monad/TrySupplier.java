package com.example.demo.monad;

public interface TrySupplier<T>{
    T get() throws Throwable;
}
