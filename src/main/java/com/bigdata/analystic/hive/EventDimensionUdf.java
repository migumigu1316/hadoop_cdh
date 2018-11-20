package com.bigdata.analystic.hive;

import com.bigdata.analystic.model.base.EventDimension;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.analystic.mr.service.implement.IDimensionImpl;
import com.bigdata.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @ClassName: EventDimensionUdf
 * @Description: TODO 获取事件维度的ID
 * @Author: xqg
 * @Date: 2018/11/17 0:00
 */
public class EventDimensionUdf extends UDF {
    IDimension iDimension = new IDimensionImpl();

    /**
     * @param category
     * @param action
     * @return 获取事件维度的ID
     */
    public int evaluate(String category, String action) {
        if (StringUtils.isEmpty(category)) {
            category = action = GlobalConstants.DEFAULT_VALUE;
        }

        if (StringUtils.isEmpty(action)) {
            action = GlobalConstants.DEFAULT_VALUE;
        }
        int id = -1;
        try {
            EventDimension ed = new EventDimension(category, action);
            id = iDimension.getDimensionIdByObject(ed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }
}
