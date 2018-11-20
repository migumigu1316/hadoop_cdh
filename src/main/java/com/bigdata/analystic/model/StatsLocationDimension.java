package com.bigdata.analystic.model;

import com.bigdata.analystic.model.base.BaseDimension;
import com.bigdata.analystic.model.base.BrowserDimension;
import com.bigdata.analystic.model.base.LocationDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @ClassName: StatsLocationDimension
 * @Description: TODO
 * @Author: xqg
 * @Date: 2018/11/3 21:37
 *
 * 可以用于地域模块的map和reduce阶段的输出的key的类型
 */
public class StatsLocationDimension extends StatsBaseDimension{
    private LocationDimension locationDimension = new LocationDimension();
    private StatsCommonDimension statsCommonDimension = new StatsCommonDimension();

    public StatsLocationDimension() {
    }

    public StatsLocationDimension(LocationDimension locationDimension, StatsCommonDimension statsCommonDimension) {
        this.locationDimension = locationDimension;
        this.statsCommonDimension = statsCommonDimension;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.locationDimension.write(dataOutput);
        this.statsCommonDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.locationDimension.readFields(dataInput);
        this.statsCommonDimension.readFields(dataInput);
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (o == this){
            return 0;
        }
        StatsLocationDimension other = (StatsLocationDimension) o;
        int tmp = this.locationDimension.compareTo(other.locationDimension);
        if(tmp != 0){
            return  tmp;
        }
        return this.statsCommonDimension.compareTo(other.statsCommonDimension);
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsLocationDimension that = (StatsLocationDimension) o;
        return Objects.equals(locationDimension, that.locationDimension) &&
                Objects.equals(statsCommonDimension, that.statsCommonDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationDimension, statsCommonDimension);
    }

    @Override
    public String toString() {
        return "StatsLocationDimension{" +
                "locationDimension=" + locationDimension +
                ", statsCommonDimension=" + statsCommonDimension +
                '}';
    }
}