package Meta;

import java.io.Serializable;

public class SecurityPosture implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private long timestamp;
    private double riskScore;
    private int threatLevel;
    private int anomalyCount;
    private int attackCount;
    private int scanCount;
    private String details;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public int getThreatLevel() { return threatLevel; }
    public void setThreatLevel(int threatLevel) { this.threatLevel = threatLevel; }
    public int getAnomalyCount() { return anomalyCount; }
    public void setAnomalyCount(int anomalyCount) { this.anomalyCount = anomalyCount; }
    public int getAttackCount() { return attackCount; }
    public void setAttackCount(int attackCount) { this.attackCount = attackCount; }
    public int getScanCount() { return scanCount; }
    public void setScanCount(int scanCount) { this.scanCount = scanCount; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
