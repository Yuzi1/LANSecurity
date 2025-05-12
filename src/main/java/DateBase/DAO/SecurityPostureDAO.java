package DateBase.DAO;
import DateBase.DatabaseManager;
import Meta.SecurityPosture;
import java.sql.*;
import java.util.*;
public class SecurityPostureDAO {
    public void savePosture(SecurityPosture posture) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO security_posture (timestamp, risk_score, threat_level, " +
                    "anomaly_count, attack_count, scan_count, details) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, posture.getTimestamp());
                pstmt.setDouble(2, posture.getRiskScore());
                pstmt.setInt(3, posture.getThreatLevel());
                pstmt.setInt(4, posture.getAnomalyCount());
                pstmt.setInt(5, posture.getAttackCount());
                pstmt.setInt(6, posture.getScanCount());
                pstmt.setString(7, posture.getDetails());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SecurityPosture> getPostureHistory(long startTime, long endTime) {
        List<SecurityPosture> history = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM security_posture " +
                    "WHERE timestamp BETWEEN ? AND ? " +
                    "ORDER BY timestamp";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, startTime);
                pstmt.setLong(2, endTime);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        SecurityPosture posture = new SecurityPosture();
                        posture.setId(rs.getLong("id"));
                        posture.setTimestamp(rs.getLong("timestamp"));
                        posture.setRiskScore(rs.getDouble("risk_score"));
                        posture.setThreatLevel(rs.getInt("threat_level"));
                        posture.setAnomalyCount(rs.getInt("anomaly_count"));
                        posture.setAttackCount(rs.getInt("attack_count"));
                        posture.setScanCount(rs.getInt("scan_count"));
                        posture.setDetails(rs.getString("details"));

                        history.add(posture);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public SecurityPosture getLatestPosture() {
        SecurityPosture posture = null;

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM security_posture " +
                    "ORDER BY timestamp DESC LIMIT 1";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    posture = new SecurityPosture();
                    posture.setId(rs.getLong("id"));
                    posture.setTimestamp(rs.getLong("timestamp"));
                    posture.setRiskScore(rs.getDouble("risk_score"));
                    posture.setThreatLevel(rs.getInt("threat_level"));
                    posture.setAnomalyCount(rs.getInt("anomaly_count"));
                    posture.setAttackCount(rs.getInt("attack_count"));
                    posture.setScanCount(rs.getInt("scan_count"));
                    posture.setDetails(rs.getString("details"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posture;
    }
}
