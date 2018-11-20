package com.bigdata.analystic.model.value.map;

import com.bigdata.analystic.model.value.StatsOutputValue;
import com.bigdata.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @ClassName: LocationOutputValue
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/16 14:12
 */
public class LocationOutputValue extends StatsOutputValue {
    //对id的泛指,可以是uuid，可以是umid，还可以是sessionId
    private String uid;
    //时间戳
    private String sid;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.uid);
        dataOutput.writeUTF(this.sid);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        uid = dataInput.readUTF();
        sid = dataInput.readUTF();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public KpiType getKpi() {
        return null;
    }
}
