package com.example.java8.defaultmethod;

public class defaultTest implements Animal, Person {

    @Override
    public void getName() {
        Person.super.getName();
        Animal.super.getName();
    }
}
