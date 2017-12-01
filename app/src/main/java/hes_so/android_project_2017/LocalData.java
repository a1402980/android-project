package hes_so.android_project_2017;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthias and Joey on 01.12.2017.
 */

public class LocalData {
    private static Track track;
    private static List<POD> podList;
    private static List<POI> poiList;

    public static void setTrack(Track track) {
        LocalData.track = track;
    }

    public static Track getTrack() {
        return track;
    }

    public static List<POD> getPodList() {
        return podList;
    }

    public static List<POI> getPoiList() {
        return poiList;
    }

    public static void addPOD(POD pod)
    {
        podList.add(pod);
    }

    public static void addPOI(POI poi)
    {
        poiList.add(poi);
    }

    public static void resetAll()
    {
        Track track = null;
        poiList = new ArrayList<POI>();
        podList = new ArrayList<POD>();
    }

    public static void saveDataFirebase()
    {
        //Save Track
        //SaveGPS Data
        for (POD pod :podList) {
            //save as child from TRACK
            //SaveHashmap from POD Difficulties
        }

        for (POI poi : poiList)
        {
            //save as child from TRACK
        }
    }
}
