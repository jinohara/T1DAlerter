package com.example.research;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver {
	public interface Receiver {
		 public void onReceiveResult(int resultCode, Bundle resultData);
	}

	public MyResultReceiver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	private MyResultReceiver mReceiver;

	public void setReceiver(MyResultReceiver receiver) {
		mReceiver = receiver;
	}

	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (mReceiver != null && resultCode != 0) {
			//ADD CODE TO CALL EVAN's CODE
		}

	}
}
