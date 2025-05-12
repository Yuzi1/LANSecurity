package Analysis.Service;

import Meta.AttackSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

//攻击特征库（保留关键签名，CNN补充检测）
public class AttackSignatureDatabase {
    private Map<String, AttackSignature> signatures = new ConcurrentHashMap<>();

    public AttackSignatureDatabase() {
        // 仅保留高确定性规则（如SQL注入基础特征）
        addSignature(new AttackSignature(
                "SQLI-001",
                "Basic SQL Injection",
                Pattern.compile("(?i)(union\\s+select|sleep\\s*\\(|benchmark\\s*\\()"),
                5
        ));
    }

    public void addSignature(AttackSignature signature) {
        signatures.put(signature.getId(), signature);
    }

    public List<AttackMatch> analyzePayload(String payload) {
        List<AttackMatch> matches = new ArrayList<>();
        for (AttackSignature sig : signatures.values()) {
            if (sig.getPattern().matcher(payload).find()) {
                matches.add(new AttackMatch(
                        sig.getId(),
                        sig.getDescription(),
                        "Matched pattern",
                        sig.getSeverity()
                ));
            }
        }
        return matches;
    }
}
