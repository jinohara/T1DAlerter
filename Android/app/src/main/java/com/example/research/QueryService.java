package com.example.research;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class QueryService extends IntentService {

 String urlString;

    public QueryService() {

        super("MongoLabService");
        this.urlString = "https://api.mongolab.com/api/1/databases/jcostik-nightscout/collections/"
                + "entries?apiKey=CR4PAAj5PmApVtW6XKHTGp8sMkmug76a&s={%22date%22:-1}";
    }

    public QueryService(String URL) {
        super(URL);
    }


    protected void onHandleIntent(Intent intent) {
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);
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
                        GraphActivity.result.add(line);
                    }
                }
                else {

                    for (int i = 0; i < 1 && database.hasNext(); i++) {
                        line = database.next();
                        GraphActivity.result.add(line);
                    }
                }

            } catch (Exception e) {
                Log.d("issue", e.getMessage());

            }
        }
    }
}

