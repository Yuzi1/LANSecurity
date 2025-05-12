package Meta;

//// 基线数据结构
public class Baseline {
    private String metricKey;
    private double mean;
    private double stdDev;
    private long sampleCount;
    private long lastUpdated;

    public Baseline(String metricKey, double mean, double stdDev, long sampleCount, long lastUpdated) {
        this.metricKey = metricKey;
        this.mean = mean;
        this.stdDev = stdDev;
        this.sampleCount = sampleCount;
        this.lastUpdated = lastUpdated;
    }


    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(long sampleCount) {
        this.sampleCount = sampleCount;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
