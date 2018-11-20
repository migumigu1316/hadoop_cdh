package com.hadoopTest;

import com.bigdata.analystic.model.base.DateDimension;
import com.bigdata.analystic.model.base.LocationDimension;
import com.bigdata.analystic.model.base.PlatformDimension;
import com.bigdata.common.DateEnum;

public class DimensionTest {
    public static void main(String[] args) {
        LocationDimension locationDimension = new LocationDimension("中国", "浙江", "温州");
        PlatformDimension platformDimension = new PlatformDimension("website");
        DateDimension dt = DateDimension.buildDate(1540656000000L, DateEnum.DAY);
    }
}
