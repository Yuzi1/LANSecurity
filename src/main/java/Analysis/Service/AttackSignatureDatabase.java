package Analysis.Service;

import Meta.AttackSignature;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 攻击特征库
public class AttackSignatureDatabase {
    private Map<String, AttackSignature> signatures = new ConcurrentHashMap<>();
    private static final String SQL_INJECTION_PATTERN =
            "(?i)(union.*select|sleep\\(\\s*\\d|benchmark\\s*\\(|\\bexec\\b|\\bxp_cmdshell\\b)";

    public AttackSignatureDatabase() {
        // 初始化已知攻击特征
        addSignature(new AttackSignature(
                "SQLI-001",
                "SQL Injection Detection",
                Pattern.compile(SQL_INJECTION_PATTERN),
                5 // 严重等级
        ));
    }

    public void addSignature(AttackSignature signature) {
        signatures.put(signature.getId(), signature);
    }

    public List<AttackMatch> analyzePayload(String payload) {
        List<AttackMatch> matches = new ArrayList<>();
        for (AttackSignature sig : signatures.values()) {
            Matcher matcher = sig.getPattern().matcher(payload);
            if (matcher.find()) {
                matches.add(new AttackMatch(
                        sig.getId(),
                        sig.getDescription(),
                        matcher.group(),
                        sig.getSeverity()
                ));
            }
        }
        return matches;
    }
}
