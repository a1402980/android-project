package hes_so.santour;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocalData {
    private static int gpsMaxrange;
    private static int gpsMinRange;

    private static boolean timerIsRunning = false;
    private static boolean trackingFinished = false;
    private static Track track;
    private static List<PO> poList;
    private static String actuellLongitute = "";
    private static String actuellLangitude="";




    public static void setTrack(Track track) {
        LocalData.track = track;
    }

    public static Track getTrack() {
        if (track == null)
            track = new Track();
        return track;
    }

    public static List<PO> getPoList() {
        if (poList == null)
            poList = new ArrayList<>();
        if(poList.size() == 0)
        {
            POI poiExample = new POI();
            poiExample.setName("Example");
            poiExample.setDescription("This is only a Example");
            poiExample.setLatLng(new LatLng(42, 13));
            poList.add(poiExample);
            POI poiExample2 = new POI();
            poiExample2.setName("Example");
            poiExample2.setDescription("This is only a Example");
            poiExample2.setLatLng(new LatLng(42, 13));
            poiExample2.setDescription("This is only a Example Number 2");
            poList.add(poiExample2);
            POD podExample3 = new POD();
            podExample3.setName("Example");
            podExample3.setDescription("This is only a Example");
            podExample3.setLatLng(new LatLng(42, 13));
            podExample3.setDescription("This is only a Example Number 3");
            podExample3.setPOI(false);
            poList.add(podExample3);
            POI poiExample4 = new POI();
            poiExample4.setName("Example");
            poiExample4.setDescription("This is only a Example");
            poiExample4.setLatLng(new LatLng(42, 13));
            poiExample4.setDescription("This is only a Example Number 4");
            poList.add(poiExample4);

        }

        return poList;
    }

    public static boolean isTrackingFinished() {
        return trackingFinished;
    }

    public static void setTrackingFinished(boolean trackingFinished) {
        LocalData.trackingFinished = trackingFinished;
    }

    public static void addPO(PO po)
    {
        if (poList == null)
            poList = new ArrayList<>();
        poList.add(po);
    }


    public static void removePO(String id)
    {
     PO temp = null;
        for (PO po: poList) {
            if(po.getId() == id)
            {
                temp = po;
            }
        }
     if (temp != null)
     {
         poList.remove(temp);
         Log.d("Remove", temp.getId());
     }

    }

    public static void resetAll()
    {
        Track track = null;
        poList = new ArrayList<PO>();
    }

    public static void saveDataFirebase()
    {

        DatabaseReference trackRef;
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        trackRef = mdatabase.getReference("tracks");


        String trackKey = trackRef.push().getKey();

        SaveClass sc = new SaveClass(poList, track);

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


        if (poList != null)
            for (PO po: poList) {
                    savePicture(po.getByteArrayFromImage(), sc.getTrack().getName(), po.getName());
            }

    }


    private static void savePicture (byte[] byteFromImage, String trackName, String poName) {
        //Log.d("Image", file64);

        //Bitmap decodedByte = BitmapFactory.decodeByteArray(byteFromImage, 0, byteFromImage.length);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();



        UploadTask uploadTask =  storageReference.child("/images/" + trackName + "/" + poName + "/" + getCurrentDate() + ".jpg").putBytes(byteFromImage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //sendMsg("" + downloadUrl, 2);
                Log.d("downloadUrl-->", "" + downloadUrl);
            }
        });


        /*StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            try {
                UploadTask uploadTask = storageReference.child("/images/" + trackName + "/" + poName + "/" + getCurrentDate() + ".jpg").putFile(fileUri);
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }

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

*/
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }

    private static class SaveClass
    {
        private List<POI> poi;
        private List<POD> pod;
        private Track track;

        public SaveClass(List<PO> po, Track track) {
            if (po == null)
                po = new ArrayList<>();
            poi = new ArrayList<POI>();
            pod = new ArrayList<POD>();

            for (PO poObjecz: po) {
                if(poObjecz.isPOI())
                {
                    poi.add((POI) poObjecz);
                }else
                {
                    pod.add((POD) poObjecz);
                }
            }
            this.track = track;
        }

        public Track getTrack() {
            if (track == null)
                track = new Track();
            return track;
        }

        public List<POI> getPoi() {
            return poi;
        }

        public List<POD> getPod() {
            return pod;
        }
    }


    public static boolean isTimerIsRunning() {
        return timerIsRunning;
    }

    public static void setTimerIsRunning(boolean timerIsRunning) {
        LocalData.timerIsRunning = timerIsRunning;
    }

    public static void setPoList(List<PO> poList) {
        LocalData.poList = poList;
    }

    public static String getActuellLongitute() {
        return actuellLongitute;
    }

    public static void setActuellLongitute(String actuellLongitute) {
        LocalData.actuellLongitute = actuellLongitute;
    }

    public static String getActuellLangitude() {
        return actuellLangitude;
    }

    public static void setActuellLangitude(String actuellLangitude) {
        LocalData.actuellLangitude = actuellLangitude;
    }

    public static int getGpsMaxrange() {
        return gpsMaxrange;
    }

    public static void setGpsMaxrange(int gpsMaxrange) {
        LocalData.gpsMaxrange = gpsMaxrange;
    }

    public static int getGpsMinRange() {
        return gpsMinRange;
    }

    public static void setGpsMinRange(int gpsMinRange) {
        LocalData.gpsMinRange = gpsMinRange;
    }
}
