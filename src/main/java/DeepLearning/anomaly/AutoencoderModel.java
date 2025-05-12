package DeepLearning.anomaly;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class AutoencoderModel {
    private MultiLayerNetwork model;

    public void Autoencoder(int inputSize) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(inputSize).nOut(32)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(32).nOut(16)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder()
                        .nIn(16).nOut(inputSize)
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .activation(Activation.SIGMOID)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
    }

    public void train(DataSet dataset, int epochs) {
        for (int i = 0; i < epochs; i++) {
            model.fit(dataset);
        }
    }

    public double detectAnomaly(double[] input) {
        INDArray features = Nd4j.create(input);
        INDArray reconstructed = model.output(features);
        return reconstructed.distance2(features);
    }
}
