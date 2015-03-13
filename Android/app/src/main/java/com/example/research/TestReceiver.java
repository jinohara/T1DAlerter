package com.example.research;

/**
 * Created by abhimanyumuchhal on 3/13/15.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;

public class TestReceiver extends BroadcastReceiver {

    private static WakeLock mWakeLock;
    private String LCLT;

    @Override
    public void onReceive(Context context, Intent intent) {
       // if (intent.getAction().equals(WAKELOCK_INTENT)) {
            Log.v("wakelock", "GOT THE wakelock INTENT");
            boolean on = intent.getExtras().getBoolean("on");
            if (mWakeLock == null) {
                PowerManager pm = (PowerManager) context
                        .getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "Breeze WakeLock");
            }
            if (on) {
                if (!mWakeLock.isHeld()) {
                    mWakeLock.acquire();
                    Log.v("wakelock", "acquiring wakelock");
                }
            } else {
                if (mWakeLock.isHeld()) {
                    Log.v("wakelock", "releasing wakelock");
                    mWakeLock.release();
                }
                mWakeLock = null;
            }
        //}
    }
}