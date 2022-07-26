package com.example.java8.defaultmethod;

public interface Animal {
    default void getName() {
        System.out.println("i m animal");
    }
}
