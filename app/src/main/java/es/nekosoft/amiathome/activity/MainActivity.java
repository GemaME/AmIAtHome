package es.nekosoft.amiathome.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ubidots.ApiClient;
import com.ubidots.Variable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import es.nekosoft.amiathome.R;
import es.nekosoft.amiathome.service.ActivityIntentService;
import es.nekosoft.amiathome.service.LocationIntentService;
import es.nekosoft.amiathome.utils.Constants;
import es.nekosoft.amiathome.utils.Sensible;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    GoogleApiClient mGoogleApiClient;

    TextView tvDistanceQuantity;
    TextView tvDistanceRemain;
    TextView tvDistancePlace;

    ImageView ivActivity;
    TextView tvActivityType;

    SeekBar sbUbi;
    TextView tvUbi;
    SeekBar sbAct;
    TextView tvAct;

    ImageButton btnMap;
    ImageButton btnBrowser;
    ImageButton btnHouse;
    ImageButton btnLog;

    float actLat;
    float actLon;
    String actPlace;

    private float lastLat = 0.0f;
    private float lastLon = 0.0f;
    private float lastDist = 0.0f;
    private int lastAct = 0;

    int sbUbiProg;
    int sbActProg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actLat = Constants.CASA_LAT;
        actLon = Constants.CASA_LONG;
        actPlace = getString(R.string.place_casa);

        getViews();
        setupUploadUbidots();
        setupTimeActivity();
        setupGoogleApi();

        //Init Handler timer
        handler.post(runnableCode);
    }

    private void getViews(){

        tvDistanceQuantity = (TextView) findViewById(R.id.tv_distance_quantity);
        tvDistanceRemain = (TextView) findViewById(R.id.tv_distance_remain);
        tvDistancePlace = (TextView) findViewById(R.id.tv_distance_place);

        ivActivity = (ImageView) findViewById(R.id.iv_activity);
        tvActivityType = (TextView) findViewById(R.id.tv_activity_type);

        sbUbi = (SeekBar) findViewById(R.id.sb_upload_ubi);
        tvUbi = (TextView) findViewById(R.id.tv_upload_ubi);
        sbAct = (SeekBar) findViewById(R.id.sb_upload_act);
        tvAct = (TextView) findViewById(R.id.tv_upload_act);

        btnMap = (ImageButton) findViewById(R.id.btn_map);
        btnBrowser = (ImageButton) findViewById(R.id.btn_browser);
        btnHouse = (ImageButton) findViewById(R.id.btn_home);
        btnLog = (ImageButton) findViewById(R.id.btn_log);
        btnMap.setOnClickListener(this);
        btnBrowser.setOnClickListener(this);
        btnHouse.setOnClickListener(this);
        btnLog.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    //---- Setup upload data ----//

    private void setupUploadUbidots() {

        //Set progress
        sbUbiProg = (int) Constants.UP_DEF_TIME;
        sbUbi.setProgress((int) ((100*Constants.UP_DEF_TIME / 50)-Constants.UP_MIN_TIME));
        String msg = String.format(getString(R.string.msg_upload_ubi), "" + sbUbiProg);
        tvUbi.setText(msg);

        //Seekbar event
        sbUbi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sbUbiProg = (int) ((progress * (Constants.UP_MAX_TIME - Constants.UP_MIN_TIME) / 100) + Constants.UP_MIN_TIME);
                String msg = String.format(getString(R.string.msg_upload_ubi), "" + sbUbiProg);
                tvUbi.setText(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupTimeActivity() {

        //Set progress
        sbActProg = (int) Constants.ACT_DEF_TIME;
        sbAct.setProgress((int) (100*Constants.ACT_DEF_TIME / 50));
        String msg = String.format(getString(R.string.msg_upload_act), "" + sbActProg);
        tvAct.setText(msg);

        //Seekbar event
        sbAct.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                removeGoogleActivities();
                sbActProg = (int) ((progress * (Constants.ACT_MAX_TIME - Constants.ACT_MIN_TIME) / 100) + Constants.ACT_MIN_TIME);
                String msg = String.format(getString(R.string.msg_upload_act), "" + sbActProg);
                setupGoogleActivities(sbActProg);
                tvAct.setText(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //Handler for the timer
    Handler handler = new Handler();
    Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            //Maybe we haven't info of the distance
            if(lastAct != 0){

                //Send info to ubidots
                ApiUbidots ubidots = new ApiUbidots();
                ubidots.execute(lastLat, lastLon, new Float(lastDist), Float.valueOf(lastAct));
            }

            //Set next call
            handler.postDelayed(runnableCode, sbUbiProg * 1000);
        }
    };

    public class ApiUbidots extends AsyncTask<Float, Void, Void> {

        @Override
        protected Void doInBackground(Float... params) {

            //What does mean each element of the array?
            float lat = params[0];
            float lng = params[1];
            float dst = params[2];
            float act = params[3];

            //Preparing client
            Log.d("API", Constants.UBI_KEY);
            ApiClient apiClient = new ApiClient(Constants.UBI_KEY);

            //Send information about de location
            Variable location = apiClient.getVariable(Constants.UBI_LOC);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("lat", lat);
            map.put("lng", lng);
            location.saveValue((lat+lng)/2, map);

            //Send information about distance & activity
            Variable destination = apiClient.getVariable(Constants.UBI_DEST);
            map = new HashMap<String, Object>();
            map.put("dist", dst);
            map.put("act", act);
            destination.saveValue(act + dst, map);

            return null;
        }
    }


    //---- Prepare broadcast msg ----//

    private void prepareBroadcast() {

        ActivityReceiver receiver = new ActivityReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.REC_RESPONSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int type = intent.getIntExtra(Constants.REC_TYPE, 0);

            //We recieve info about location
            if (type == Constants.REC_TYPE_LOC) {

                lastLat = intent.getFloatExtra(Constants.REC_LAT, 0);
                lastLon = intent.getFloatExtra(Constants.REC_LONG, 0);
                calculateDistance();
            }

            //We recieve info about the activity
            else if (type == Constants.REC_TYPE_ACT) {

                lastAct = intent.getIntExtra(Constants.REC_ACTIVITY, 0);
                int percent = intent.getIntExtra(Constants.REC_PERCENT, 0);
                setActivityType(lastAct);
            }
        }
    }


    //---- Google Api ----//

    private void setupGoogleApi() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        prepareBroadcast();
        setupLocation();
        setupGoogleActivities(Constants.ACT_DEF_TIME);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getBaseContext(), getString(R.string.err_google_api_connect), Toast.LENGTH_LONG).show();
    }


    //---- Location ----//

    private void setupLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = createLocationRequest(Constants.LOC_INTERVAL, Constants.LOC_FAST_INTERVAL, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getLocationPendingIntent()).setResultCallback(this);
    }

    private LocationRequest createLocationRequest(int interval, int fastest, int priority) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastest);
        mLocationRequest.setPriority(priority);

        return mLocationRequest;
    }

    private PendingIntent getLocationPendingIntent() {

        Intent intent = new Intent(this, LocationIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }


    //---- Activities ----//

    private void setupGoogleActivities(int time){

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                time * 1000,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private void removeGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGoogleActivitiesPendingIntent(){

        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //---- Show distance & activity info ----//

    public void calculateDistance(){

        Double distance = distanceHaversine(lastLat, lastLon, actLat, actLon);

        if (distance>0.1){ //We are not in teh destination

            DecimalFormat df = new DecimalFormat("#.##");
            tvDistanceQuantity.setText(df.format(distance) + "");
            tvDistanceRemain.setText(getString(R.string.distance_left));
            tvDistancePlace.setText(actPlace);
            lastDist = new Float(distance);
        }
        else{ //We've arrived

            tvDistanceQuantity.setText(getString(R.string.distance_left_finish));
            tvDistanceRemain.setText("");
            tvDistancePlace.setText("");
        }
    }

    public void setActivityType(int activity){

        //Set image
        int drawable = Constants.getActivityDrawable(activity);
        ivActivity.setImageResource(drawable);

        //Set activity title
        int actStr = Constants.getActivityString(activity);
        tvActivityType.setText(getString(actStr));
    }

    private double distanceHaversine(double lat01, double lon01, double lat02, double lon02){

        int R = 6371;
        double latDistance = toRad(lat02-lat01);
        double lonDistance = toRad(lon02-lon01);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(toRad(lat01)) * Math.cos(toRad(lat02)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;

        return distance;
    }

    private static double toRad(double value) {

        return value * Math.PI / 180;
    }


    //---- Menu ----//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //---- Buttons ----//

    @Override
    public void onClick(View v) {
        
        int id = v.getId();
        
        switch (id){

            case R.id.btn_map:
                showMap();
                break;
            case R.id.btn_browser:
                showBrowser();
                break;
            case R.id.btn_home:
                showLocations();
                break;
            case R.id.btn_log:
                showLog();
                break;

        }
    }

    private void showMap() {

        Intent i = new Intent(this, BrowserActivity.class);
        i.putExtra(Constants.BROW_URL, Sensible.BROW_URL_MAP);
        startActivity(i);
    }

    private void showBrowser() {

        Intent i = new Intent(this, BrowserActivity.class);
        i.putExtra(Constants.BROW_URL, Sensible.BROW_URL_DATA);
        startActivity(i);
    }

    private void showLocations() {

        //Construir dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(getString(R.string.dialog_places_title))
                .setItems(getResources().getStringArray(R.array.list_places),

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {

                                String place = null;

                                switch (item){

                                    case 0:
                                        actLat = Constants.PISO_LAT;
                                        actLon = Constants.PISO_LONG;
                                        place = getString(R.string.place_piso);
                                        break;
                                    case 1:
                                        actLat = Constants.CASA_LAT;
                                        actLon = Constants.CASA_LONG;
                                        place = getString(R.string.place_casa);
                                        break;
                                    case 2:
                                        actLat = Constants.HIPERBER_LAT;
                                        actLon = Constants.HIPERBER_LONG;
                                        place = getString(R.string.place_hiperber);
                                        break;
                                    case 3:
                                        actLat = Constants.POLI01_LAT;
                                        actLon = Constants.POLI01_LONG;
                                        place = getString(R.string.place_poli_01);
                                        break;
                                    case 4:
                                        actLat = Constants.AULARIO02_LAT;
                                        actLon = Constants.AULARIO02_LONG;
                                        place = getString(R.string.place_aulario_02);
                                        break;
                                    case 5:
                                        actLat = Constants.BIBLIOTECA_LAT;
                                        actLon = Constants.BIBLIOTECA_LONG;
                                        place = getString(R.string.place_biblio);
                                        break;
                                }

                                //Show new distance and place
                                actPlace = place;
                                calculateDistance();

                            }
                        })
                .setNegativeButton(getText(R.string.dialog_places_cancel), null);

        //Devolver el dialogo
        builder.show();
    }

    private void showLog() {

        startActivity(new Intent(this, LogActivity.class));
    }

}
