package hes_so.android_project_2017;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Trek {

    public String id;
    private List<LatLng> trackingPoints;

    public Trek(){}

    public Trek(String id, List<LatLng> trackingPoints){
        this.id = id;
        this.trackingPoints=trackingPoints;
    }
}
