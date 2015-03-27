package com.example.research;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MainActivity extends Activity {

    public int HIGH = 160;
    public int LOW = 80;
    private Vector<String> last11;
    private Vector<Classifier> SVMs;
    private SVMMethods methodObject;
    private setsMeanStdDev holdInfo;
    private static String FILENAME = "synthData.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();





//        //TODO: look up way to disable pending intent from alarm manager
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timer timer = new Timer();
                Vector<String> result = read();
                train(result);

                TimerTask timerTask = new TimerTask() {
                    public void run(){
                        normalRun(read());
                    }
                };

                timer.schedule(timerTask, 5000, 10000);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }





    private Vector<String> read(){

        Vector<String> result = new Vector<String>();
        try {
            InputStream is = getAssets().open(FILENAME);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    is));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();
            Log.d("TRAIN", "worked");
            while ((inputString = inputReader.readLine()) != null) {
                result.add(inputString);
                //Log.d("main", inputString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void train(Vector<String> result) {

        try {
            InputStream is = getAssets().open(FILENAME);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    is));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();
            Log.d("TRAIN", "worked");
            while ((inputString = inputReader.readLine()) != null) {
                result.add(inputString);
                //Log.d("main", inputString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 11; i >= 0; --i) {
            last11.add(result.get(i));
        }
        holdInfo = methodObject.produceDataSets(result, LOW, HIGH);
        SVMs = methodObject.trainSVM(holdInfo.sets.get(0), holdInfo.sets.get(1));

        Instance toTest = methodObject.makeInstance(last11, result.get(0), holdInfo);
    }

    private void normalRun(Vector<String> result) {


        int alertVal =0;

        Log.d("NormalRun", "" + result.get(0));
        Instance toClassify = methodObject.makeInstance(last11, result.get(0), holdInfo);

        //HIGH
        if (methodObject.classify(SVMs.get(0), toClassify))
            alertVal = 1;
            //LOW
        else if (methodObject.classify(SVMs.get(1), toClassify))
            alertVal = -1;

        Log.d("NormalRun", "" + alertVal);

        try {
            if (alertVal != 0) {
                Twilio.sendMessage(alertVal);

            }
        }
        catch (Exception e){}

        //increment the last11
        for (int i = 10; i > 0; --i) {
            last11.set(i, last11.get(i - 1));
        }
        last11.set(0, (String) result.get(0));
    }


}
