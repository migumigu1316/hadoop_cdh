package com.bigdata.analystic.model.base;

import java.sql.PreparedStatement;

/**
 * @ClassName: BrowserActionDimension
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/5 10:14
 */
public class BrowserActionDimension extends BaseActionDimension {

    @Override
    public String buildCacheKey() {
        return null;
    }

    @Override
    public String buildSqls() {
        return null;
    }

    @Override
    public void setArgs(PreparedStatement ps) {

    }
}
