package DateBase.DAO;

import Meta.PacketInfo;
import DateBase.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class PacketInfoDAO {
    private static final int BATCH_SIZE = 100;
    private List<PacketInfo> batchBuffer = new ArrayList<>(BATCH_SIZE);

    public void saveBatch(List<PacketInfo> packets) {
        if (packets == null || packets.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO packet_info (timestamp, src_mac, dst_mac, src_ip, dst_ip, " +
                    "ip_version, protocol, src_port, dst_port, packet_size, ttl, flags) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                conn.setAutoCommit(false);

                for (PacketInfo packet : packets) {
                    pstmt.setLong(1, packet.getTimestamp());
                    pstmt.setString(2, packet.getSrcMac());
                    pstmt.setString(3, packet.getDstMac());
                    pstmt.setString(4, packet.getSrcIp());
                    pstmt.setString(5, packet.getDstIp());
                    pstmt.setInt(6, packet.getIpVersion());
                    pstmt.setString(7, packet.getProtocol());
                    pstmt.setInt(8, packet.getSrcPort());
                    pstmt.setInt(9, packet.getDstPort());
                    pstmt.setInt(10, packet.getPacketSize());
                    pstmt.setInt(11, packet.getTtl());

                    // 构建标志位字符串
                    StringBuilder flags = new StringBuilder();
                    if (packet.isSyn()) flags.append("S");
                    if (packet.isAck()) flags.append("A");
                    if (packet.isFin()) flags.append("F");
                    if (packet.isRst()) flags.append("R");
                    if (packet.isPsh()) flags.append("P");
                    if (packet.isUrg()) flags.append("U");

                    pstmt.setString(12, flags.toString());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToBuffer(PacketInfo packet) {
        synchronized (batchBuffer) {
            batchBuffer.add(packet);

            if (batchBuffer.size() >= BATCH_SIZE) {
                List<PacketInfo> batchCopy = new ArrayList<>(batchBuffer);
                batchBuffer.clear();
                saveBatch(batchCopy);
            }
        }
    }

    public void flushBuffer() {
        synchronized (batchBuffer) {
            if (!batchBuffer.isEmpty()) {
                saveBatch(new ArrayList<>(batchBuffer));
                batchBuffer.clear();
            }
        }
    }
}
