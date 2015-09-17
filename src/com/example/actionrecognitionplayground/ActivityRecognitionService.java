package com.example.actionrecognitionplayground;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Config;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionService extends IntentService{

	public static final String TAG="demo1";
	public static final String BROADCAST_ACTION="BROADCAST_ACTION";
	
	public ActivityRecognitionService() {
		super("ActivityRecognitionService");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG,"service onstart");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (ActivityRecognitionResult.hasResult(intent)) {
			Intent broadcastCall=new Intent(BROADCAST_ACTION);
			
			ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
				
			DetectedActivity detectedActivity=result.getMostProbableActivity();
			ArrayList<DetectedActivity> activities=(ArrayList)result.getProbableActivities();
			int confidence=detectedActivity.getConfidence();
			int activityType=detectedActivity.getType();
			
			//generateNotification("Activity Recognition", getFriendlyName(activityType));
			Log.d(TAG,"activity detected");
			for(DetectedActivity da:activities){
				Log.d(TAG,da.getType()+" "+"confidence: "+da.getConfidence());
			}
			
			//broadcastCall.putExtra("activities", activities);
			broadcastCall.putExtra("type", getFriendlyName(activityType));
			//LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastCall);
			sendBroadcast(broadcastCall);
		}
	}
	
	private static String getFriendlyName(int detected_activity_type){
		switch (detected_activity_type ) {
			case DetectedActivity.IN_VEHICLE:
					return "in vehicle";
			case DetectedActivity.ON_BICYCLE:
					return "on bike";
			case DetectedActivity.ON_FOOT:
					return "on foot";
			case DetectedActivity.TILTING:
					return "tilting";
			case DetectedActivity.STILL:
					return "still";
			default:
					return "unknown";
			}
		}
	private void generateNotification(String title, String content) {
        long when = System.currentTimeMillis();
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("title", title);
        notifyIntent.putExtra("content", content);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.dac_logo)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(when);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) when, builder.build());
    }
}


