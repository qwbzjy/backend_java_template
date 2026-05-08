package com.mall.common.enums;

public class YesNoEnum {

    public static final int YES = 1;
    public static final int NO = 0;

    public static boolean isYes(Integer value) {
        return value != null && value == YES;
    }

    public static boolean isNo(Integer value) {
        return value != null && value == NO;
    }
}