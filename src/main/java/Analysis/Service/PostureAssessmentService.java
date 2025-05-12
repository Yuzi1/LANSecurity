package Analysis.Service;

import Meta.SecurityEvent;
import Meta.SecurityPosture;

import java.util.List;

// 态势评估
public class PostureAssessmentService {
    private static final double BASE_RISK_SCORE = 50.0;
    private static final double EVENT_WEIGHT = 0.7;
    private static final double VULNERABILITY_WEIGHT = 0.3;

    public SecurityPosture assessPosture(List<SecurityEvent> events,
                                         double vulnerabilityScore) {
        SecurityPosture posture = new SecurityPosture();
        posture.setTimestamp(System.currentTimeMillis());

        // 计算风险评分
        double eventScore = events.stream()
                .mapToDouble(e -> e.getSeverity() * 2.5) // 将严重等级转换为分数
                .average()
                .orElse(0.0);

        double riskScore = BASE_RISK_SCORE +
                (eventScore * EVENT_WEIGHT) +
                (vulnerabilityScore * VULNERABILITY_WEIGHT);
        posture.setRiskScore(Math.min(100.0, riskScore));

        // 确定威胁等级
        posture.setThreatLevel(calculateThreatLevel(riskScore));

        // 统计事件类型
        posture.setAnomalyCount((int) events.stream()
                .filter(e -> e.getEventType().contains("Anomaly")).count());
        posture.setAttackCount((int) events.stream()
                .filter(e -> e.getEventType().contains("Attack")).count());

        return posture;
    }

    private int calculateThreatLevel(double riskScore) {
        if (riskScore >= 80) return 5; // 危急
        if (riskScore >= 60) return 4; // 高危
        if (riskScore >= 40) return 3; // 中危
        if (riskScore >= 20) return 2; // 低危
        return 1; // 正常
    }
}
