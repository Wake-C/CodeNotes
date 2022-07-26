package com.example.enums;

public enum WeekEnum {
    Monday(1, "星期一"),
    Tuesday(2, "星期二"),
    Wednesday(3, "星期三"),
    Thursday(4, "星期四"),
    Friday(5, "星期五"),
    Saturday(6, "星期六"),
    Sunday(7, "星期天");

    private Integer code;
    private String  name;

    WeekEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Integer getCode(String name) {
        for (WeekEnum week : WeekEnum.values()) {
            if (week.getName().equals(name)) {
                return week.getCode();
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (WeekEnum week : WeekEnum.values()) {
            if (week.getCode().equals(code)) {
                return week.getName();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
