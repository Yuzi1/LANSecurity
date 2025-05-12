package Capture.Manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class InterfaceConfigManager {
    private Properties config = new Properties();
    private String configPath = "config/network.properties";

    public void loadConfig() throws IOException {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
        }
    }

    public String getPreferredInterface() {
        return config.getProperty("preferred.interface", "eth0");
    }

    public void setPreferredInterface(String interfaceName) throws IOException {
        config.setProperty("preferred.interface", interfaceName);
        try (FileOutputStream fos = new FileOutputStream(configPath)) {
            config.store(fos, "Network Interface Configuration");
        }
    }
}
