package com.bigdata.analystic.mr;

import com.bigdata.analystic.model.StatsBaseDimension;
import com.bigdata.analystic.model.value.StatsOutputValue;
import com.bigdata.analystic.mr.service.IDimension;
import org.apache.hadoop.conf.Configuration;

import java.sql.PreparedStatement;

/**
 * @ClassName: IOutputWriter
 * @Description: TODO 操作结果表的接口
 * @Author: xqg
 * @Date: 2018/11/5 14:32
 */
public interface IOutputWriter {
    /**
     * 这里通过key和value给ps语句赋值
     *
     * @param conf
     * @param key
     * @param value
     * @param ps
     * @param iDimension
     */
    void output(Configuration conf,
                StatsBaseDimension key,
                StatsOutputValue value,
                PreparedStatement ps,
                IDimension iDimension);
}
