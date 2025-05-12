package process.Clean;

import Meta.PacketInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DataCleanService {
    private static final int MAX_PACKET_SIZE = 65535;

    public PacketInfo cleanPacket(PacketInfo packet) {
        // 检查数据包大小是否合法
        if (packet.getPacketSize() <= 0 || packet.getPacketSize() > MAX_PACKET_SIZE) {
            return null;
        }

        // 标准化IP地址格式
        packet.setSrcIp(normalizeIpAddress(packet.getSrcIp()));
        packet.setDstIp(normalizeIpAddress(packet.getDstIp()));

        // 协议名称映射
        packet.setProtocol(normalizeProtocol(packet.getProtocol()));

        return packet;
    }

    private String normalizeIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "0.0.0.0";
        }

        // 处理IPv4地址
        if (ip.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            return ip;
        }

        // 处理IPv6地址 - 简化表示
        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            return "0.0.0.0";
        }
    }

    private String normalizeProtocol(String protocol) {
        if (protocol == null) {
            return "UNKNOWN";
        }

        // 将数值协议映射为名称
        switch (protocol) {
            case "1":
            case "ICMP":
                return "ICMP";
            case "6":
            case "TCP":
                return "TCP";
            case "17":
            case "UDP":
                return "UDP";
            default:
                return protocol;
        }
    }
}
