import java.io.*;
import java.util.Map;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.classification.Classifier;
import libsvm.LibSVM;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.classification.AbstractClassifier;
public class Example2
{
    public static void main(String[] args)
    {
        double[] vals1 = new double[] {1,2,3,4,5};
        double[] vals2 = new double[] {2,3,4,5,6};
        double[] vals3 = new double[] {3,4,5,6,7};
        double[] vals4 = new double[] {5,4,3,2,1};
        double[] vals5 = new double[] {6,5,4,3,2};

        Instance instanceWClassValue1 = new DenseInstance(vals1, 1);
        Instance instanceWClassValue2 = new DenseInstance(vals2, 1);
        Instance instanceWClassValue3 = new DenseInstance(vals3, 1);
        Instance instanceWClassValue4 = new DenseInstance(vals4, 0);
        Instance instanceWClassValue5 = new DenseInstance(vals5, 0);
    
        Dataset data = new DefaultDataset();
        data.add(instanceWClassValue1);
        data.add(instanceWClassValue2);
        data.add(instanceWClassValue3);
        data.add(instanceWClassValue4);
        data.add(instanceWClassValue5);

        Classifier svm = new LibSVM();
        System.out.println("created");
        svm.buildClassifier(data); 
        System.out.println("trained");

        double[] vals6 = new double[] {2,4,6,8,10};
        double[] vals7 = new double[] {10,8,6,4,2};
        Instance instanceWClassValue6 = new DenseInstance(vals6, 1);
        Instance instanceWClassValue7 = new DenseInstance(vals7,0);
        Object predictedClassValue = svm.classify(instanceWClassValue6);
        Object predictedClassValue2= svm.classify(instanceWClassValue7);
        System.out.println(predictedClassValue);
        System.out.println(predictedClassValue2);
    }
}
