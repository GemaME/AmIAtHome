package es.nekosoft.ejercicio02.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import es.nekosoft.ejercicio02.utils.Constants;


public class MainReciever extends BroadcastReceiver {


    //---- Atributes ----//

    private String TAG = "MainReciever";
    Activity activity;


    //---- Constructor ----//

    public MainReciever(Activity activity){

        this.activity = activity;
    }

    public MainReciever() {

    }


    //---- Intent reception ----//

    @Override
    public void onReceive(Context context, Intent intent) {

        /*int type = intent.getIntExtra(Constants.REC_TYPE, 0);

        if(type == Constants.REC_TYPE_LOC){

            float lat = intent.getFloatExtra(Constants.REC_TYPE_LAT, 0);
            float lon = intent.getFloatExtra(Constants.REC_TYPE_LONG, 0);
        }
        else if(type == Constants.REC_TYPE_ACT){

        }


        String typexx = intent.getStringExtra("type");

        Toast.makeText(context, "Una prueba: " + typexx, Toast.LENGTH_LONG).show();*/
    }

}



















