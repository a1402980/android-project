package hes_so.android_project_2017;

/**
 * Created by Lionel on 20/11/2017.
 */

public class GPSdata {
    private int id;
    private float latitude;
    private float longitude;

    public GPSdata(){

    }

    public GPSdata(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId(){
        return this.id;
    }

    public float getLatitude(){
        return this.latitude;
    }

    public float getLongitude(){
        return this.longitude;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setLatitude(float latitude){
        this.latitude = latitude;
    }

    public void setLongitude(float longitude){
        this.longitude = longitude;
    }
}

