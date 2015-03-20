package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHLight.PHLightAlertMode;

import java.io.InputStream;
import java.io.IOException;

/**
 * MyApplicationActivity
 *
 * @author Danny Sanchez
 *
 */
public class MyApplicationActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
        Button randomButton, veryLowButton, lowButton, mediumButton, highButton, veryHighButton;
        randomButton = (Button) findViewById(R.id.buttonRand);
        randomButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                randomLights();
            }
        });

        veryLowButton=(Button) findViewById(R.id.buttonVeryLow);
        veryLowButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkValue(1);
            }
        });

        lowButton=(Button) findViewById(R.id.buttonLow);
        lowButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkValue(2);
            }
        });

        mediumButton=(Button) findViewById(R.id.buttonMedium);
        mediumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkValue(3);
            }
        });

        highButton=(Button) findViewById(R.id.buttonHigh);
        highButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkValue(4);
            }
        });

        veryHighButton=(Button) findViewById(R.id.buttonVeryHigh);
        veryHighButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkValue(5);
            }
        });

    }
    public void checkValue(int value) {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light: allLights){
            PHLightState lightState = new PHLightState();

            if (value==1) {

                //lightState.setTransitionTime(20);
                lightState.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
                lightState.setHue(0);
                bridge.updateLightState(light, lightState, listener);
            }

            else if (value==2){
                lightState.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
                lightState.setHue(10000);
                bridge.updateLightState(light, lightState, listener);
            }
            else if (value==3){
                lightState.setHue(36210);
                bridge.updateLightState(light, lightState, listener);
            }
            else if (value==4) {
                lightState.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
                lightState.setHue(10000);
                bridge.updateLightState(light, lightState, listener);
            }
            else if (value==5) {
                lightState.setAlertMode(PHLightAlertMode.ALERT_LSELECT);
                lightState.setHue(0);
                bridge.updateLightState(light, lightState, listener);
            }
        }

    }


    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();
        
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }
    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };
    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
