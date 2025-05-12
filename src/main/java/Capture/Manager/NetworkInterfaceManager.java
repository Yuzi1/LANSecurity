package Capture.Manager;

import org.pcap4j.core.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

//网络接口管理器，负责发现和管理可用的网络接口
public class NetworkInterfaceManager {
    private List<PcapNetworkInterface> availableInterfaces;

    public List<PcapNetworkInterface> discoverInterfaces() throws PcapNativeException {
        availableInterfaces = Pcaps.findAllDevs();
        return availableInterfaces;
    }

    public List<PcapNetworkInterface> getEffectiveInterfaces() {
        return availableInterfaces.stream()
                .filter(this::isEffectiveInterface)
                .collect(Collectors.toList());
    }

    private boolean isEffectiveInterface(PcapNetworkInterface nif) {
        // 排除回环接口
        if (nif.isLoopBack()) {
            return false;
        }

        // 检查接口是否处于活动状态
        List<PcapAddress> addresses = nif.getAddresses();
        return addresses != null && !addresses.isEmpty();
    }

    public boolean isInterfaceUp(PcapNetworkInterface nif) {
        // 检查接口是否处于UP状态
        try {
            NetworkInterface javaNetInterface = NetworkInterface.getByName(nif.getName());
            return javaNetInterface != null && javaNetInterface.isUp();
        } catch (SocketException e) {
            return false;
        }
    }
}
