package hes_so.santour;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class POD extends PO{

    private ArrayList<Difficulty> difficultiesList;


    public POD(){
        super();
        this.difficultiesList = new ArrayList<>();
    }

    public POD(int track_Id, String name, String description, Uri filePath, LatLng latLng){
        super(track_Id, name, description, filePath, latLng, false);
        difficultiesList = new ArrayList<>();
    }


    public void addDifficulty(Difficulty d)
    {
        difficultiesList.add(d);
    }

    public ArrayList<Difficulty> getDifficultiesList() {
        return this.difficultiesList;
    }

}

