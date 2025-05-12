package Analysis.Service;

import Analysis.DAO.BaselineDAO;
import Meta.TimeSeriesData;
import Meta.Baseline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.*;

//基于统计的异常检测
public class BaselineService {
    private static final Logger logger = LoggerFactory.getLogger(BaselineService.class);
    private final Map<String, Baseline> trafficBaselines = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final BaselineDAO baselineDAO;

    public BaselineService(BaselineDAO baselineDAO) {
        this.baselineDAO = baselineDAO;
    }

    /**
     * 初始化并启动基线更新任务
     */
    public void start() {
        // 加载历史基线数据
        loadBaselines();

        // 定期计算基线
        scheduler.scheduleAtFixedRate(this::updateBaselines, 1, 1, TimeUnit.HOURS);
    }

    /**
     * 停止基线服务
     */
    public void stop() {
        scheduler.shutdown();
        saveBaselines();
    }

    /**
     * 加载历史基线数据
     */
    private void loadBaselines() {
        try {
            List<Baseline> baselines = baselineDAO.getAllBaselines();
            for (Baseline baseline : baselines) {
                trafficBaselines.put(baseline.getMetricKey(), baseline);
            }
            logger.info("Loaded {} baselines from database", baselines.size());
        } catch (Exception e) {
            logger.error("Failed to load baselines", e);
        }
    }

    /**
     * 保存基线数据到数据库
     */
    private void saveBaselines() {
        try {
            for (Baseline baseline : trafficBaselines.values()) {
                baselineDAO.saveBaseline(baseline);
            }
            logger.info("Saved {} baselines to database", trafficBaselines.size());
        } catch (Exception e) {
            logger.error("Failed to save baselines", e);
        }
    }

    /**
     * 更新所有基线
     */
    private void updateBaselines() {
        try {
            // 获取最近数据
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 24 * 60 * 60 * 1000; // 最近24小时

            // 更新网络流量基线
            updateTrafficBaseline("total_packets", startTime, endTime);
            updateTrafficBaseline("total_bytes", startTime, endTime);

            // 更新协议流量基线
            for (String protocol : Arrays.asList("TCP", "UDP", "ICMP")) {
                updateTrafficBaseline("protocol_" + protocol, startTime, endTime);
            }

            // 保存更新后的基线
            saveBaselines();

            logger.info("Baselines updated successfully");
        } catch (Exception e) {
            logger.error("Failed to update baselines", e);
        }
    }

    /**
     * 更新特定指标的基线
     */
    private void updateTrafficBaseline(String metricKey, long startTime, long endTime) {
        // 获取历史数据
        List<TimeSeriesData> timeSeriesData = baselineDAO.getMetricTimeSeries(metricKey, startTime, endTime);

        if (timeSeriesData.isEmpty()) {
            logger.warn("No data available for metric: {}", metricKey);
            return;
        }

        // 提取数值
        double[] values = timeSeriesData.stream()
                .mapToDouble(TimeSeriesData::getValue)
                .toArray();

        // 计算均值和标准差
        DoubleSummaryStatistics stats = Arrays.stream(values).summaryStatistics();
        double mean = stats.getAverage();

        double variance = Arrays.stream(values)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(variance);

        // 更新或创建基线
        Baseline baseline = trafficBaselines.computeIfAbsent(
                metricKey,
                k -> new Baseline(metricKey, 0, 0, 0, System.currentTimeMillis())
        );

        baseline.setMean(mean);
        baseline.setStdDev(stdDev);
        baseline.setLastUpdated(System.currentTimeMillis());

        logger.debug("Updated baseline for {}: mean={}, stdDev={}", metricKey, mean, stdDev);
    }

    /**
     * 检查指标值是否异常
     * @param metricKey 指标键名
     * @param value 当前值
     * @return 是否异常（超过均值±3σ）
     */
    public boolean isAnomaly(String metricKey, double value) {
        Baseline baseline = trafficBaselines.get(metricKey);
        if (baseline == null) {
            return false; // 没有基线数据，无法判断
        }

        double mean = baseline.getMean();
        double stdDev = baseline.getStdDev();
        double lowerBound = mean - 3 * stdDev;
        double upperBound = mean + 3 * stdDev;

        return value < lowerBound || value > upperBound;
    }

    /**
     * 获取指标的正常范围
     * @param metricKey 指标键名
     * @return 正常范围 [下限, 上限]
     */
    public double[] getNormalRange(String metricKey) {
        Baseline baseline = trafficBaselines.get(metricKey);
        if (baseline == null) {
            return new double[] { 0, 0 };
        }

        double mean = baseline.getMean();
        double stdDev = baseline.getStdDev();
        double lowerBound = Math.max(0, mean - 3 * stdDev);
        double upperBound = mean + 3 * stdDev;

        return new double[] { lowerBound, upperBound };
    }
}
