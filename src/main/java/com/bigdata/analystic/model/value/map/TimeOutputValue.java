package com.bigdata.analystic.model.value.map;

import com.bigdata.analystic.model.value.StatsOutputValue;
import com.bigdata.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @ClassName: TimeOutputValue
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/3 22:06
 */
public class TimeOutputValue extends StatsOutputValue {
    //对id的泛指,可以是uuid，可以是umid，还可以是sessionId
    private String id;
    //时间戳
    private long time;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(id);
        dataOutput.writeLong(time);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        id = dataInput.readUTF();
        time = dataInput.readLong();
    }

    @Override
    public KpiType getKpi() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
