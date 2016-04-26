package es.nekosoft.amiathome.utils;

import com.google.android.gms.location.DetectedActivity;

import es.nekosoft.amiathome.R;


public class Constants {

    //---- Upload data ----//

    public final static int UP_DEF_TIME = 30;
    public final static int UP_MAX_TIME = 60;
    public final static int UP_MIN_TIME = 10;


    //---- Ubidots ----//

    public final static String UBI_KEY = "3d847f2f51bc001ff3daefa178433bff2d2b3ee8";
    public final static String UBI_DEST = "5717bd587625424beb0c0123";
    public final static String UBI_LOC = "5717bd867625424c3013cce1";


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
    public final static int ACT_DEF_TIME = 10;
    public final static int ACT_MAX_TIME = 60;
    public final static int ACT_MIN_TIME = 10;

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
                return R.drawable.people;
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


    //---- Logs ----//

    public final static String LOG_MAX_ENTRIES = "100";


    //---- BrowserActivity ----//

    public final static String BROW_URL = "url";

}
