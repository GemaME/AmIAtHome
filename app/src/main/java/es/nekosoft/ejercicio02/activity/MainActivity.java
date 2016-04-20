package es.nekosoft.ejercicio02.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;

import es.nekosoft.ejercicio02.R;
import es.nekosoft.ejercicio02.receiver.MainReciever;
import es.nekosoft.ejercicio02.service.ActivityIntentService;
import es.nekosoft.ejercicio02.service.LocationIntentService;
import es.nekosoft.ejercicio02.utils.Constants;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    GoogleApiClient mGoogleApiClient;
    ImageButton btnBrowser;
    ImageButton btnHouse;
    ImageView ivActivity;
    TextView tvDistance;
    TextView tvDistLabel;
    TextView tvActivity;

    float actLat = Constants.CASA_LAT;
    float actLon = Constants.CASA_LONG;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        btnBrowser = (ImageButton) findViewById(R.id.btn_browser);
        btnHouse = (ImageButton) findViewById(R.id.btn_home);
        btnBrowser.setOnClickListener(this);
        btnHouse.setOnClickListener(this);
        tvDistance = (TextView) findViewById(R.id.distance_quantity);
        tvActivity = (TextView) findViewById(R.id.vehicle_type);
        tvDistLabel = (TextView) findViewById(R.id.distance_label);
        ivActivity = (ImageView) findViewById(R.id.vehicle_pic);

        //Setup Google APIs
        setupGoogleApi();
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


    //---- Mensaje Broadcast ----//

    private void prepareBroadcast(){

        ActivityReceiver receiver = new ActivityReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.REC_RESPONSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int type = intent.getIntExtra(Constants.REC_TYPE, 0);

            //Location
            if(type == Constants.REC_TYPE_LOC){

                float lat = intent.getFloatExtra(Constants.REC_LAT, 0);
                float lon = intent.getFloatExtra(Constants.REC_LONG, 0);
                calculateDistance(lat, lon);
            }

            //Activity
            else if(type == Constants.REC_TYPE_ACT){

                int activity = intent.getIntExtra(Constants.REC_ACTIVITY, 0);
                int percent = intent.getIntExtra(Constants.REC_PERCENT, 0);
                setActivityType(activity);
            }
            //String typexx = intent.getStringExtra("type");
            //Toast.makeText(context, "Una prueba: " + typexx, Toast.LENGTH_LONG).show();
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
        setupGoogleActivities();
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

    private void setupGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.ACT_DETECT_INTERV_MS,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGoogleActivitiesPendingIntent(){

        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //---- Calculate distance and upload UI ----//

    private void calculateDistance(float lat, float lon){

        Double distance = distanceHaversine(lat, lon, actLat, actLon);

        if (distance>0.01){

            DecimalFormat df = new DecimalFormat("#.##");
            tvDistance.setText(df.format(distance) + "");
            tvDistLabel.setText(getString(R.string.distance_left));
        }
        else{

            tvDistance.setText(getString(R.string.distance_left_finish));
            tvDistLabel.setText("");
        }
    }

    private void setActivityType(int activity){

        //Set image
        int drawable = Constants.getActivityDrawable(activity);
        ivActivity.setImageResource(drawable);

        //Set activity title
        int actStr = Constants.getActivityString(activity);
        tvActivity.setText(getString(actStr));
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


    //---- Button events ----//

    @Override
    public void onClick(View v) {
        
        int id = v.getId();
        
        switch (id){
            
            case R.id.btn_browser:
                showBrowser();
                break;
            case R.id.btn_home:
                showLocations();
                break;
        }
    }

    private void showBrowser() {

        startActivity(new Intent(this, BrowserActivity.class));
    }

    private void showLocations() {

        //Construir dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(getString(R.string.dialog_places_title))
                .setItems(getResources().getStringArray(R.array.list_places),

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {

                                Toast.makeText(getBaseContext(), "prueba seleccion", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton(getText(R.string.dialog_places_cancell), null);

        //Devolver el dialogo
        builder.show();
    }

}
