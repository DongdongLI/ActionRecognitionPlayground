package com.example.actionrecognitionplayground;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private static final String TAG="demo1";
	
	private TextView textView;
	private Button startBtn;
	private Button stopBtn;
	
	GoogleApiClient googleApiClient;
	private PendingIntent pendingIntent;
	BroadcastReceiver broadcastReceiver=null;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        textView=(TextView)findViewById(R.id.textView1);
        startBtn=(Button)findViewById(R.id.startBtn);
        stopBtn=(Button)findViewById(R.id.stopBtn);
    
        buildGoogleApiClient();
        
        startBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				requestActivityUpdatesButtonHandler(v);
			}
		});
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopDetect();
				textView.setText("Unknown");
			}
		});
        
        broadcastReceiver=new BroadcastReceiver() {
        	public void onReceive(Context context, Intent intent) {
//                ArrayList<DetectedActivity> updatedActivities =
//                        intent.getParcelableArrayListExtra("activities");
//                
        		String activity=intent.getExtras().getString("type");
        		textView.setText(activity);
                Log.d(TAG,"broadcast get");
               // updateDetectedActivitiesList(updatedActivities);
            }
		};
		IntentFilter intentFilter=new IntentFilter(ActivityRecognitionService.BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	googleApiClient.connect();
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	unregisterReceiver(broadcastReceiver);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	registerReceiver(broadcastReceiver, new IntentFilter(ActivityRecognitionService.BROADCAST_ACTION));
    };
    
    // set up the googleApiClient: very important
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(TAG,"connected");
		startDetect();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.d(TAG,"connection suspend");
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(TAG, "connection fail");
	}
	
	public void requestActivityUpdatesButtonHandler(View view) {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, "API no connected",
                    Toast.LENGTH_SHORT).show();
            return;
        }
//        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
//        		googleApiClient,
//                100,
//                getActivityDetectionPendingIntent()
//        ).setResultCallback(new ResultCallback<Status>() {
//        	public void onResult(Status status) {
//                if (status.isSuccess()) {
//                     Log.d(TAG,"status is Success");
//                } else {
//                    Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
//                }
//            }
//		});
//        Intent intent =new Intent(this,ActivityRecognitionService.class);
//        startService(intent);
    }

	private PendingIntent getActivityDetectionPendingIntent() {
		if(pendingIntent==null){
			Intent i=new Intent(this,ActivityRecognitionService.class);
			return PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		}else{
			return pendingIntent;
		}
	}
	private void startDetect(){
		PendingIntent activityRecognitionIntent=getActivityDetectionPendingIntent();
		ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 0, activityRecognitionIntent);
	}
	private void stopDetect(){
		ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, getActivityDetectionPendingIntent());
	}
}
