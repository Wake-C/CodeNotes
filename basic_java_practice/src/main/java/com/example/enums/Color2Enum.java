package com.example.enums;

public enum Color2Enum {
    RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);

    //颜色
    private String name;

    private int code;


    Color2Enum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(int index) {
        for (Color2Enum c : Color2Enum.values()) {
            if (c.getCode() == index) {
                return c.name;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}