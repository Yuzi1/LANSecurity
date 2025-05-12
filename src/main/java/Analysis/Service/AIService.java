package Analysis.Service;

import DeepLearning.anomaly.AutoencoderModel;
import DeepLearning.anomaly.LSTMModel;
import DeepLearning.situation.GNNModel;
import DeepLearning.threat.CNNModel;
import DeepLearning.utils.TensorUtils;
import Meta.TimeSeriesData;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;
import java.util.Map;

public class AIService {
    private AutoencoderModel autoencoder;
    private LSTMModel lstm;
    private CNNModel cnn;
    private GNNModel gnn;

    public AIService(String aeModelPath, String lstmModelPath,
                     String cnnModelPath, String gnnModelPath) {
        this.autoencoder = new AutoencoderModel(aeModelPath);
        this.lstm = new LSTMModel(lstmModelPath);
        this.cnn = new CNNModel(cnnModelPath);
        this.gnn = new GNNModel(gnnModelPath);
    }

    // 异常检测入口
    public double detectTrafficAnomaly(Map<String, Double> stats) {
        return autoencoder.detectAnomaly(TensorUtils.convertToAutoencoderInput(stats));
    }

    // 时间序列异常检测
    public boolean isSequenceAnomaly(List<TimeSeriesData> series) {
        return lstm.isAnomaly(series);
    }

    // 载荷攻击检测
    public double detectMaliciousPayload(byte[] payload) {
        return cnn.analyzePayload(payload);
    }

    // 态势风险评估
    public double evaluateNetworkRisk() {
        // 需从数据库加载图数据
        INDArray nodes = loadGraphNodes();
        INDArray edges = loadGraphEdges();
        return gnn.evaluateRisk(nodes, edges);
    }
}
