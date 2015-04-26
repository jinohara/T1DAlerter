package com.example.research;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Instance;

import java.util.ArrayList;
import java.util.Vector;


public class GraphActivity extends ActionBarActivity {

    private Vector<String> last11;
    private Vector<Classifier> SVMs;
    private SVMMethods methodObject;
    private setsMeanStdDev holdInfo;
    private GraphView graph;

    public static ArrayList<String> result;

    private static final int HIGH = 180;
    private static final int LOW = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

        graph = (GraphView) findViewById(R.id.graph);
        graph_init();

        WakefulBroadcastReceiver.startWakefulService(getApplicationContext());
    }

    private void graph_init(){
        graph.setTitle("Blood Glucose (mg/dl) over the last hour");

        Viewport display = graph.getViewport();
        display.setXAxisBoundsManual(true);
        display.setYAxisBoundsManual(true);
        display.setMaxX(60);
        display.setMaxY(300);
        display.setBackgroundColor(getResources().getColor(
                android.R.color.holo_green_light));

        GridLabelRenderer labels = new GridLabelRenderer(graph);
        labels.setHorizontalLabelsVisible(true);
        labels.setVerticalLabelsVisible(true);
        labels.setVerticalAxisTitleTextSize(20);
        labels.setHorizontalAxisTitle("Last Hour");
        labels.setVerticalAxisTitle("Blood Glucose (mg/dl)");
    }

    private void train(Vector<String> result) {

        for (int i = 11; i >= 0; --i) {
            last11.add(result.get(i));
        }
        holdInfo = methodObject.produceDataSets(result, LOW, HIGH);
        SVMs = methodObject.trainSVM(holdInfo.sets.get(0), holdInfo.sets.get(1));

        final double data [] =  methodObject.getDataSGV(last11, result.get(0));
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                graph(data, 0);
            }
        });

        Instance toTest = methodObject.makeInstance(data, holdInfo);
    }

    private void normalRun(Vector<String> result) {


        final int alertVal;
        final double data [] =  methodObject.getDataSGV(last11, result.get(0));
        Instance toClassify = methodObject.makeInstance(data,holdInfo);

        //DANNY LOOK HEREE
        //HIGH
        if (methodObject.classify(SVMs.get(0), toClassify)) {
            Twilio.httpMessage("HIGH");
            Yo.sendMessage("OMGITSANJANAA");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    graph(data, 1);
                }
            });
        }
        //LOW
        else if (methodObject.classify(SVMs.get(1), toClassify)) {
            Yo.sendMessage("OMGITSANJANAA");
            Twilio.httpMessage("LOW");
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    graph(data, -1);
                }
            });
        }

        else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    graph(data, 0);
                }
            });
        }
        //increment the last11
        for (int i = 10; i > 0; --i) {
            last11.set(i, last11.get(i - 1));
        }
        last11.set(0, (String) result.get(0));
    }

    public void graph(double data [], int alertVal){

        DataPoint displayvals [] = new DataPoint[12];
        for(int i = 0; i < 12; i++){
            displayvals[i] = new DataPoint(i * 5, data[i]);
        }

//        DataPoint lowBound[] = new DataPoint[12];
//        for(int i =0; i < 12; i++)
//            lowBound[i] = new DataPoint(i*5, LOW);
//
//        DataPoint highBound[] = new DataPoint[12];
//        for(int i =0; i < 12; i++)
//            highBound[i] = new DataPoint(i*5, HIGH);

//        LineGraphSeries<DataPoint> high = new LineGraphSeries<DataPoint>(highBound);
//        LineGraphSeries<DataPoint> low = new LineGraphSeries<DataPoint>(lowBound);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(displayvals);

        graph.removeAllSeries();
        graph.addSeries(series);
//        graph.addSeries(low);
//        graph.addSeries(high);

        if(alertVal == 1)
            graph.getViewport().setBackgroundColor(getResources().getColor
                    (android.R.color.holo_red_light));
        else if (alertVal == -1){
            graph.getViewport().setBackgroundColor(getResources().getColor
                    (android.R.color.holo_orange_light));
        }
        else {
            graph.getViewport().setBackgroundColor(getResources().getColor
                    (android.R.color.holo_green_light));
        }

    }

}

