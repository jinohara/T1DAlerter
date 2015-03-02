package com.example.research;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    private static int NORMAL = 1;
    private static int TRAIN = 2;
    private MyResultReceiver mReceiver;
    private boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: look up way to disable pending intent from alarm manager
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	first = false;
            	MainActivity.this.startService(startIntent(TRAIN));
            }
        });

        //TODO: Look at whether it calls immediately or not
        if(!first){
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),3333,startIntent(NORMAL),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 3000, pi);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Intent startIntent(int type){
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(mReceiver);
        Intent myIntent = new Intent(MainActivity.this, QueryService.class);
        myIntent.putExtra("receiver", mReceiver);
        myIntent.putExtra("type", type);
        return myIntent;
    }
    
}
