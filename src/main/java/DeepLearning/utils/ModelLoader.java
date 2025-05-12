package DeepLearning.utils;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class ModelLoader {
    public static MultiLayerNetwork loadModel(String modelPath) throws IOException {
        return ModelSerializer.restoreMultiLayerNetwork(new File(modelPath));
    }
}
