package hes_so.android_project_2017;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by Matthia on 14.12.2017.
 */

public class PO {

    private boolean isPOI;
    private String id;
    private int track_Id;
    private String name;
    private String description;
    private Uri filePath;
    private String image64;
    private LatLng latLng;
    private byte[] byteArrayFromImage;

    public PO(int track_Id, String name, String description, Uri filePath, LatLng latLng,boolean isPOI){
        this.track_Id = track_Id;
        this.name = name;
        this.description = description;
        this.filePath = filePath;
        this.latLng = latLng;
        this.isPOI = isPOI;
        if (isPOI) {
            this.id = "POI : Name : "+name+" Description : "+description;
        }else
        {
            this.id = "POD : Name : "+name+" Description : "+description;
        }
    }

    public PO()
    {

    }

    @Exclude
    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public String getId(){
        if (id == null || id.isEmpty())
        {
            if (isPOI) {
                this.id = "POI : Name : "+name+" Description : "+description;
            }else
            {
                this.id = "POD : Name : "+name+" Description : "+description;
            }
        }
        return this.id;
    }

    public int getTrack_Id(){

        return this.track_Id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }



    public void setTrack_Id(int track_id){
        this.track_Id = track_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Exclude
    public byte[] getByteArrayFromImage() {
        return byteArrayFromImage;
    }

    public void setByteArrayFromImage(byte[] byteArrayFromImage) {
        this.byteArrayFromImage = byteArrayFromImage;
    }

    public String getImage64() {
        return image64;
    }

    public void setImage64(String image64) {
        this.image64 = image64;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public boolean isPOI() {
        return isPOI;
    }

    public void setPOI(boolean POI) {
        isPOI = POI;
    }
}
