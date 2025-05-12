package DateBase.DAO;

public class ConnectionInfo {
    private Long id;
    private String connectionKey;
    private String srcIp;
    private String dstIp;
    private String protocol;
    private int srcPort;
    private int dstPort;
    private long startTime;
    private long lastUpdate;
    private long duration;
    private int packetCount;
    private long byteCount;
    private double packetRate;
    private double byteRate;
    private double avgPacketSize;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConnectionKey() { return connectionKey; }
    public void setConnectionKey(String connectionKey) { this.connectionKey = connectionKey; }
    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }
    public String getDstIp() { return dstIp; }
    public void setDstIp(String dstIp) { this.dstIp = dstIp; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public int getSrcPort() { return srcPort; }
    public void setSrcPort(int srcPort) { this.srcPort = srcPort; }
    public int getDstPort() { return dstPort; }
    public void setDstPort(int dstPort) { this.dstPort = dstPort; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(long lastUpdate) { this.lastUpdate = lastUpdate; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public int getPacketCount() { return packetCount; }
    public void setPacketCount(int packetCount) { this.packetCount = packetCount; }
    public long getByteCount() { return byteCount; }
    public void setByteCount(long byteCount) { this.byteCount = byteCount; }
    public double getPacketRate() { return packetRate; }
    public void setPacketRate(double packetRate) { this.packetRate = packetRate; }
    public double getByteRate() { return byteRate; }
    public void setByteRate(double byteRate) { this.byteRate = byteRate; }
    public double getAvgPacketSize() { return avgPacketSize; }
    public void setAvgPacketSize(double avgPacketSize) { this.avgPacketSize = avgPacketSize; }
}
