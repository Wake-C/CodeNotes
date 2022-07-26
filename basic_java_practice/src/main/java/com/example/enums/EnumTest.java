package com.example.enums;

import java.util.EnumSet;

/**
 * @author Alexis
 * @date 2019年08月14日
 */
public class EnumTest {
    public static void main(String[] args) {
        ColorEnum color = ColorEnum.RED;
        switch (color) {
            case BLUE:
                System.out.println("蓝色");
                break;
            case RED:
                System.out.println("红色");
                break;
            default:
                System.out.println("黑色");
        }
        System.out.println(Color2Enum.getName(1));

        //获取范围enum    EnumSet.range
        for (WeekEnum weekEnum : EnumSet.range(WeekEnum.Wednesday, WeekEnum.Saturday)) {
            System.out.println(weekEnum.getName());
        }

        //获取指定enum  enumSet.of
        for (WeekEnum weekEnum : EnumSet.of(WeekEnum.Tuesday, WeekEnum.Friday)) {
            System.out.println(weekEnum.getName());
        }
    }
}
