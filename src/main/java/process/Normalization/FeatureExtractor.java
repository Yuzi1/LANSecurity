package process.Normalization;

import Meta.ConnectionStats;
import Meta.HostStats;
import Meta.PacketInfo;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FeatureExtractor {
    private Map<String, ConnectionStats> connectionMap = new ConcurrentHashMap<>();
    private Map<String, HostStats> hostStatsMap = new ConcurrentHashMap<>();

    public FeatureData extractFeatures(PacketInfo packet) {
        // 更新连接统计
        updateConnectionStats(packet);

        // 更新主机统计
        updateHostStats(packet);

        // 提取特征数据
        FeatureData feature = new FeatureData();
        feature.setTimestamp(packet.getTimestamp());
        feature.setSrcIp(packet.getSrcIp());
        feature.setDstIp(packet.getDstIp());
        feature.setProtocol(packet.getProtocol());

        // 添加连接特征
        String connectionKey = getConnectionKey(packet);
        ConnectionStats connStats = connectionMap.get(connectionKey);
        if (connStats != null) {
            feature.setConnectionDuration(connStats.getDuration());
            feature.setPacketCount(connStats.getPacketCount());
            feature.setByteCount(connStats.getByteCount());
            feature.setPacketRate(connStats.getPacketRate());
            feature.setByteRate(connStats.getByteRate());
            feature.setAvgPacketSize(connStats.getAvgPacketSize());
        }

        // 添加主机特征
        HostStats srcStats = hostStatsMap.get(packet.getSrcIp());
        if (srcStats != null) {
            feature.setSrcHostConnections(srcStats.getConnectionCount());
            feature.setSrcHostPorts(srcStats.getUniquePortCount());
            feature.setSrcHostDestinations(srcStats.getUniqueDestCount());
        }

        return feature;
    }

    private void updateConnectionStats(PacketInfo packet) {
        String key = getConnectionKey(packet);
        ConnectionStats stats = connectionMap.computeIfAbsent(key, k -> new ConnectionStats());
        stats.update(packet);
    }

    private void updateHostStats(PacketInfo packet) {
        // 更新源主机统计
        HostStats srcStats = hostStatsMap.computeIfAbsent(packet.getSrcIp(), k -> new HostStats());
        srcStats.update(packet.getDstIp(), packet.getDstPort());

        // 更新目的主机统计
        HostStats dstStats = hostStatsMap.computeIfAbsent(packet.getDstIp(), k -> new HostStats());
        dstStats.update(packet.getSrcIp(), packet.getSrcPort());
    }

    private String getConnectionKey(PacketInfo packet) {
        if (packet.getSrcIp().compareTo(packet.getDstIp()) < 0) {
            return packet.getSrcIp() + ":" + packet.getSrcPort() + "-" +
                    packet.getDstIp() + ":" + packet.getDstPort() + "-" + packet.getProtocol();
        } else {
            return packet.getDstIp() + ":" + packet.getDstPort() + "-" +
                    packet.getSrcIp() + ":" + packet.getSrcPort() + "-" + packet.getProtocol();
        }
    }

    // 清理过期连接
    public void cleanupExpiredConnections(long expirationTime) {
        long currentTime = System.currentTimeMillis();
        connectionMap.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getLastUpdateTime() > expirationTime);
    }
}
