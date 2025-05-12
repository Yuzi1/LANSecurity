package Meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 主机统计数据
public class HostStats {
    private Set<String> destinations = new HashSet<>();
    private Set<Integer> ports = new HashSet<>();
    private Map<String, Integer> connectionCount = new HashMap<>();

    public void update(String destination, int port) {
        destinations.add(destination);
        ports.add(port);
        connectionCount.put(destination, connectionCount.getOrDefault(destination, 0) + 1);
    }

    public int getUniqueDestCount() {
        return destinations.size();
    }

    public int getUniquePortCount() {
        return ports.size();
    }

    public int getConnectionCount() {
        return connectionCount.values().stream().mapToInt(Integer::intValue).sum();
    }
}