package com.example.research;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        /*
         * You can pull from the database in two ways
         * - One by a set timing (every x minutes; by default it is 5)
         * - Or by pressing the Database button
         */
        
        if(!first){
        Intent i = new Intent(getApplicationContext(), DatabaseActivity.class);
        i.putExtra("type", 1);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),3333,i,
        PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 300000, pi);
        first = false;
        }
        
        
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MainActivity.this, DatabaseActivity.class);
            	myIntent.putExtra("type", 2);
            	first = false;
            	MainActivity.this.startActivity(myIntent);
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        
        
        
    }
    
}
