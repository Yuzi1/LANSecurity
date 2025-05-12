package Meta;

import java.util.regex.Pattern;

// 攻击特征类
public class AttackSignature {
    private String id;
    private String description;
    private Pattern pattern;
    private int severity;

    public AttackSignature(String id, String description, Pattern pattern, int severity) {
        this.id = id;
        this.description = description;
        this.pattern = pattern;
        this.severity = severity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
