package Capture.Service;
import Meta.PacketInfo;
import Capture.Parser.PacketParser;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import Netty.Client.NettyClient;

import java.util.concurrent.*;


//打开接口并设置过滤器
public class PacketCaptureService {
    private static final int SNAPLEN = 65535;
    private static final int READ_TIMEOUT = 100;
    private PcapHandle handle;
    private ExecutorService executor;
    private volatile boolean running = false;

    public void startCapture(PcapNetworkInterface nif, String filter)
            throws PcapNativeException, NotOpenException {
        if (running) {
            return;
        }

        // 打开接口
        handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

        // 设置过滤器
        if (filter != null && !filter.isEmpty()) {
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        // 启动捕获线程
        executor = Executors.newSingleThreadExecutor();
        running = true;
        executor.submit(this::capturePackets);
    }

    private void capturePackets() {
        try {
            while (running) {
                try {
                    Packet packet = handle.getNextPacketEx();
                    processPacket(packet);
                } catch (TimeoutException e) {
                    // 超时继续
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }

    private void processPacket(Packet packet) {
        // 解析数据包
        PacketParser pr = new PacketParser();
        PacketInfo packetInfo = pr.parse(packet);

        // 发送到数据处理层
        NettyClient.getInstance().sendPacketInfo(packetInfo);
    }

    public void stopCapture() {
        running = false;
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (handle != null && handle.isOpen()) {
            handle.close();
        }
    }
}
