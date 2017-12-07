package hes_so.android_project_2017;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
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

        Map<String, Object> childs = new HashMap<String, Object>();

        if (sc.getTrack().getName().equals(""))
        {

            sc.getTrack().setName("Track"+ Calendar.getInstance().getTime());
        }
        childs.put(sc.getTrack().getName(), sc);

        try {
            trackRef.updateChildren(childs);
        }catch (Exception e)
        {
            Log.d("ERROR", e.getMessage());
        }

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
        /*StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            try {
                UploadTask uploadTask = storageReference.child("/images/" + trackName + "/" + poName + "/" + getCurrentDate() + ".jpg").putFile(fileUri);
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }*/

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageReference.child("/images/" + trackName + "/" + poName + "/" + getCurrentDate() + ".jpg");
        riversRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog

                        //and displaying a success toast
                        //Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog

                        //and displaying error message
                        //Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                    }
                });


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
