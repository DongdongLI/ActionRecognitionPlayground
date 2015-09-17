package com.example.actionrecognitionplayground;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

public class ActionRecognitionScan implements 
	GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
	public static final String TAG="demo1";
	
	private Context context;
	private static ActivityRecognition mActivityRecognition;
	private static PendingIntent callbackIntent;

	public ActionRecognitionScan(Context context){
		this.context=context;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(TAG,"connection fail");
	}

	public void onConnected(Bundle connectionHint) {
		Intent intent = new Intent(context, ActivityRecognitionService.class);
		callbackIntent = PendingIntent.getService(context, 0, intent,
		PendingIntent.FLAG_UPDATE_CURRENT);
		//mActivityRecognition.requestActivityUpdates(0, callbackIntent); // 0 sets it to update as fast as possible, just use this for testing!
		}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
