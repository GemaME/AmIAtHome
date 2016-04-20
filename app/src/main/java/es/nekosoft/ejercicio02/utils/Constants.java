package es.nekosoft.ejercicio02.utils;

import com.google.android.gms.location.DetectedActivity;

import es.nekosoft.ejercicio02.R;


public class Constants {


    //---- Receiver ----//

    public final static String REC_RESPONSE = "es.nekosoft.RESPONSE";
    public final static String REC_TYPE = "type";
    public final static int REC_TYPE_LOC = 1;
    public final static int REC_TYPE_ACT = 2;
    public final static String REC_LAT = "latitude";
    public final static String REC_LONG = "longitude";
    public final static String REC_ACTIVITY = "activity";
    public final static String REC_PERCENT = "percentage";

    //---- Location ----//

    public final static String LOC_ACTION = "es.nekosoft.LOCATION";
    public static int LOC_INTERVAL = 10000;
    public static int LOC_FAST_INTERVAL = 1000;

    //---- Activity ----//

    public final static String ACT_ACTION = "es.nekosoft.ACTION";
    public static int ACT_DETECT_INTERV_MS = 20000;

    public static int getActivityString(int detectedActivityType) {

        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return R.string.act_vehicle;
            case DetectedActivity.ON_BICYCLE:
                return R.string.act_bicycle;
            case DetectedActivity.ON_FOOT:
                return R.string.act_foot;
            case DetectedActivity.RUNNING:
                return R.string.act_running;
            case DetectedActivity.STILL:
                return R.string.act_still;
            case DetectedActivity.TILTING:
                return R.string.act_tilting;
            case DetectedActivity.UNKNOWN:
                return R.string.act_unkown;
            case DetectedActivity.WALKING:
                return R.string.act_walking;
            default:
                return R.string.act_undefined;
        }
    }

    public static int getActivityDrawable(int detectedActivityType) {

        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return R.drawable.car;
            case DetectedActivity.ON_BICYCLE:
                return R.drawable.bycicle;
            case DetectedActivity.ON_FOOT:
                return R.drawable.alien;
            case DetectedActivity.RUNNING:
                return R.drawable.running;
            case DetectedActivity.STILL:
                return R.drawable.still;
            case DetectedActivity.TILTING:
                return R.drawable.alien;
            case DetectedActivity.UNKNOWN:
                return R.drawable.alien;
            case DetectedActivity.WALKING:
                return R.drawable.people;
            default:
                return R.drawable.car;
        }
    }


    //---- Locations ---//

    public final static String SP_ASK_HOME = "ask home";

    public final static float BIBLIOTECA_LAT = 38.38346617364772f;
    public final static float BIBLIOTECA_LONG = -0.5119961500167847f;

    public final static float AULARIO02_LAT = 38.38444173186643f;
    public final static float AULARIO02_LONG = -0.5101668834686279f;

    public final static float POLI01_LAT = 38.38678806370045f;
    public final static float POLI01_LONG = -0.5113685131072998f;

    public final static float PISO_LAT = 38.390807767128706f;
    public final static float PISO_LONG = -0.5154776573181152f;

    public final static float CASA_LAT = 37.981077621922594f;
    public final static float CASA_LONG = -0.6657564640045166f;

    public final static float HIPERBER_LAT = 38.39308241662081f;
    public final static float HIPERBER_LONG = -0.5182886123657227f;
}
