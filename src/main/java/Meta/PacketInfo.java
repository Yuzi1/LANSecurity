package Meta;

import java.io.Serializable;

public class PacketInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 基本信息
    private long timestamp;
    private int packetSize;

    // 链路层
    private String srcMac;
    private String dstMac;
    private String etherType;

    // 网络层
    private String srcIp;
    private String dstIp;
    private int ipVersion;
    private String protocol;
    private int ttl;
    private int fragmentOffset;
//    private String FragmentFlags;
    private boolean moreFragmentFlag;
    private boolean dontFragmentFlag;

    // 传输层 : TCP/UDP
    private int srcPort;
    private int dstPort;
    private long seqNumber;
    private long ackNumber;
    private int windowSize;
    private int length;
    private short checksum;

    // TCP标志位
    private boolean syn;
    private boolean ack;
    private boolean fin;
    private boolean rst;
    private boolean psh;
    private boolean urg;

    // ICMP信息
    private short icmpType;
    private short icmpCode;

    // 协议负载特征
    private String payload;



    public int getPacketSize() { return packetSize; }
    public void setPacketSize(int packetSize) { this.packetSize = packetSize; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSrcMac() { return srcMac; }
    public void setSrcMac(String srcMac) { this.srcMac = srcMac; }

    public String getDstMac() { return dstMac; }
    public void setDstMac(String dstMac) { this.dstMac = dstMac; }

    public String getEtherType() { return etherType; }
    public void setEtherType(String etherType) { this.etherType = etherType; }

    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }

    public String getDstIp() { return dstIp; }
    public void setDstIp(String dstIp) { this.dstIp = dstIp; }

    public int getIpVersion() { return ipVersion; }

    public void setIpVersion(int ipVersion) { this.ipVersion = ipVersion; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }

    public int getFragmentOffset() { return fragmentOffset; }
    public void setFragmentOffset(int fragmentOffset) { this.fragmentOffset = fragmentOffset; }

    public boolean isMoreFragmentFlag() { return moreFragmentFlag; }
    public void setMoreFragmentFlag(boolean moreFragmentFlag) { this.moreFragmentFlag = moreFragmentFlag; }

    public boolean isDontFragmentFlag() { return dontFragmentFlag; }
    public void setDontFragmentFlag(boolean dontFragmentFlag) { this.dontFragmentFlag = dontFragmentFlag; }



    public int getSrcPort() { return srcPort; }
    public void setSrcPort(int srcPort) { this.srcPort = srcPort; }

    public int getDstPort() { return dstPort; }
    public void setDstPort(int dstPort) { this.dstPort = dstPort; }

    public long getSeqNumber() { return seqNumber; }
    public void setSeqNumber(long sequenceNumber) { this.seqNumber = sequenceNumber; }

    public long getAckNumber() { return ackNumber; }

    public void setAckNumber(long ackNumber) { this.ackNumber = ackNumber; }

    public int getWindowSize() { return windowSize; }
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public short getChecksum() { return checksum; }
    public void setChecksum(short checksum) { this.checksum = checksum; }

    public boolean isSyn() { return syn; }
    public void setSyn(boolean syn) { this.syn = syn; }

    public boolean isAck() { return ack; }
    public void setAck(boolean ack) { this.ack = ack; }

    public boolean isFin() { return fin; }
    public void setFin(boolean fin) { this.fin = fin; }

    public boolean isRst() { return rst; }
    public void setRst(boolean rst) { this.rst = rst; }

    public boolean isPsh() { return psh; }
    public void setPsh(boolean psh) { this.psh = psh; }

    public boolean isUrg() { return urg; }
    public void setUrg(boolean urg) { this.urg = urg; }

    public int getIcmpType() { return icmpType; }

    public void setIcmpType(short icmpCode) { this.icmpType = icmpType; }

    public int getIcmpCode() { return icmpCode; }
    public void setIcmpCode(short icmpCode) { this.icmpCode = icmpCode; }

    public String getPayload() { return payload; }

    public void setPayload(String payload) { this.payload = payload; }

    public static long getSerialVersionUID() { return serialVersionUID; }

    //分片
    public String getFragmentFlags() {
        return String.format("DF:%b MF:%b",
                dontFragmentFlag, moreFragmentFlag);
    }

//    public void setFragmentFlags(String fragmentFlags) {
//        FragmentFlags = fragmentFlags;
//    }

    // 校验和验证方法
    public boolean isUdpChecksumValid() {
        if ("UDP".equals(protocol)) {
            return checksum == 0xFFFF;
        }
        return true;
    }
}
