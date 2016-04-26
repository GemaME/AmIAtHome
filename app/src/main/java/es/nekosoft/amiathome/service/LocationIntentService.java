package es.nekosoft.amiathome.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

import java.util.Date;

import es.nekosoft.amiathome.dao.LogMHDAO;
import es.nekosoft.amiathome.model.LogMH;
import es.nekosoft.amiathome.utils.Constants;


public class LocationIntentService extends IntentService {


    //---- Constructor ----//

    public LocationIntentService() {

        super(Constants.LOC_ACTION);
    }

    public LocationIntentService(String name) {

        super(name);
    }


    //---- Methods ---//

    protected void onHandleIntent(Intent intent) {

        LocationResult result = LocationResult.extractResult(intent);
        if(result == null)
            return;

        Location loc = result.getLastLocation();
        createLog(loc);
        sendInfo(loc);
    }

    private void createLog(Location loc){

        String msj = "lat: " +loc.getLatitude() + " - lgn: " +loc.getLongitude();
        LogMH obj = new LogMH(LogMH.TYPE_LOCATION, msj, new Date());
        LogMHDAO dao = new LogMHDAO(getBaseContext());
        dao.insert(obj);
    }

    private void sendInfo(Location loc){

        Intent intent = new Intent(Constants.REC_RESPONSE);
        intent.putExtra(Constants.REC_TYPE, Constants.REC_TYPE_LOC);
        intent.putExtra(Constants.REC_LAT, (float) loc.getLatitude());
        intent.putExtra(Constants.REC_LONG, (float) loc.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



}


















