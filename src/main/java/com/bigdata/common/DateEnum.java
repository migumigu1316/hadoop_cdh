package com.bigdata.common;

/**
 * @ClassName: DateEnum
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/3 17:48
 */
public enum  DateEnum {
    YEAR("year"),
    SEASON("season"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour")
    ;

    public String dateType;

    DateEnum(String dateType) {
        this.dateType = dateType;
    }

    public static DateEnum valueOfDateType(String type){
        for (DateEnum date:values()) {
            if(date.dateType.equals(type)){
                return date;
            }
        }
        return null;
    }
}
