package com.bigdata.analystic.mr.service;

import com.bigdata.analystic.model.base.BaseDimension;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 根据维度获取对应的Id的接口
 * @ClassName: IDimension
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/4 11:03
 */
public interface IDimension {
    int getDimensionIdByObject(BaseDimension dimension) throws IOException, SQLException;
}