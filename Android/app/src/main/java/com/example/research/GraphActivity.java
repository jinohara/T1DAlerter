package com.example.research;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class GraphActivity extends Activity {

    public int HIGH = 160;
    public int LOW = 80;
    private int fileIndex = 0;
    private GraphView graph;
    private Vector<String> last11;
    private Vector<Classifier> SVMs;
    private SVMMethods methodObject;
    private setsMeanStdDev holdInfo;
    private static final int TRAIN = 2;
    private static final int NORMAL = 1;
    private static final int SIZE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

        graph = (GraphView) findViewById(R.id.graph);//new GraphView(this);
        Viewport display = graph.getViewport();
        display.setMaxX(60);
        display.setMaxY(300);
        display.setBackgroundColor(getResources().getColor(
                android.R.color.holo_green_light));

        Vector<String> result = read(TRAIN, "synthData.txt");
        train(result);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run(){
                Log.d("Timer", "Ran");
                Vector<String> temp = read(NORMAL, "last10000.txt");
                normalRun(temp);

            }
        };
                timer.scheduleAtFixedRate(timerTask, 10000, 1000);

    }

    private Vector<String> read(int type, String filename){

        Vector<String> result = new Vector<String>();
        try {
            InputStream is = getAssets().open(filename);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    is));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();


            if(type == TRAIN){
                int count = 0;
                while ((inputString = inputReader.readLine()) != null && count < (SIZE /10)) {
                    result.add(inputString);
                    count++;
                }
            }
            else{

                for(int i = 0; i < fileIndex; i++)
                    inputReader.readLine();
                fileIndex++;
                result.add(inputReader.readLine());

            }
        } catch (IOException e) {
            Log.d("read", "FILE NOT FOUND");
        }
        return result;
    }


    private void train(Vector<String> result) {

        for (int i = 11; i >= 0; --i) {
            last11.add(result.get(i));
        }
        holdInfo = methodObject.produceDataSets(result, LOW, HIGH);
        SVMs = methodObject.trainSVM(holdInfo.sets.get(0), holdInfo.sets.get(1));

        double data [] =  methodObject.getDataSGV(last11, result.get(0), holdInfo);
        graph(data);

        Instance toTest = methodObject.makeInstance(data);
    }

    private void normalRun(Vector<String> result) {


        int alertVal =-1;
        Log.d("NormalRun", "" + result.get(0));
        final double data [] =  methodObject.getDataSGV(last11, result.get(0), holdInfo);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                graph(data);
            }
        });
                Instance toClassify = methodObject.makeInstance(data);


            //HIGH
            if (methodObject.classify(SVMs.get(0), toClassify)) {
                alertVal = 1;
                Twilio.httpMessage("HIGH");
            }
            //LOW
            else if (methodObject.classify(SVMs.get(1), toClassify)) {
                Twilio.httpMessage("HIGH");
                alertVal = -1;
            }

            Log.d("NormalRun", "" + alertVal);

        //increment the last11
        for (int i = 10; i > 0; --i) {
            last11.set(i, last11.get(i - 1));
        }
        last11.set(0, (String) result.get(0));
    }

    public void graph(double data []){

        DataPoint displayvals [] = new DataPoint[11];
        for(int i = 1; i < 12; i++){
            displayvals[i-1] = new DataPoint(i * 5, data[i]);
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(displayvals);
        graph.removeAllSeries();
        graph.addSeries(series);
    }

}
