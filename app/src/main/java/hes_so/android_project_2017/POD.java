package hes_so.android_project_2017;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by Lionel on 20/11/2017.
 */

public class POD extends PO{

    private ArrayList<Difficulty> difficultiesList;


    public POD(){
        super();
        difficultiesList = new ArrayList<Difficulty>();
    }

    public POD(int track_Id, String name, String description, Uri filePath, LatLng latLng){
        super(track_Id, name, description, filePath, latLng, false);
        difficultiesList = new ArrayList<Difficulty>();
    }



    public void addDifficulty(Difficulty d)
    {
        difficultiesList.add(d);
    }

    public ArrayList<Difficulty> getDifficultiesList() {
        return difficultiesList;
    }

}

