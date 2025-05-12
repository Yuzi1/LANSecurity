package process.Normalization;

import java.io.Serializable;

public class FeatureData implements Serializable {
    private static final long serialVersionUID = 1L;

    private long timestamp;
    private String srcIp;
    private String dstIp;
    private String protocol;

    // 连接特征
    private long connectionDuration;
    private int packetCount;
    private long byteCount;
    private double packetRate;
    private double byteRate;
    private double avgPacketSize;

    // 主机特征
    private int srcHostConnections;
    private int srcHostPorts;
    private int srcHostDestinations;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSrcIp() {
        return srcIp;
    }
    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public long getConnectionDuration() {
        return connectionDuration;
    }

    public void setConnectionDuration(long connectionDuration) {
        this.connectionDuration = connectionDuration;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public void setPacketCount(int packetCount) {
        this.packetCount = packetCount;
    }

    public long getByteCount() {
        return byteCount;
    }

    public void setByteCount(long byteCount) {
        this.byteCount = byteCount;
    }

    public double getPacketRate() {
        return packetRate;
    }

    public void setPacketRate(double packetRate) {
        this.packetRate = packetRate;
    }

    public double getByteRate() {
        return byteRate;
    }

    public void setByteRate(double byteRate) {
        this.byteRate = byteRate;
    }

    public double getAvgPacketSize() {
        return avgPacketSize;
    }

    public void setAvgPacketSize(double avgPacketSize) {
        this.avgPacketSize = avgPacketSize;
    }

    public int getSrcHostConnections() {
        return srcHostConnections;
    }

    public void setSrcHostConnections(int srcHostConnections) {
        this.srcHostConnections = srcHostConnections;
    }

    public int getSrcHostPorts() {
        return srcHostPorts;
    }

    public void setSrcHostPorts(int srcHostPorts) {
        this.srcHostPorts = srcHostPorts;
    }

    public int getSrcHostDestinations() {
        return srcHostDestinations;
    }

    public void setSrcHostDestinations(int srcHostDestinations) {
        this.srcHostDestinations = srcHostDestinations;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
