package DeepLearning.utils;

import Meta.TimeSeriesData;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class TensorUtils {
    // 将流量特征转换为自编码器输入
    public static INDArray convertToAutoencoderInput(Map<String, Double> stats) {
        double[] values = stats.values().stream().mapToDouble(Double::doubleValue).toArray();
        return Nd4j.create(values).reshape(1, values.length);
    }

    // 将时间序列数据转换为LSTM输入
    public static INDArray convertToLSTMInput(List<TimeSeriesData> series) {
        double[][] data = new double[1][series.size()];
        for (int i = 0; i < series.size(); i++) {
            data[0][i] = series.get(i).getValue();
        }
        return Nd4j.create(data).reshape(1, series.size(), 1);
    }

    // 将载荷字节转换为CNN输入
    public static INDArray convertToCNNInput(byte[] payload) {
        double[] normalized = new double[payload.length];
        for (int i = 0; i < payload.length; i++) {
            normalized[i] = (payload[i] & 0xFF) / 255.0;
        }
        return Nd4j.create(normalized).reshape(1, 1, normalized.length);
    }
}
