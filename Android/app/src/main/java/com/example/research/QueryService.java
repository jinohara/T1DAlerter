package com.example.research;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.sf.javaml.core.Instance;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class QueryService extends IntentService {

    private ArrayList<String> bob;

 String urlString;

    public QueryService() {
        super("MongoLabService");
        this.urlString = "https://api.mongolab.com/api/1/databases/jcostik-nightscout/collections/entries?apiKey=CR4PAAj5PmApVtW6XKHTGp8sMkmug76a&s={%22date%22:-1}";
    }

    public QueryService(String URL) {
        super(URL);
    }

    protected void onHandleIntent(Intent intent) {
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);
        String line;
        int type = intent.getIntExtra("type", 0);
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
                        InitActivity.result.add(line);
                    }
                    train();
                    Log.d("TRAIN", line);
                }
                else {
                    for (int i = 0; i < 1 && database.hasNext(); i++) {
                        line = database.next();
                        InitActivity.result.add(line);
                    }
                    normalRun();
                    Log.d("NORMAL", line);
                }

            } catch (Exception e) {
                Log.d("issue", e.toString());

            }
        }

    private void train() {

        for (int i = 11; i >= 0; --i) {
            InitActivity.last11.add(InitActivity.result.get(i));

        }
        InitActivity.holdInfo = InitActivity.methodObject.produceDataSets(InitActivity.LOW,
                InitActivity.HIGH);
        InitActivity.SVMs = InitActivity.methodObject.trainSVM(InitActivity.holdInfo.sets.get(0),
                InitActivity.holdInfo.sets.get(1));

        final double data [] =  InitActivity.methodObject.getDataSGV(InitActivity.last11,
                InitActivity.result.get(0));
        InitActivity.graph(data, 0);

        Instance toTest = InitActivity.methodObject.makeInstance(data, InitActivity.holdInfo);
    }

    private void normalRun() {


        final double data [] =  InitActivity.methodObject.getDataSGV(InitActivity.last11,
                        InitActivity.result.get(0));
        Instance toClassify = InitActivity.methodObject.makeInstance(data, InitActivity.holdInfo);

        //DANNY LOOK HEREE
        //HIGH
        if (InitActivity.methodObject.classify(InitActivity.SVMs.get(0), toClassify)) {
            Twilio.httpMessage("HIGH");
        //    Yo.sendMessage("OMGITSANJANAA");
            InitActivity.graph(data, 1);

        }
        //LOW
        else if (InitActivity.methodObject.classify(InitActivity.SVMs.get(1), toClassify)) {
        //    Yo.sendMessage("OMGITSANJANAA");
            Twilio.httpMessage("LOW");
            InitActivity.graph(data, -1);
        }

        else
            InitActivity.graph(data, 0);

        //increment the last11
        for (int i = 10; i > 0; --i) {
            InitActivity.last11.set(i, InitActivity.last11.get(i - 1));
        }
        InitActivity.last11.set(0, (String) InitActivity.result.get(0));
    }

}

