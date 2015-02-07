package com.example.research;

import java.util.ArrayList;
import java.util.Vector;

import net.sf.javaml.classification.Classifier;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver {

	private Vector<String> last11;
	private Vector<Classifier> SVMs;
	private SVMMethods garbage;

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	public MyResultReceiver(Handler handler) {
		super(handler);
		last11 = new Vector<String>();
		SVMs = new Vector<Classifier>();
		garbage = new SVMMethods();
		// TODO Auto-generated constructor stub
	}

	private MyResultReceiver mReceiver;

	public void setReceiver(MyResultReceiver receiver) {
		mReceiver = receiver;
	}

	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (mReceiver != null) {
			ArrayList<String> temp;
			temp = resultData.getStringArrayList("results");
			if (resultCode == 1) {
				temp = null;
			}
			//TRAIN / RETRAIN
			else if (resultCode == 2) {
				temp = null;

			}

		}
	}
}
