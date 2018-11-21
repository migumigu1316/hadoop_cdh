package com.bigdata.analystic.model.base;


import com.bigdata.common.GlobalConstants;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * TODO 浏览器维度类
 */
public class BrowserDimension extends BaseDimension {
    private int id;
    private String browserName;
    private String browserVersion;

    public BrowserDimension() {
    }

    public BrowserDimension(String browerName, String browerVersion) {
        this.browserName = browerName;
        this.browserVersion = browerVersion;
    }

    public BrowserDimension(int id, String browerName, String browerVersion) {
        this(browerName, browerVersion);
        this.id = id;

    }

    /**
     * 构建浏览器纬度的集合对象
     * @param browerName
     * @param browerVersion
     * @return
     */
    public static BrowserDimension getInstance(String browerName, String browerVersion){
        if (StringUtils.isEmpty(browerName)) {
            browerName = browerVersion = GlobalConstants.DEFAULT_VALUE;
        }
        if (StringUtils.isEmpty(browerVersion)) {
            browerVersion = GlobalConstants.DEFAULT_VALUE;
        }
        return new BrowserDimension(browerName,browerVersion);
    }


    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(browserName);
        dataOutput.writeUTF(browserVersion);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        id = dataInput.readInt();
        browserName = dataInput.readUTF();
        browserVersion = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }

        BrowserDimension other = (BrowserDimension)o;
        int tmp = this.id - other.id;
        if (tmp != 0){
            return tmp;
        }

        tmp = this.browserName.compareTo(other.browserName);
        if (tmp != 0){
            return tmp;
        }

        return  this.browserVersion.compareTo(other.browserVersion);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserDimension that = (BrowserDimension) o;
        return id == that.id &&
                Objects.equals(browserName, that.browserName) &&
                Objects.equals(browserVersion, that.browserVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, browserName, browserVersion);
    }

    @Override
    public String toString() {
        return "BrowserDimension{" +
                "id=" + id +
                ", browserName='" + browserName + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                '}';
    }
}
