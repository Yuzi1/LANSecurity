package Analysis.Rule;

import Analysis.Service.BaselineService;

import java.util.HashMap;
import java.util.Map;

// 流量统计异常检测规则
public class TrafficAnomalyRule extends AbstractRule{
    private BaselineService baselineService;

    public TrafficAnomalyRule(BaselineService baselineService) {
        super("RULE_TRAFFIC_ANOMALY", "Traffic Anomaly Detection",
                "Detects traffic volume that deviates significantly from the baseline", 100);
        this.baselineService = baselineService;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        // 获取当前流量统计
        Map<String, Double> currentStats = context.getFact("current_traffic_stats", Map.class);
        if (currentStats == null || currentStats.isEmpty()) {
            return RuleResult.notTriggered(getId(), getName());
        }

        boolean anomalyDetected = false;
        StringBuilder messageBuilder = new StringBuilder();
        Map<String, Object> anomalyDetails = new HashMap<>();

        // 检查各指标是否异常
        for (Map.Entry<String, Double> entry : currentStats.entrySet()) {
            String metricKey = entry.getKey();
            double currentValue = entry.getValue();

            if (baselineService.isAnomaly(metricKey, currentValue)) {
                anomalyDetected = true;
                double[] range = baselineService.getNormalRange(metricKey);

                String anomalyType = currentValue > range[1] ? "increase" : "decrease";
                String detail = String.format("%s shows abnormal %s: %.2f (normal range: [%.2f, %.2f])",
                        formatMetricName(metricKey), anomalyType, currentValue, range[0], range[1]);

                if (messageBuilder.length() > 0) {
                    messageBuilder.append("; ");
                }
                messageBuilder.append(detail);

                // 添加详细信息
                Map<String, Object> metricDetails = new HashMap<>();
                metricDetails.put("current", currentValue);
                metricDetails.put("normalRange", range);
                metricDetails.put("anomalyType", anomalyType);
                anomalyDetails.put(metricKey, metricDetails);
            }
        }

        if (anomalyDetected) {
            RuleResult result = RuleResult.triggered(getId(), getName(),
                    "Traffic anomaly detected: " + messageBuilder.toString(), 3);
            result.addAdditionalInfo("details", anomalyDetails);
            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }

    private String formatMetricName(String metricKey) {
        // 将指标键名转换为可读的名称
        switch (metricKey) {
            case "total_packets":
                return "Total packet rate";
            case "total_bytes":
                return "Total traffic volume";
            default:
                if (metricKey.startsWith("protocol_")) {
                    return metricKey.substring(9) + " traffic";
                }
                return metricKey;
        }
    }
}
