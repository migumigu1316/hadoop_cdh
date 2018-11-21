package com.bigdata.analystic.model.value;

import com.bigdata.common.KpiType;
import org.apache.hadoop.io.Writable;

/**
 * @ClassName: StatsOutputValue
 * @Description: TODO 封装map或者reduce阶段输出的value类型的顶级父类
 * @Author: xqg
 * @Date: 2018/11/3 22:08
 */
public abstract class StatsOutputValue implements Writable {
    //获取kpi的抽象方法
    public abstract KpiType getKpi();

}
