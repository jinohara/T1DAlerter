package com.example.research;

import android.util.Log;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import java.lang.*;

import java.util.Map;
import java.util.Vector;

import libsvm.LibSVM;
class SVMMethods
{
    private static String TAG = "SVMMethods";
    public SVMMethods()
    {
    }

    public DexComReading produceReading(String readStr) throws NumberFormatException
    {
        DexComReading garbage = new DexComReading(0, "empty", 0);
        int position = readStr.indexOf("dateString");
        int sgvVal = garbage.strToSgvVal(readStr, position);
        String slope = garbage.strToDirection(readStr, position);
        int time = garbage.strToTime(readStr, position);
        DexComReading reading = new DexComReading(sgvVal, slope, time);
        return reading;
    }
    
    class setsMeanStdDev 
    {
        public Vector<Dataset> sets;
        public double stdDev;
        public double mean;
        public setsMeanStdDev(Vector<Dataset> setsIn, double meanIn, double stdDevIn)
        {
            this.sets = setsIn;
            this.mean = meanIn;
            this.stdDev = stdDevIn;
        }
    }

    public setsMeanStdDev produceDataSets(Vector<String> all, int tooHigh, int tooLow)
    {
        double mean;
        double standardDev;
        double standardDevTimesN;
        double sum;
        double curCount;

        DexComReading garbage = new DexComReading(0,"empty", 0);
        Vector<DexComReading> dexReadings = new Vector<DexComReading>();
        for(String curString : all)
        {
        	try
        	{
        		DexComReading newRead = produceReading(curString);
        		dexReadings.add(newRead);
        	}
        	catch(NumberFormatException e)
        	{
                Log.d("SVMMethods", "Bad Date Format");
        	}
        }
        Vector<Boolean> dangerListHigh = new Vector<Boolean>();
        Vector<Boolean> dangerListLow = new Vector<Boolean>(); 
        for(int i=0; i<12; ++i)
        {
            dangerListHigh.add(false);
            dangerListLow.add(false);
        }
        for(int i=12; i<dexReadings.size()-6; ++i)
        {

            sum = sum+dexReadings.get(i+6);
            ++curCount;
        }
        mean = sum/curCount;
        curCount = 0;
        
        for(int i=12; i<dexReadings.size()-6; ++i)
        {
            standardDevTimesN = standardDevTimesN + 
                Math.pow((dexReadings.get(i)-mean),2);
            ++curCount;
            if(dexReadings.get(i+6).getSgv()>tooHigh)
            {
                dangerListHigh.add(true);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+6).getSgv()>tooLow)
            {
                dangerListHigh.add(false);
                dangerListLow.add(false);
            }
            else if(dexReadings.get(i+6).getSgv()<tooLow)
            {
                dangerListLow.add(true);
                dangerListHigh.add(false);
            }
        }

        standardDev = standardDevTimesN/curCount;

        double [][] sets13 = new double[dangerListHigh.size()][13];
        for(int i=12; i<dexReadings.size()-6;++i)
        {
            double [] set13 = new double[13];
            for(int j=0; j<12; ++j)
            {
                set13[11-j]=(dexReadings.get(i-j).getDoubleSgv()-mean)/standardDev;
            }
            set13[12]=dexReadings.get(i).getTime();
            sets13[i-12]=set13;
        }


        Dataset dataHigh = new DefaultDataset();
        Dataset dataLow = new DefaultDataset();
        Log.d(TAG, "Value="+tooHigh);
        for(int i=0; i<sets13.length; ++i)
        {
            Instance instanceWClassValueHigh=new DenseInstance(sets13[i], 
                    dangerListHigh.get(i));
            Instance instanceWClassValueLow = new DenseInstance(sets13[i], 
                    dangerListLow.get(i));
            dataHigh.add(instanceWClassValueHigh);
            dataLow.add(instanceWClassValueLow);
        }
        Vector<Dataset> dataSets = new Vector<Dataset>();
        dataSets.add(dataHigh);
        dataSets.add(dataLow);
        setsMeanStdDev toReturn = new setsMeanStdDev(dataSets, mean, stdDev);
        return toReturn;
    }
    public Vector<Classifier> trainSVM(Dataset dataHigh, Dataset dataLow)
    {
        Classifier svmHigh = new LibSVM();
        svmHigh.buildClassifier(dataHigh);
        Classifier svmLow = new LibSVM();
        svmLow.buildClassifier(dataLow);
        
        Vector<Classifier> toReturn = new Vector<Classifier>();
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

    //!!THE LAST 11 should be in order from 5 minutes back to 55 minutes back
    Instance makeInstance(Vector<String> last11, String mostRecent, setsMeanStdDev info)
    {
        double [] set13 = new double[13];
        for(int i=0; i<11; --i)
        {
        	
            DexComReading tempReading = produceReading(last11.get(i));
            set13[i+1]=(tempReading.getDoubleSgv()-info.mean)/info.stdDev;
        }
        
        DexComReading tempReading = produceReading(mostRecent);
        set13[0]=(tempReading.getDoubleSgv()-info.mean)/info.stdDev; 
        set13[12]=tempReading.getTime();
        Instance mostRecentInst = new DenseInstance(set13, "false");
        return mostRecentInst;
    }

    public Object classify(Classifier svm, Instance aRead)
    {
        return svm.classify(aRead);
    } 
}
