package com.example.research;

import java.io.IOException;
import java.util.Vector;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
class CreateAndTest
{
    public static void main(String[] args) throws IOException
    {
        
        String fileName = "sgvNums";
        SVMMethods methodObject = new SVMMethods();
        int tooHigh=160;
        int tooLow=100;
        Vector<Dataset> highAndLow= methodObject.produceDataSets(fileName, tooHigh, tooLow);
        Dataset minus200High = new DefaultDataset();
        Dataset minus200Low = new DefaultDataset(); 
        Dataset last200High = new DefaultDataset();
        Dataset last200Low = new DefaultDataset();

        for(int i=0; i<highAndLow.get(0).size()-201; ++i)
        {
            minus200High.add(highAndLow.get(0).get(i));
            minus200Low.add(highAndLow.get(1).get(i));
        }
        for(int i=highAndLow.get(0).size()-201; i<highAndLow.get(0).size(); ++i)
        {
            last200High.add(highAndLow.get(0).get(i));
            last200Low.add(highAndLow.get(1).get(i));
        }

        Vector<Classifier> SVMs = methodObject.trainSVM(minus200High, minus200Low);
        methodObject.testSVMs(SVMs, last200High, last200Low);   
        System.out.println(methodObject.classify(SVMs.get(0), minus200High.get(54))); 
    }
}
