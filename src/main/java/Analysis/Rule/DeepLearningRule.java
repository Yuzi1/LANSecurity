package Analysis.Rule;

import Analysis.Service.AIService;
import Meta.PacketInfo;

//深度学习规则基类
public abstract class DeepLearningRule extends AbstractRule {
    private Autoencoder autoencoder;

    public DeepLearningAnomalyRule() {
        super("RULE_DL_ANOMALY", "Deep Learning Anomaly", "AI-based anomaly detection", 100);
        this.autoencoder = new Autoencoder(10); // 输入维度根据特征调整
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        PacketInfo packet = context.getFact("packet", PacketInfo.class);
        if (packet == null) return RuleResult.notTriggered(getId(), getName());

        // 提取特征（示例）
        double[] features = extractFeatures(packet);
        double score = autoencoder.detectAnomaly(features);

        if (score > 0.5) { // 阈值需根据训练调整
            return RuleResult.triggered(
                    getId(), getName(),
                    "AI detected anomaly (Score: " + score + ")", 3
            );
        }
        return RuleResult.notTriggered(getId(), getName());
    }

    private double[] extractFeatures(PacketInfo packet) {
        // 特征：包大小、协议类型、端口
        return new double[]{
                packet.getPacketSize(),
                packet.getProtocol().equals("TCP") ? 1 : 0,
                packet.getDstPort()
        };
    }
}
