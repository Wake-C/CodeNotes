package com.example.java8.defaultmethod;

public interface Person {
    default void getName() {
        System.out.println(" where are you fatehr ");
    }
}
