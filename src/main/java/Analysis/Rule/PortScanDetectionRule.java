package Analysis.Rule;

import Meta.PacketInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 端口扫描检测规则
public class PortScanDetectionRule extends AbstractRule {
    private static final int SCAN_THRESHOLD = 15; // 扫描端口阈值
    private static final long TIME_WINDOW = 60000; // 时间窗口(1分钟)

    private Map<String, PortScanTracker> scanTrackers = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanupService;

    public PortScanDetectionRule() {
        super("RULE_PORT_SCAN", "Port Scan Detection",
                "Detects port scanning behavior from a single source IP", 200);

        // 启动清理任务，移除过期的跟踪器
        cleanupService = Executors.newSingleThreadScheduledExecutor();
        cleanupService.scheduleAtFixedRate(this::cleanupExpiredTrackers, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        PacketInfo packet = context.getFact("packet", PacketInfo.class);
        if (packet == null) {
            return RuleResult.notTriggered(getId(), getName());
        }

        // 仅处理TCP和UDP数据包
        if (!"TCP".equals(packet.getProtocol()) && !"UDP".equals(packet.getProtocol())) {
            return RuleResult.notTriggered(getId(), getName());
        }

        String srcIp = packet.getSrcIp();
        String dstIp = packet.getDstIp();
        int dstPort = packet.getDstPort();

        // 获取或创建扫描跟踪器
        PortScanTracker tracker = scanTrackers.computeIfAbsent(
                srcIp, k -> new PortScanTracker(srcIp)
        );

        // 记录目标端口访问
        boolean scanDetected = tracker.addPortAccess(dstIp, dstPort);

        if (scanDetected) {
            // 获取扫描详情
            Map<String, Set<Integer>> portsByTarget = tracker.getPortsByTarget();
            int totalUniqueTargets = portsByTarget.size();
            int totalUniquePorts = tracker.getTotalUniquePorts();
            String scanType = tracker.isSingleTargetScan() ? "vertical (single host)" : "horizontal (multiple hosts)";

            String message = String.format(
                    "Port scan detected from %s: %s scan, %d unique ports across %d targets within %d seconds",
                    srcIp, scanType, totalUniquePorts, totalUniqueTargets, TIME_WINDOW / 1000);

            // 创建结果
            RuleResult result = RuleResult.triggered(getId(), getName(), message, 4);
            result.addAdditionalInfo("sourceIp", srcIp);
            result.addAdditionalInfo("scanType", scanType);
            result.addAdditionalInfo("uniquePorts", totalUniquePorts);
            result.addAdditionalInfo("uniqueTargets", totalUniqueTargets);
            result.addAdditionalInfo("portsByTarget", portsByTarget);

            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }

    private void cleanupExpiredTrackers() {
        long currentTime = System.currentTimeMillis();
        scanTrackers.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getLastAccessTime() > TIME_WINDOW * 2);
    }

    public void shutdown() {
        if (cleanupService != null) {
            cleanupService.shutdown();
        }
    }

    // 端口扫描跟踪器
    private static class PortScanTracker {
        private String sourceIp;
        private Map<String, Set<Integer>> portsByTarget = new HashMap<>();
        private long lastAccessTime;
        private long firstAccessTime;

        public PortScanTracker(String sourceIp) {
            this.sourceIp = sourceIp;
            this.lastAccessTime = System.currentTimeMillis();
            this.firstAccessTime = this.lastAccessTime;
        }

        public boolean addPortAccess(String targetIp, int port) {
            lastAccessTime = System.currentTimeMillis();

            // 如果超过时间窗口，重置跟踪数据
            if (lastAccessTime - firstAccessTime > TIME_WINDOW) {
                portsByTarget.clear();
                firstAccessTime = lastAccessTime;
            }

            // 记录目的IP和端口
            Set<Integer> ports = portsByTarget.computeIfAbsent(
                    targetIp, k -> new HashSet<>()
            );
            ports.add(port);

            // 判断是否为扫描行为
            return isScanDetected();
        }

        private boolean isScanDetected() {
            int totalUniquePorts = getTotalUniquePorts();

            // 单主机多端口扫描（垂直扫描）
            if (portsByTarget.size() == 1 && totalUniquePorts >= SCAN_THRESHOLD) {
                return true;
            }

            // 多主机相同/少量端口扫描（水平扫描）
            if (portsByTarget.size() >= SCAN_THRESHOLD && totalUniquePorts < portsByTarget.size() * 2) {
                return true;
            }

            return false;
        }

        public boolean isSingleTargetScan() {
            return portsByTarget.size() == 1;
        }

        public int getTotalUniquePorts() {
            Set<Integer> allPorts = new HashSet<>();
            for (Set<Integer> ports : portsByTarget.values()) {
                allPorts.addAll(ports);
            }
            return allPorts.size();
        }

        public Map<String, Set<Integer>> getPortsByTarget() {
            return new HashMap<>(portsByTarget);
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }
    }
}
