package Capture.Parser;
import Meta.PacketInfo;
import org.pcap4j.packet.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


//数据包解析
public class PacketParser {

    private final Map<String, List<IpV4Packet>> fragmentCache = new ConcurrentHashMap<>();
    public PacketInfo parse(Packet packet) {
        PacketInfo info = new PacketInfo();

        // 解析以太网帧
        if (packet.contains(EthernetPacket.class)) {
            EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
            info.setSrcMac(ethernetPacket.getHeader().getSrcAddr().toString());
            info.setDstMac(ethernetPacket.getHeader().getDstAddr().toString());
            info.setEtherType(ethernetPacket.getHeader().getType().valueAsString());
        }

        // 解析IP包
        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);
            info.setSrcIp(ipPacket.getHeader().getSrcAddr().getHostAddress());
            info.setDstIp(ipPacket.getHeader().getDstAddr().getHostAddress());
            info.setProtocol(ipPacket.getHeader().getProtocol().valueAsString());


            // 区分IPv4和IPv6
            if (ipPacket instanceof IpV4Packet) {
                IpV4Packet ipv4Packet = (IpV4Packet) ipPacket;
                IpV4Packet.IpV4Header header = ipv4Packet.getHeader();
                info.setIpVersion(4);
                info.setTtl(header.getTtl());
                info.setFragmentOffset(header.getFragmentOffset());
                info.setMoreFragmentFlag(header.getMoreFragmentFlag());
                info.setDontFragmentFlag(header.getDontFragmentFlag());

                // 分片处理逻辑
                if (header.getFragmentOffset() > 0 || header.getMoreFragmentFlag()){
                    return handleIpv4Fragmentation(ipv4Packet, info);
                }

            } else if (ipPacket instanceof IpV6Packet) {
                IpV6Packet ipv6Packet = (IpV6Packet) ipPacket;
                IpV6Packet.IpV6Header header = ipv6Packet.getHeader();
                info.setIpVersion(6);
                info.setTtl(header.getHopLimit());
            }
        }

        // 传输层解析
        parseTransportLayer(packet, info);

        // 负载解析
        if (packet.getPayload() != null) {
            info.setPayload(new String(
                    packet.getPayload().getRawData(),
                    StandardCharsets.UTF_8
            ));
        }

        // 添加时间戳
        info.setTimestamp(System.currentTimeMillis());
        info.setPacketSize(packet.length());

        return info;
    }

    private PacketInfo handleIpv4Fragmentation(IpV4Packet packet, PacketInfo info) {
        String cacheKey = packet.getHeader().getIdentification() +
                "@" + packet.getHeader().getSrcAddr();

        List<IpV4Packet> fragments = fragmentCache.computeIfAbsent(
                cacheKey, k -> new ArrayList<>()
        );
        fragments.add(packet);

        if (!packet.getHeader().getMoreFragmentFlag()) {
            try {
                IpV4Packet reassembled = reassembleFragments(fragments);
                fragmentCache.remove(cacheKey);
                return parse(reassembled); // 重新解析重组后的完整包
            } catch (Exception e) {
                return null; // 保持原有异常处理方式
            }
        }
        return null;
    }

    private IpV4Packet reassembleFragments(List<IpV4Packet> fragments) throws IllegalRawDataException {
        fragments.sort(Comparator.comparingInt(
                p -> p.getHeader().getFragmentOffset()
        ));

        byte[] buffer = new byte[65535];
        int offset = 0;
        for (IpV4Packet frag : fragments) {
            byte[] data = frag.getPayload().getRawData();
            System.arraycopy(data, 0, buffer, offset, data.length);
            offset += data.length;
        }

        return IpV4Packet.newPacket(buffer, 0, offset);
    }

    // 传输层解析
    private void parseTransportLayer(Packet packet, PacketInfo info){
        // 解析TCP包
        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            TcpPacket.TcpHeader header = tcpPacket.getHeader();

            info.setSrcPort(header.getSrcPort().valueAsInt());
            info.setDstPort(header.getDstPort().valueAsInt());
            info.setSeqNumber(header.getSequenceNumber());
            info.setAckNumber(header.getAcknowledgmentNumber());
            info.setWindowSize(header.getWindowAsInt());

            // TCP标志位
            info.setSyn(header.getSyn());
            info.setAck(header.getAck());
            info.setFin(header.getFin());
            info.setRst(header.getRst());
            info.setPsh(header.getPsh());
            info.setUrg(header.getUrg());
        }

        // 解析UDP包
        if (packet.contains(UdpPacket.class)) {
            UdpPacket udpPacket = packet.get(UdpPacket.class);
            UdpPacket.UdpHeader header = udpPacket.getHeader();

            info.setSrcPort(header.getSrcPort().valueAsInt());
            info.setDstPort(header.getDstPort().valueAsInt());
            info.setLength(header.getLength());
            info.setChecksum(header.getChecksum());
        }

        // 解析ICMP包
        if (packet.contains(IpV4Packet.class)) {
            IpV4Packet ipv4Packet = packet.get(IpV4Packet.class);
            if (ipv4Packet.getPayload() instanceof IcmpV4CommonPacket) {
                IcmpV4CommonPacket icmpV4Packet = (IcmpV4CommonPacket) ipv4Packet.getPayload();
                IcmpV4CommonPacket.IcmpV4CommonHeader header = icmpV4Packet.getHeader();
                info.setIcmpType(header.getType().value());
                info.setIcmpCode(header.getCode().value());
            }
        }
        else if (packet.contains(IpV6Packet.class)) {
            IpV6Packet ipv6Packet = packet.get(IpV6Packet.class);
            if (ipv6Packet.getPayload() instanceof IcmpV6CommonPacket) {
                IcmpV6CommonPacket icmpV6Packet = (IcmpV6CommonPacket) ipv6Packet.getPayload();
                IcmpV6CommonPacket.IcmpV6CommonHeader header = icmpV6Packet.getHeader();
                info.setIcmpType(header.getType().value());
                info.setIcmpCode(header.getCode().value());
            }
        }
    }
}
