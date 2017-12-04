package hes_so.android_project_2017;

import android.net.Uri;

/**
 * Created by Lionel on 20/11/2017.
 */

public class POI {
    private int id;
    private int track_Id;
    private String name;
    private String description;
    private Uri filePath;


    public POI(){

    }

    public POI(int track_Id, String name, String description, Uri filePath){
        this.track_Id = track_Id;
        this.name = name;
        this.description = description;
        this.filePath = filePath;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public int getId(){
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

    public void setId(int id){
        this.id = id;
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
}

