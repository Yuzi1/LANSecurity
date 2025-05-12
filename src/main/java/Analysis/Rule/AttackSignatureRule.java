package Analysis.Rule;

import Analysis.Service.AttackMatch;
import DeepLearning.threat.CNNModel;
import Meta.PacketInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// 攻击特征检测规则（集成传统签名匹配和CNN模型）
public class AttackSignatureRule extends AbstractRule{
    private AttackSignatureDatabase signatureDB;
    private CNNModel cnnModel; // CNN模型用于载荷分析
    private static final double CNN_THRESHOLD = 0.8; // CNN判定阈值

    public AttackSignatureRule(AttackSignatureDatabase signatureDB, CNNModel cnnModel) {
        super("RULE_ATTACK_SIGNATURE", "Attack Signature Detection",
                "Detects known attack patterns with deep learning support", 150);
        this.signatureDB = signatureDB;
        this.cnnModel = cnnModel;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        PacketInfo packet = context.getFact("packet", PacketInfo.class);
        if (packet == null || packet.getPayload() == null) {
            return RuleResult.notTriggered(getId(), getName());
        }

        // 传统签名匹配
        List<AttackMatch> matches = signatureDB.analyzePayload(packet.getPayload());

        // CNN检测加密/变形攻击
        double cnnScore = cnnModel.analyzePayload(packet.getPayload().getBytes());
        boolean cnnDetected = cnnScore > CNN_THRESHOLD;

        // 综合判定
        if (!matches.isEmpty() || cnnDetected) {
            String message;
            if (cnnDetected) {
                message = String.format("Deep learning detected suspicious payload (Score: %.2f)", cnnScore);
            } else {
                AttackMatch highest = Collections.max(matches, Comparator.comparingInt(AttackMatch::getSeverity));
                message = String.format("Signature match: %s (Severity %d)", highest.getDescription(), highest.getSeverity());
            }

            RuleResult result = RuleResult.triggered(getId(), getName(), message,
                    cnnDetected ? 5 : highest.getSeverity()); // CNN检测赋予更高优先级
            result.addAdditionalInfo("cnn_score", cnnScore);
            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }
}
