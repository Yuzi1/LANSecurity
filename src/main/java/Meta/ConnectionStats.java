package Meta;

//// 连接统计数据
public class ConnectionStats {
    private long startTime;
    private long lastUpdateTime;
    private int packetCount;
    private long byteCount;

    public ConnectionStats() {
        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = startTime;
        this.packetCount = 0;
        this.byteCount = 0;
    }

    public void update(PacketInfo packet) {
        this.lastUpdateTime = System.currentTimeMillis();
        this.packetCount++;
        this.byteCount += packet.getPacketSize();
    }

    public long getDuration() {
        return lastUpdateTime - startTime;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public long getByteCount() {
        return byteCount;
    }

    public double getPacketRate() {
        long duration = getDuration();
        return duration > 0 ? (double) packetCount * 1000 / duration : 0;
    }

    public double getByteRate() {
        long duration = getDuration();
        return duration > 0 ? (double) byteCount * 1000 / duration : 0;
    }

    public double getAvgPacketSize() {
        return packetCount > 0 ? (double) byteCount / packetCount : 0;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
