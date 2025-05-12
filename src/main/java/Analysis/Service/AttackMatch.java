package Analysis.Service;

public class AttackMatch implements Comparable<AttackMatch>{
    private final String attackId;
    private final String description;
    private final String matchedContent;
    private final int severity;

    public AttackMatch(String attackId, String description,
                       String matchedContent, int severity) {
        this.attackId = attackId;
        this.description = description;
        this.matchedContent = matchedContent;
        this.severity = severity;
    }
    
    public String getAttackId() { 
        return attackId; 
    }
    
    public String getDescription() { 
        return description; 
    }
    public String getMatchedContent() { 
        return matchedContent; 
    }
    public int getSeverity() { 
        return severity; 
    }

    @Override
    public int compareTo(AttackMatch other) {
        return Integer.compare(this.severity, other.severity);
    }
}
