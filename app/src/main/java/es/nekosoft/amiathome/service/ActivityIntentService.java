package es.nekosoft.amiathome.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.nekosoft.amiathome.dao.LogMHDAO;
import es.nekosoft.amiathome.model.LogMH;
import es.nekosoft.amiathome.utils.Constants;


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
        int activity = 0;
        int maxPercent = -1;
        int maxActivity = -1;

        for (DetectedActivity da: detectedActivities) {

            //Get activity data
            activity = da.getType();
            percent = da.getConfidence();
            //Create log
            createLog(activity, percent);

            if(percent>maxPercent && activity!=DetectedActivity.TILTING) {
                maxPercent = percent;
                maxActivity = da.getType();
            }
        }

        //Send info, provided it is relevant
        if(maxPercent != -1)
            sendInfo(maxActivity, maxPercent);
    }

    private void sendInfo(int activity, int percentage){

        Intent intent = new Intent(Constants.REC_RESPONSE);
        intent.putExtra(Constants.REC_TYPE, Constants.REC_TYPE_ACT);
        intent.putExtra(Constants.REC_ACTIVITY, activity);
        intent.putExtra(Constants.REC_PERCENT, percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void createLog(int type, int per){

        String msj = getBaseContext().getString(Constants.getActivityString(type)) + " " + per + "%";
        LogMH obj = new LogMH(LogMH.TYPE_ACTIVITY, msj, new Date());
        LogMHDAO dao = new LogMHDAO(getBaseContext());
        dao.insert(obj);
    }

}
