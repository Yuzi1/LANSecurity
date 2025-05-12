package Meta;

import java.io.Serializable;

public class SecurityEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private long timestamp;
    private String eventType;
    private String srcIp;
    private String dstIp;
    private String protocol;
    private int srcPort;
    private int dstPort;
    private int severity;
    private String description;
    private String rawData;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }
    public String getDstIp() { return dstIp; }
    public void setDstIp(String dstIp) { this.dstIp = dstIp; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public int getSrcPort() { return srcPort; }
    public void setSrcPort(int srcPort) { this.srcPort = srcPort; }
    public int getDstPort() { return dstPort; }
    public void setDstPort(int dstPort) { this.dstPort = dstPort; }
    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}
