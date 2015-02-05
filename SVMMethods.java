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
class SVMMethods
{
    public SVMMethods()
    {
    }

    public Vector<Dataset> produceDataSets(String fileName, int tooHigh, int tooLow) 
        throws IOException, FileNotFoundException
    {
        FileReader fr;
        String wholeFile;
        fr = new FileReader(fileName);
        
        BufferedReader textReader = new BufferedReader(fr);
        wholeFile = textReader.readLine();
        
        Vector<Integer> dateTimeIdxs = new Vector<>();
        int position = wholeFile.indexOf("dateString");
        while(position >= 0)
        {
            dateTimeIdxs.add(position);
            position = wholeFile.indexOf("dateString", position+1);
        }
        
        DexComReading garbage = new DexComReading(0,"empty", 0);
        Vector<DexComReading> dexReadings = new Vector<>();
        for(int idx : dateTimeIdxs)
        {
            int sgvVal = garbage.strToSgvVal(wholeFile, idx);
            String slope = garbage.strToDirection(wholeFile, idx);
            try
            {
                int time = garbage.strToTime(wholeFile, idx);
                DexComReading curReading = new DexComReading(sgvVal, slope, time);
                dexReadings.add(curReading);
            }
            catch(NumberFormatException e)
            {
                System.out.println("date format is different");
            }
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
            if(dexReadings.get(i+5).getSgv()>tooHigh)
            {
                dangerListHigh.add(true);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+5).getSgv()>tooLow)
            {
                dangerListHigh.add(false);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+5).getSgv()<tooLow)
            {
                dangerListLow.add(true);
                dangerListHigh.add(false);
            }
        }
        double [][] sets10 = new double[dangerListHigh.size()][11];
        for(int i=10; i<=dexReadings.size()-6;++i)
        {
            double [] set10 = new double[11];
            for(int j=0; j<10; ++j)
            {
                set10[9-j]=dexReadings.get(i-j).getDoubleSgv();
            }
            set10[10]=dexReadings.get(i).getTime();
            sets10[i-10]=set10;
        }

        Dataset dataHigh = new DefaultDataset();
        Dataset dataLow = new DefaultDataset();
        for(int i=0; i<sets10.length; ++i)
        {
            Instance instanceWClassValueHigh=new DenseInstance(sets10[i], 
                    dangerListHigh.get(i));
            Instance instanceWClassValueLow = new DenseInstance(sets10[i], 
                    dangerListLow.get(i));
            dataHigh.add(instanceWClassValueHigh);
            dataLow.add(instanceWClassValueLow);
        }
        
        Vector<Dataset> dataSets = new Vector<>();
        dataSets.add(dataHigh);
        dataSets.add(dataLow);
        return dataSets;
    }
    public Vector<Classifier> trainSVM(Dataset dataHigh, Dataset dataLow)
    {
        Classifier svmHigh = new LibSVM();
        svmHigh.buildClassifier(dataHigh);
        Classifier svmLow = new LibSVM();
        svmLow.buildClassifier(dataLow);
        
        Vector<Classifier> toReturn = new Vector<>();
        toReturn.add(svmHigh);
        toReturn.add(svmLow);
        return toReturn;
    }
    public void testSVMs(Vector<Classifier> SVMsToTest, Dataset dataHigh, Dataset dataLow)
    { 
        Map<Object, PerformanceMeasure> pmHigh = EvaluateDataset.testDataset(SVMsToTest.get(0),
                dataHigh);
        Map<Object, PerformanceMeasure> pmLow = EvaluateDataset.testDataset(SVMsToTest.get(1), 
                dataLow);
        for(Object o : pmHigh.keySet())
        {
            System.out.println(o + ": " + pmHigh.get(o).tp + " "+ pmHigh.get(o).fp);
            System.out.println("f measure: "+ pmHigh.get(o).getFMeasure());
        }
        for(Object o: pmLow.keySet())
        { 
            System.out.println(o + ": " + pmLow.get(o).tp + " "+ pmLow.get(o).fp);   
            System.out.println("f measure: "+ pmLow.get(o).getFMeasure());
        }
    }
    public Object classify(Classifier svm, Instance aRead)
    {
        return svm.classify(aRead);
    } 
}
