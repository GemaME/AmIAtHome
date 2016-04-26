package es.nekosoft.amiathome.model;

import java.util.Date;

import es.nekosoft.amiathome.R;


public class LogMH {

    //---- Atributes ---//

    private long id;
    private int type;
    private String title;
    private Date date;


    //---- Constructors ----//

    public LogMH(long id, int type, String title, Date date) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public LogMH(int type, String title, Date date) {
        this.type = type;
        this.title = title;
        this.date = date;
    }

    public LogMH() {
    }


    //---- Getter & Setters ----//

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //---- Icons ---//

    public final static int TYPE_ACTIVITY = 1;
    public final static int TYPE_LOCATION = 2;

    public static int getIdIcon(int iconType){

        int result = R.drawable.location_color;

        switch (iconType){

            case TYPE_ACTIVITY:
                result = R.drawable.car_color;
                break;
            case TYPE_LOCATION:
                result = R.drawable.location_color;
                break;
        }

        return result;
    }

}
