package DateBase.DAO;

import DateBase.DatabaseManager;
import Meta.HostStats;
import java.util.*;
import java.sql.*;

public class HostStatsDAO {
    public void updateHostStats(String ipAddress, HostStats stats) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO host_stats (ip_address, connection_count, unique_port_count, " +
                    "unique_dest_count, scan_probability, last_update) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "connection_count = ?, unique_port_count = ?, unique_dest_count = ?, " +
                    "scan_probability = ?, last_update = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                double scanProbability = calculateScanProbability(stats);

                // 插入参数
                pstmt.setString(1, ipAddress);
                pstmt.setInt(2, stats.getConnectionCount());
                pstmt.setInt(3, stats.getUniquePortCount());
                pstmt.setInt(4, stats.getUniqueDestCount());
                pstmt.setDouble(5, scanProbability);
                pstmt.setLong(6, System.currentTimeMillis());

                // 更新参数
                pstmt.setInt(7, stats.getConnectionCount());
                pstmt.setInt(8, stats.getUniquePortCount());
                pstmt.setInt(9, stats.getUniqueDestCount());
                pstmt.setDouble(10, scanProbability);
                pstmt.setLong(11, System.currentTimeMillis());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double calculateScanProbability(HostStats stats) {
        // 简单扫描概率计算：目的地址数量与端口数量之比
        int destCount = stats.getUniqueDestCount();
        int portCount = stats.getUniquePortCount();

        if (destCount == 0 || portCount == 0) {
            return 0.0;
        }

        // 端口数量远大于目的地址数量，可能是端口扫描
        if (portCount > 5 * destCount) {
            return Math.min(1.0, portCount / (double)(10 * destCount));
        }

        // 目的地址数量远大于端口数量，可能是主机扫描
        if (destCount > 5 * portCount) {
            return Math.min(1.0, destCount / (double)(10 * portCount));
        }

        return 0.0;
    }

    public List<Map<String, Object>> getTopScannersHosts(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT ip_address, connection_count, unique_port_count, " +
                    "unique_dest_count, scan_probability " +
                    "FROM host_stats " +
                    "WHERE scan_probability > 0.5 AND last_update > ? " +
                    "ORDER BY scan_probability DESC LIMIT ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, System.currentTimeMillis() - 3600000); // 最近一小时
                pstmt.setInt(2, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> host = new HashMap<>();
                        host.put("ipAddress", rs.getString("ip_address"));
                        host.put("connectionCount", rs.getInt("connection_count"));
                        host.put("uniquePortCount", rs.getInt("unique_port_count"));
                        host.put("uniqueDestCount", rs.getInt("unique_dest_count"));
                        host.put("scanProbability", rs.getDouble("scan_probability"));

                        results.add(host);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
