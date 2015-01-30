package com.example.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DatabaseActivity extends Activity {

	private final String databaseName = "jcostik-nightscout";
	private final String collectionName = "entries";
	private final String apiKey = "CR4PAAj5PmApVtW6XKHTGp8sMkmug76a";
	private final static int COUNT = 2;
	private ArrayList<String> result;

	private class MongoLab extends
			AsyncTask<String, ArrayList<String>, ArrayList<String>> {

		protected ArrayList<String> doInBackground(String... params) {

			
			String urlString = params[0];
			String line;

			try {
				HttpURLConnection urlConnection = (HttpURLConnection) new URL(
						urlString).openConnection();
				urlConnection.connect();
				Scanner database = new Scanner(new InputStreamReader(
						urlConnection.getInputStream()));
				database.useDelimiter("\"_id\"");
				line = database.next();

				for (int i = 0; i < COUNT && database.hasNext(); i++) {
					line = database.next();
					result.add(line);
					Log.d("DatabaseQuery", line);

				}
	
				 Intent myIntent = new Intent(DatabaseActivity.this,
							ResultActivity.class);
							 myIntent.putExtra("val", result);
							 DatabaseActivity.this.startActivity(myIntent);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}

			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database);
		result = new ArrayList<String>();
		String url = "https://api.mongolab.com/api/1/databases/" + databaseName
				+ "/collections/" + collectionName + "?apiKey=" + apiKey + "";
		new MongoLab().execute(url);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.database, menu);
		return true;
	}

}
