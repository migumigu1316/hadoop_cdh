package com.bigdata.analystic.model.base;

import java.sql.PreparedStatement;

/**
 * @ClassName: BaseActionDimension
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/5 10:13
 */
public abstract class BaseActionDimension {
    protected static BaseDimension dimension;

    public abstract  String buildCacheKey();

    public abstract String buildSqls();

    public abstract void setArgs(PreparedStatement ps);
}

