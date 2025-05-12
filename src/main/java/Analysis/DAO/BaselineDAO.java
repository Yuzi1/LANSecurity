package Analysis.DAO;

import DateBase.DatabaseManager;
import Meta.Baseline;
import Meta.TimeSeriesData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BaselineDAO {
    private DatabaseManager dbManager;

    public BaselineDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 保存基线数据
     */
    public void saveBaseline(Baseline baseline) {
        try (Connection conn = dbManager.getConnection()) {
            String sql = "INSERT INTO traffic_baseline (metric_key, mean, std_dev, sample_count, last_updated) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE mean = ?, std_dev = ?, sample_count = ?, last_updated = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, baseline.getMetricKey());
                pstmt.setDouble(2, baseline.getMean());
                pstmt.setDouble(3, baseline.getStdDev());
                pstmt.setLong(4, baseline.getSampleCount());
                pstmt.setLong(5, baseline.getLastUpdated());

                pstmt.setDouble(6, baseline.getMean());
                pstmt.setDouble(7, baseline.getStdDev());
                pstmt.setLong(8, baseline.getSampleCount());
                pstmt.setLong(9, baseline.getLastUpdated());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save baseline", e);
        }
    }

    /**
     * 获取所有基线数据
     */
    public List<Baseline> getAllBaselines() {
        List<Baseline> baselines = new ArrayList<>();

        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT * FROM traffic_baseline";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String metricKey = rs.getString("metric_key");
                    double mean = rs.getDouble("mean");
                    double stdDev = rs.getDouble("std_dev");
                    long sampleCount = rs.getLong("sample_count");
                    long lastUpdated = rs.getLong("last_updated");

                    baselines.add(new Baseline(metricKey, mean, stdDev, sampleCount, lastUpdated));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch baselines", e);
        }

        return baselines;
    }

    /**
     * 获取指标的时间序列数据
     */
    public List<TimeSeriesData> getMetricTimeSeries(String metricKey, long startTime, long endTime) {
        List<TimeSeriesData> timeSeries = new ArrayList<>();

        try (Connection conn = dbManager.getConnection()) {
            String sql = "SELECT timestamp, value FROM traffic_metrics " +
                    "WHERE metric_key = ? AND timestamp BETWEEN ? AND ? " +
                    "ORDER BY timestamp";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, metricKey);
                pstmt.setLong(2, startTime);
                pstmt.setLong(3, endTime);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        long timestamp = rs.getLong("timestamp");
                        double value = rs.getDouble("value");

                        timeSeries.add(new TimeSeriesData(timestamp, metricKey, value));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch time series data", e);
        }

        return timeSeries;
    }
}
