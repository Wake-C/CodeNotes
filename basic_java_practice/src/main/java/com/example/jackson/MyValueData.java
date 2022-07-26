package com.example.jackson;

import lombok.Data;

@Data
public class MyValueData {
    private MyValue myValue;
    private String  name;
    private Boolean verified;
    private int[]   marks;
}
