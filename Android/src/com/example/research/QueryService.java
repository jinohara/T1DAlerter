package com.example.research;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class QueryService extends IntentService {

	private int count = 1;
	private String urlString;
	private int type;

	public QueryService() {
		super("MongoLabService");
		this.type = 1;
		this.urlString = "https://api.mongolab.com/api/1/databases/jcostik-nightscout/collections/entries?apiKey=CR4PAAj5PmApVtW6XKHTGp8sMkmug76a";
		// TODO Auto-generated constructor stub
	}

	public QueryService(String URL) {
		super(URL);

		// TODO Auto-generated constructor stub
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void setType(int type){
		this.type = type;
		
	}

	protected void onHandleIntent(Intent intent) {
		; // ADD INTENT
		ArrayList<String> result = new ArrayList<String>();
		String line;

		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		int type = intent.getIntExtra("type", 0);
		Bundle b = new Bundle();
		  receiver.send(type, Bundle.EMPTY);
		  
		if (type > 0) {
			try {
				HttpURLConnection urlConnection = (HttpURLConnection) new URL(
						urlString).openConnection();
				urlConnection.connect();
				Scanner database = new Scanner(new InputStreamReader(
						urlConnection.getInputStream()));
				database.useDelimiter("\"_id\"");
				line = database.next();

				for (int i = 0; i < count && database.hasNext(); i++) {
					line = database.next();
					result.add(line);
				}
				
				  b.putStringArrayList("results", result);
	                receiver.send(type, b);

			} catch (Exception e) {
				Log.d("issue", e.getMessage());

			}
		}
	}
}
