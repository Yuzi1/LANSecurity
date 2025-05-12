package Analysis.Rule;

import Analysis.Service.AttackMatch;
import Analysis.Service.AttackSignatureDatabase;
import Meta.PacketInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// 攻击特征匹配规则
public class AttackSignatureRule extends AbstractRule{
    private AttackSignatureDatabase signatureDB;

    public AttackSignatureRule(AttackSignatureDatabase signatureDB) {
        super("RULE_ATTACK_SIGNATURE", "Attack Signature Detection",
                "Detects known attack patterns in network payloads", 150);
        this.signatureDB = signatureDB;
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        PacketInfo packet = context.getFact("packet", PacketInfo.class);
        if (packet == null || packet.getPayload() == null) {
            return RuleResult.notTriggered(getId(), getName());
        }

        List<AttackMatch> matches = signatureDB.analyzePayload(packet.getPayload());
        if (!matches.isEmpty()) {
            AttackMatch highestSeverity = Collections.max(matches,
                    Comparator.comparingInt(AttackMatch::getSeverity));

            String message = String.format(
                    "Attack signature detected: %s (Severity %d)",
                    highestSeverity.getDescription(), highestSeverity.getSeverity());

            RuleResult result = RuleResult.triggered(
                    getId(), getName(), message, highestSeverity.getSeverity());
            result.addAdditionalInfo("matches", matches);
            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }
}
