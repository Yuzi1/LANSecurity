package Analysis.Rule;

import Analysis.Service.BaselineService;
import DeepLearning.anomaly.AutoencoderModel;
import DeepLearning.utils.TensorUtils;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Map;

//流量异常检测规则（混合统计基线 + 自编码器）
public class TrafficAnomalyRule extends AbstractRule {
    private BaselineService baselineService;
    private AutoencoderModel autoencoder;
    private static final double AE_THRESHOLD = 1.5; // 自编码器重构误差阈值

    public TrafficAnomalyRule(BaselineService baselineService, AutoencoderModel autoencoder) {
        super("RULE_TRAFFIC_ANOMALY", "Traffic Anomaly Detection",
                "Detects anomalies using baseline and autoencoder", 100);
        this.baselineService = baselineService;
        this.autoencoder = autoencoder;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        Map<String, Double> currentStats = context.getFact("current_traffic_stats", Map.class);
        if (currentStats == null) return RuleResult.notTriggered(getId(), getName());

        // 传统统计检测
        boolean baselineAnomaly = currentStats.entrySet().stream()
                .anyMatch(e -> baselineService.isAnomaly(e.getKey(), e.getValue()));

        // 自编码器检测
        INDArray features = TensorUtils.convertToAutoencoderInput(currentStats);
        double aeScore = autoencoder.detectAnomaly(features);
        boolean aeAnomaly = aeScore > AE_THRESHOLD;

        if (baselineAnomaly || aeAnomaly) {
            String message = "Traffic anomaly detected: ";
            if (baselineAnomaly) message += "Baseline deviation; ";
            if (aeAnomaly) message += String.format("Autoencoder score (%.2f)", aeScore);

            RuleResult result = RuleResult.triggered(getId(), getName(), message.trim(),
                    aeAnomaly ? 4 : 3); // 自编码器检测赋予更高严重性
            result.addAdditionalInfo("ae_score", aeScore);
            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }
}
