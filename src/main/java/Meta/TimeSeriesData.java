package Meta;

// 时间序列数据
public class TimeSeriesData {
    private long timestamp;
    private String metricKey;
    private double value;

    public TimeSeriesData(long timestamp, String metricKey, double value) {
        this.timestamp = timestamp;
        this.metricKey = metricKey;
        this.value = value;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
