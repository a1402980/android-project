package hes_so.android_project_2017;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthias on 01.12.2017.
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
        if (podList == null)
            podList = new ArrayList<>();
        podList.add(pod);
    }

    public static void addPOI(POI poi)
    {
        if (poiList == null)
            poiList = new ArrayList<>();
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

        DatabaseReference trackRef;
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        trackRef = mdatabase.getReference("tracks");

        Track track1 = new Track(track.getName(), "");
        String trackKey = trackRef.push().getKey();

        SaveClass sc = new SaveClass(podList, poiList, track);

        Map<String, Object> childs = new HashMap<>();

        childs.put(sc.getTrack().getName(), sc);

        trackRef.updateChildren(childs);
        
        //Add Save Pictures here later
    }


    private static class SaveClass
    {
        private List<POD> pod;
        private List<POI> poi;
        private Track track;

        public SaveClass(List<POD> pod, List<POI> poi, Track track) {
            this.pod = pod;
            this.poi = poi;
            this.track = track;
        }

        public Track getTrack() {
            return track;
        }

        public List<POD> getPod() {
            return pod;
        }

        public List<POI> getPoi() {
            return poi;
        }
    }
}
