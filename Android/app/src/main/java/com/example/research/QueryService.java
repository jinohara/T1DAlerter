package com.example.research;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Instance;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class QueryService extends IntentService {

    private Vector<String> last11;
    private Vector<Classifier> SVMs;
    private SVMMethods methodObject;
    private setsMeanStdDev holdInfo;

    private String urlString;

    public QueryService() {

        super("MongoLabService");
        this.urlString = "https://api.mongolab.com/api/1/databases/jcostik-nightscout/collections/"
                + "entries?apiKey=CR4PAAj5PmApVtW6XKHTGp8sMkmug76a&s={%22date%22:-1}";

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

    }

    public QueryService(String URL) {

        super(URL);

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

    }


    protected void onHandleIntent(Intent intent) {
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);
        ArrayList<String> result = new ArrayList<String>();
        String line;

        int type = intent.getIntExtra("type", 0);
        Bundle b = new Bundle();

        if (type > 0) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(
                        urlString).openConnection();
                urlConnection.connect();
                Scanner database = new Scanner(new InputStreamReader(
                        urlConnection.getInputStream()));
                database.useDelimiter("\"_id\"");
                line = database.next();
                //TRAIN / RETRAIN
                if (type == 2) {
                    for (int i = 0; database.hasNext(); i++) {
                        line = database.next();
                        result.add(line);
                    }
                    train(result);
                }
                else {

                    for (int i = 0; i < 1 && database.hasNext(); i++) {
                        line = database.next();
                        result.add(line);
                    }
                    normalRun(result);
                }

            } catch (Exception e) {
                Log.d("issue", e.getMessage());

            }
        }
    }

    private void train(ArrayList<String> result) {
        Log.d("Train", "" + result.get(0));
        Vector<String> temp = new Vector<String>(result);

        for (int i = 11; i >= 0; --i) {
            last11.add(temp.get(i));
        }
        holdInfo = methodObject.produceDataSets(temp, 80, 160);
        SVMs = methodObject.trainSVM(holdInfo.sets.get(0), holdInfo.sets.get(1));

        Instance toTest = methodObject.makeInstance(last11, temp.get(0), holdInfo);
    }

    private void normalRun(ArrayList<String> result) {
        Log.d("NormalRun", "" + result.get(0));
        Instance toClassify = methodObject.makeInstance(last11, result.get(0), holdInfo);
        //I'm not sure what you want to do with these values,
        //they are either true or false, an alert would happen if one returned true
        Object classificationHigh = methodObject.classify(SVMs.get(0), toClassify);
        Object classificationLow = methodObject.classify(SVMs.get(1), toClassify);
        //increment the last11
        for (int i = 10; i > 0; --i) {
            last11.set(i, last11.get(i - 1));
        }
        last11.set(0, (String) result.get(0));
    }


}

