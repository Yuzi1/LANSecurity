package Analysis.Rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// 规则评估结果
public class RuleResult {
    private boolean triggered;
    private String ruleId;
    private String ruleName;
    private String message;
    private int severity;
    private Map<String, Object> additionalInfo = new HashMap<>();

    public RuleResult(boolean triggered, String ruleId, String ruleName) {
        this.triggered = triggered;
        this.ruleId = ruleId;
        this.ruleName = ruleName;
    }

    public static RuleResult notTriggered(String ruleId, String ruleName) {
        return new RuleResult(false, ruleId, ruleName);
    }

    public static RuleResult triggered(String ruleId, String ruleName, String message, int severity) {
        RuleResult result = new RuleResult(true, ruleId, ruleName);
        result.setMessage(message);
        result.setSeverity(severity);
        return result;
    }


    public boolean isTriggered() {
        return triggered;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public void addAdditionalInfo(String key, Object value) {
        additionalInfo.put(key, value);
    }

    public Object getAdditionalInfo(String key) {
        return additionalInfo.get(key);
    }

    public Map<String, Object> getAllAdditionalInfo() {
        return Collections.unmodifiableMap(additionalInfo);
    }
}
