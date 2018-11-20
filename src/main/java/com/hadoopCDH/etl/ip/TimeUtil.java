package com.hadoopCDH.etl.ip;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public  static  String times(String tt){
        if (StringUtils.isEmpty(tt)) {

            return tt;
        }


        String[] splitDate = tt.split("\\.");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.format(new Date(Long.valueOf(splitDate[0]+splitDate[1])));
    } 
}
