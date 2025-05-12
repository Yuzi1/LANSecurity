package DateBase.DAO;

import DateBase.DatabaseManager;
import process.Normalization.FeatureData;
import java.sql.*;
import java.util.*;

public class ConnectionInfoDAO {
    public void saveOrUpdate(String connectionKey, FeatureData featureData) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO connection_info (connection_key, src_ip, dst_ip, protocol, " +
                    "src_port, dst_port, start_time, last_update, duration, packet_count, " +
                    "byte_count, packet_rate, byte_rate, avg_packet_size) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "last_update = ?, duration = ?, packet_count = ?, byte_count = ?, " +
                    "packet_rate = ?, byte_rate = ?, avg_packet_size = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, connectionKey);
                pstmt.setString(2, featureData.getSrcIp());
                pstmt.setString(3, featureData.getDstIp());
                pstmt.setString(4, featureData.getProtocol());
                pstmt.setInt(5, featureData.getSrcHostPorts());
                pstmt.setInt(6, featureData.getSrcHostDestinations());
                pstmt.setLong(7, featureData.getTimestamp() - featureData.getConnectionDuration());
                pstmt.setLong(8, featureData.getTimestamp());
                pstmt.setLong(9, featureData.getConnectionDuration());
                pstmt.setInt(10, featureData.getPacketCount());
                pstmt.setLong(11, featureData.getByteCount());
                pstmt.setDouble(12, featureData.getPacketRate());
                pstmt.setDouble(13, featureData.getByteRate());
                pstmt.setDouble(14, featureData.getAvgPacketSize());


                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ConnectionInfo> getActiveConnections(int limit) {
        List<ConnectionInfo> connections = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM connection_info " +
                    "WHERE last_update > ? " +
                    "ORDER BY last_update DESC LIMIT ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, System.currentTimeMillis() - 60000); // 最近一分钟
                pstmt.setInt(2, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ConnectionInfo info = new ConnectionInfo();
                        info.setId(rs.getLong("id"));
                        info.setConnectionKey(rs.getString("connection_key"));
                        info.setSrcIp(rs.getString("src_ip"));
                        info.setDstIp(rs.getString("dst_ip"));
                        info.setProtocol(rs.getString("protocol"));
                        info.setSrcPort(rs.getInt("src_port"));
                        info.setDstPort(rs.getInt("dst_port"));
                        info.setStartTime(rs.getLong("start_time"));
                        info.setLastUpdate(rs.getLong("last_update"));
                        info.setDuration(rs.getLong("duration"));
                        info.setPacketCount(rs.getInt("packet_count"));
                        info.setByteCount(rs.getLong("byte_count"));
                        info.setPacketRate(rs.getDouble("packet_rate"));
                        info.setByteRate(rs.getDouble("byte_rate"));
                        info.setAvgPacketSize(rs.getDouble("avg_packet_size"));

                        connections.add(info);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connections;
    }
}
