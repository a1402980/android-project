package hes_so.android_project_2017;

import android.net.Uri;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;
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
        if (track == null)
            track = new Track();
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


        String trackKey = trackRef.push().getKey();

        SaveClass sc = new SaveClass(podList, poiList, track);

        Map<String, Object> childs = new HashMap<>();

        if (sc.getTrack().getName().equals(""))
        {

            sc.getTrack().setName("Track"+ Calendar.getInstance().getTime());
        }
        childs.put(sc.getTrack().getName(), sc);

        trackRef.updateChildren(childs);

        if (podList != null)
            for (POD pod: podList) {
                if (pod.getFilePath() != null)
                    savePicture(pod.getFilePath(), sc.getTrack().getName(), pod.getName());
            }

        if (poiList != null)
            for (POI poi: poiList) {
                if (poi.getFilePath() != null)
                    savePicture(poi.getFilePath(), sc.getTrack().getName(), poi.getName());
            }
        //Add Save Pictures here later
    }


    private static void savePicture (Uri fileUri, String trackName, String poName) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (fileUri != null) {
            StorageReference riversRef = storageReference.child("/images/" + trackName + "/" + poName + "/" + getCurrentDate() + ".jpg");

            Uri file = fileUri;
            Log.d("file", file.getPath());

            UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("uploadFail", "" + exception);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //sendNotification("upload backup", 1);

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Log.d("downloadUrl", "" + downloadUrl);
                }
            });
        }
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }

    private static class SaveClass
    {
        private List<POD> pod;
        private List<POI> poi;
        private Track track;

        public SaveClass(List<POD> pod, List<POI> poi, Track track) {
            if (pod == null)
                pod = new ArrayList<>();
            if (poi == null)
                poi = new ArrayList<>();
            this.pod = pod;
            this.poi = poi;
            this.track = track;
        }

        public Track getTrack() {
            if (track == null)
                track = new Track();
            return track;
        }

        public List<POD> getPod() {
            if(pod == null)
                pod = new ArrayList<>();
            return pod;
        }

        public List<POI> getPoi() {
            if (poi == null)
                pod = new ArrayList<>();
            return poi;
        }
    }
}
