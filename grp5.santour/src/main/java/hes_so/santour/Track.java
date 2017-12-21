package hes_so.santour;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lionel on 20/11/2017.
 */

public class Track {

    private int id;
    private String name;
    private String description;
    private String timeDuration;
    private float kmLength;
    private List<LatLng> trackingPoints;

    public Track(){
        trackingPoints = new ArrayList<LatLng>();

    }

    public Track(String name, String description){
        this.name = name;
        this.description = description;
        trackingPoints = new ArrayList<LatLng>();
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public String getTimeDuration(){
        return this.timeDuration;
    }

    public float getKmLength(){
        return this.kmLength;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setTimeDuration(String timeDuration){
        this.timeDuration = timeDuration;
    }

    public void setKmLength(float kmLength){
        this.kmLength = kmLength;
    }

    public void addPoint(LatLng location)
    {
        trackingPoints.add(location);
    }

    public List<LatLng> getTrackingPoints() {
        return trackingPoints;
    }

    public void setTrackingPoints(List<LatLng> trackingPoints) {
        this.trackingPoints = trackingPoints;
    }
}

