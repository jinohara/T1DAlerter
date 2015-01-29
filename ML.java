import java.io.*;
import java.util.*;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.classification.Classifier;
import libsvm.LibSVM;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.classification.AbstractClassifier;
public class ML
{
    public static void main(String[] args) throws IOException, FileNotFoundException
    {
        FileReader fr;
        String wholeFile;
        fr = new FileReader("sgvNums");
        
        BufferedReader textReader = new BufferedReader(fr);
        wholeFile = textReader.readLine();
        
        Vector<Integer> dateTimeIdxs = new Vector<>();
        int position = wholeFile.indexOf("dateString");
        while(position >= 0)
        {
            dateTimeIdxs.add(position);
            position = wholeFile.indexOf("dateString", position+1);
        }
        
        DexComReading garbage = new DexComReading(0,"empty");
        Vector<DexComReading> dexReadings = new Vector<>();
        for(int idx : dateTimeIdxs)
        {
            int sgvVal = garbage.strToSgvVal(wholeFile, idx);
            String slope = garbage.strToDirection(wholeFile, idx);
            DexComReading curReading = new DexComReading(sgvVal, slope);
            dexReadings.add(curReading);
        }
        Vector<Boolean> dangerListHigh = new Vector<>();
        Vector<Boolean> dangerListLow = new Vector<>(); 
        for(int i=0; i<10; ++i)
        {
            dangerListHigh.add(false);
            dangerListLow.add(false);
        }
        for(int i=10; i<=dexReadings.size()-6;++i)
        {
            if(dexReadings.get(i+5).getSgv()>160)
            {
                dangerListHigh.add(true);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+5).getSgv()>100)
            {
                dangerListHigh.add(false);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+5).getSgv()<100)
            {
                dangerListLow.add(true);
                dangerListHigh.add(false);
            }
        }
        double [][] sets10 = new double[dangerListHigh.size()][10];
        for(int i=10; i<=dexReadings.size()-6;++i)
        {
            double [] set10 = new double[10];
            for(int j=0; j<10; ++j)
            {
                set10[9-j]=dexReadings.get(i-j).getDoubleSgv();
            }
            sets10[i-10]=set10;
        }

        Dataset dataHigh = new DefaultDataset();
        Dataset dataLow = new DefaultDataset();
        for(int i=0; i<=sets10.length-201; ++i)
        {
            Instance instanceWClassValueHigh=new DenseInstance(sets10[i], 
                    dangerListHigh.get(i));
            Instance instanceWClassValueLow = new DenseInstance(sets10[i], 
                    dangerListLow.get(i));
            dataHigh.add(instanceWClassValueHigh);
            dataLow.add(instanceWClassValueLow);
        }
        
        Dataset dataTestHigh = new DefaultDataset();
        Dataset dataTestLow = new DefaultDataset();
        for(int i=sets10.length-200; i<sets10.length; ++i)
        {
            Instance instanceWClassValueHigh=new DenseInstance(sets10[i], 
                    dangerListHigh.get(i));
            Instance instanceWClassValueLow = new DenseInstance(sets10[i],
                    dangerListLow.get(i));
            dataTestLow.add(instanceWClassValueLow);
            dataTestHigh.add(instanceWClassValueHigh);
        }

        Classifier svmHigh = new LibSVM();
        svmHigh.buildClassifier(dataHigh);
        Classifier svmLow = new LibSVM();
        svmLow.buildClassifier(dataLow); 
        
        Map<Object, PerformanceMeasure> pmHigh = EvaluateDataset.testDataset(svmHigh,
                dataTestHigh);
        Map<Object, PerformanceMeasure> pmLow = EvaluateDataset.testDataset(svmLow, 
                dataTestLow);
        for(Object o : pmHigh.keySet())
        {
            System.out.println(o + ": " + pmHigh.get(o).tp + " "+ pmHigh.get(o).fp);
        }
    }
}
