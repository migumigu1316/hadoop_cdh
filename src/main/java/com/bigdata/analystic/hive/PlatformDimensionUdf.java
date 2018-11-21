package com.bigdata.analystic.hive;

import com.bigdata.analystic.model.base.PlatformDimension;
import com.bigdata.analystic.mr.service.IDimension;
import com.bigdata.analystic.mr.service.implement.IDimensionImpl;
import com.bigdata.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @ClassName: EventDimensionUdf
 * @Description: TODO 获取事件维度的ID
 * @Author: xqg
 * @Date: 2018/11/17 0:18
 */
public class PlatformDimensionUdf extends UDF {
    IDimension iDimension = new IDimensionImpl();

    /**
     *
     * @param platform
     * @return 事件维度的ID
     */
    public int evaluate(String platform) {
        if (StringUtils.isEmpty(platform)) {
            platform = GlobalConstants.DEFAULT_VALUE;
        }

        int id = -1;
        try {
            PlatformDimension pl = new PlatformDimension(platform);
            id = iDimension.getDimensionIdByObject(pl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    public static void main(String[] args) {
        System.out.println(new PlatformDimensionUdf().evaluate("website"));
    }

}
