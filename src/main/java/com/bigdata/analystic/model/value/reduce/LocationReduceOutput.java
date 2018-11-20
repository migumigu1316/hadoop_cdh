package com.bigdata.analystic.model.value.reduce;

import com.bigdata.common.KpiType;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * @ClassName: LocationReduceOutput
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/16 14:17
 */
public class LocationReduceOutput extends OutputWritable{
    private KpiType kpi;
    private int aus;
    private int session;
    private int bounce_session;

    public KpiType getKpi() {
        return kpi;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        WritableUtils.writeEnum(dataOutput,kpi);
        dataOutput.writeInt(this.aus);
        dataOutput.writeInt(this.session);
        dataOutput.writeInt(this.bounce_session);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        WritableUtils.readEnum(dataInput,KpiType.class);
        this.aus = dataInput.readInt();
        this.session = dataInput.readInt();
        this.bounce_session = dataInput.readInt();
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    public int getAus() {
        return aus;
    }

    public void setAus(int aus) {
        this.aus = aus;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getBounce_session() {
        return bounce_session;
    }

    public void setBounce_session(int bounce_session) {
        this.bounce_session = bounce_session;
    }
}