package hes_so.santour;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Lionel on 20/11/2017.
 */

public class POI  extends PO{

    private String image64;
    private LatLng latLng;


    public POI(){
    super();
    }

    public POI(int track_Id, String name, String description, Uri filePath, LatLng latLng){
        super(track_Id, name, description, filePath, latLng, true);
    }






}

