package es.nekosoft.ejercicio02.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.nekosoft.ejercicio02.utils.Constants;


public class ActivityIntentService extends IntentService {

    public ActivityIntentService() {

        super(Constants.ACT_ACTION);
    }

    public ActivityIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        //Get possible activities
        List<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        //Get more likely activity
        int percent = 0;
        int maxPercent = 0;
        int activity = 0;

        Log.d("ActivityZ", "---- Activity Info ----");
        for (DetectedActivity da: detectedActivities) {

            percent = da.getConfidence();
            Log.d("ActivityZ", "act: " +da.getType()+ " - per: " +percent);
            if(percent>maxPercent) {
                maxPercent = percent;
                activity = da.getType();
            }
        }

        //Send info, provided it is relevant
        if(activity != DetectedActivity.ON_FOOT && activity != DetectedActivity.TILTING && activity!=0)
            sendInfo(activity, maxPercent);
    }

    private void sendInfo(int activity, int percentage){

        Intent intent = new Intent(Constants.REC_RESPONSE);
        intent.putExtra(Constants.REC_TYPE, Constants.REC_TYPE_ACT);
        intent.putExtra(Constants.REC_ACTIVITY, activity);
        intent.putExtra(Constants.REC_PERCENT, percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
