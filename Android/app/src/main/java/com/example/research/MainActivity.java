package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

    public static int HIGH;
    public static int LOW;

    public static String PATIENTNAME;
    public static String PHONENUMBER;

    EditText patientName;
    EditText phoneNumber;
    ViewFlipper vf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vf = (ViewFlipper) findViewById(R.id.vf);

        patientName = (EditText)findViewById(R.id.editText);
        phoneNumber = (EditText)findViewById(R.id.editText2);

        
        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PATIENTNAME = patientName.getText().toString();
                PHONENUMBER = phoneNumber.getText().toString();
                vf.showNext();
            }
        });

        NumberPicker lowNp = (NumberPicker) findViewById(R.id.lowNP);
        lowNp.setMaxValue(300);
        lowNp.setMinValue(0);
        lowNp.setValue(100);
        LOW = lowNp.getValue();

        NumberPicker highNp = (NumberPicker) findViewById(R.id.highNP);
        highNp.setMaxValue(300);
        highNp.setMinValue(0);
        highNp.setValue(180);
        HIGH = highNp.getValue();

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(intent);
            }
        });
    }
}
