package DateBase.DAO;
import Meta.SecurityEvent;
import DateBase.DatabaseManager;
import java.sql.*;
import java.util.*;
public class SecurityEventDAO {
    public void saveEvent(SecurityEvent event) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO security_event (timestamp, event_type, src_ip, dst_ip, " +
                    "protocol, src_port, dst_port, severity, description, raw_data) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, event.getTimestamp());
                pstmt.setString(2, event.getEventType());
                pstmt.setString(3, event.getSrcIp());
                pstmt.setString(4, event.getDstIp());
                pstmt.setString(5, event.getProtocol());
                pstmt.setInt(6, event.getSrcPort());
                pstmt.setInt(7, event.getDstPort());
                pstmt.setInt(8, event.getSeverity());
                pstmt.setString(9, event.getDescription());
                pstmt.setString(10, event.getRawData());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SecurityEvent> getRecentEvents(int limit) {
        List<SecurityEvent> events = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM security_event " +
                    "ORDER BY timestamp DESC LIMIT ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        SecurityEvent event = new SecurityEvent();
                        event.setId(rs.getLong("id"));
                        event.setTimestamp(rs.getLong("timestamp"));
                        event.setEventType(rs.getString("event_type"));
                        event.setSrcIp(rs.getString("src_ip"));
                        event.setDstIp(rs.getString("dst_ip"));
                        event.setProtocol(rs.getString("protocol"));
                        event.setSrcPort(rs.getInt("src_port"));
                        event.setDstPort(rs.getInt("dst_port"));
                        event.setSeverity(rs.getInt("severity"));
                        event.setDescription(rs.getString("description"));
                        event.setRawData(rs.getString("raw_data"));

                        events.add(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public Map<String, Integer> getEventTypeStats() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT event_type, COUNT(*) as count FROM security_event " +
                    "WHERE timestamp > ? " +
                    "GROUP BY event_type";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, System.currentTimeMillis() - 86400000); // 最近一天

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        stats.put(rs.getString("event_type"), rs.getInt("count"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
