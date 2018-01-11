package hes_so.santour;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPod extends AppCompatActivity implements View.OnClickListener{

    private Button bSelectImage;
    private Button bTakeImage;
    private Button buploadImage;
    private Button savePod;
    private Button saveDifficulties;
    private StorageReference storageReference;
    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 2;
    private ImageView imageView;
    private EditText PoiName;

    //a Uri object to store file path
    private Uri filePath;

    private String longitudeData;
    private String latitudeData;

    private float longitudeDataInt;
    private float latitudeDataInt;

    private String encodedImage;
    private byte[] imageByte;

    private List<Difficulty> difficulties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pod);

        setTitle("Add POD");

        storageReference= FirebaseStorage.getInstance().getReference();

        bSelectImage = (Button) findViewById(R.id.choosePicture);
        bTakeImage = (Button) findViewById(R.id.takePicture);
        savePod = (Button) findViewById(R.id.savePOD);

        imageView = (ImageView) findViewById(R.id.imageView);

        PoiName = (EditText) findViewById(R.id.poiName);

        difficulties = new ArrayList<Difficulty>();

        //getting the GPS data from SanTour class
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                longitudeData = null;
                latitudeData = null;
            } else {
                longitudeData = extras.getString("longitudeData");
                latitudeData = extras.getString("latitudeData");
                longitudeDataInt = extras.getFloat("longitudeData");
                latitudeDataInt = extras.getFloat("latitudeData");
            }
        } else {
            latitudeData = (String) savedInstanceState.getSerializable("longitudeData");
            longitudeData = (String) savedInstanceState.getSerializable("latitudeData");
        }

        //show the text data on the page
        TextView longitudeView = (TextView) findViewById(R.id.longitudeText);
        TextView latitudeView = (TextView) findViewById(R.id.latitudeText);
        if (latitudeData != null && longitudeData != null) {
            latitudeView.setText(latitudeData);
            longitudeView.setText(longitudeData);
        }

    }

    public void takePhoto() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);

    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==0 && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            /*Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("imageReturnedIntent");
            imageView.setImageBitmap(bitmap);
*/
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] bytesForImage = baos.toByteArray();
            imageByte = bytesForImage;
            encodedImage = Base64.encodeToString(bytesForImage, Base64.DEFAULT);

        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] bytesForImage = baos.toByteArray();
                imageByte = bytesForImage;
                encodedImage = Base64.encodeToString(bytesForImage, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/*    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode==0 && resultCode == RESULT_OK){
            //     filePath = imageReturnedIntent.getData();

            Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("imageReturnedIntent");
            imageView.setImageBitmap(bitmap);

        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = imageReturnedIntent.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void addDifficulty(Difficulty diff){
        difficulties.add(diff);
    }
    public void removeDifficulty(Difficulty diff){
        difficulties.remove(diff);
    }
    public void setDifficulty(int index, Difficulty diff){
        difficulties.set(index,diff);
    }

    public void onAddDifficultyClick(final View view) {

        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        DatabaseReference podCategRef;
        DatabaseReference connectedRef;

        podCategRef = mdatabase.getReference("PODcategory");

        connectedRef = mdatabase.getReference(".info/connected");

        //List<PODcategory> podCategs = new ArrayList<PODcategory>();

        final View mView = getLayoutInflater().inflate(R.layout.dialog_difficulties, null);

        final LinearLayout layout = (LinearLayout) mView.findViewById(R.id.PODcategLayout);
        layout.setOrientation(LinearLayout.VERTICAL);

        final List<Difficulty> selectedDiff= new ArrayList<Difficulty>();

        podCategRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot podCategSnap : dataSnapshot.getChildren()){

                    PODcategory categ = podCategSnap.getValue(PODcategory.class);
                    final CheckBox cb = new CheckBox(getApplicationContext());
                    cb.setId(i);
                    cb.setTag("cb" + i);
                    cb.setText(categ.getName());
                    cb.setTextColor(Color.BLACK);
                    cb.setPadding(0,40,0,40);
                    layout.addView(cb);

                    final DiscreteSeekBar seek = new DiscreteSeekBar(getApplicationContext(),null, R.style.Widget_AppCompat_SeekBar_Discrete);
                    seek.setId(i);
                    seek.setTag("seek" + i);
                    seek.setMax(10);
                    seek.setMin(1);
                    seek.setProgress(0);
                    seek.setVisibility(View.GONE);
                    layout.addView(seek);

                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            CheckBox cb = (CheckBox) view;
                            DiscreteSeekBar seek = mView.findViewWithTag("seek" + cb.getId());

                            String difName = cb.getText().toString();

                            if(!cb.isSelected()){
                                cb.setSelected(true);
                                seek.setVisibility(View.VISIBLE);
                                seek.setProgress(0);

                                if(selectedDiff.size() == 0){
                                    Difficulty newDiff = new Difficulty();
                                    newDiff.setId(difName);
                                    newDiff.setLevel(0);
                                    selectedDiff.add(newDiff);
                                }
                                else {
                                    for(int i = 0; i < selectedDiff.size(); i++){
                                        if(!selectedDiff.get(i).getId().equals(difName)){
                                            Difficulty newDiff = new Difficulty();
                                            newDiff.setId(difName);
                                            newDiff.setLevel(0);
                                            selectedDiff.add(newDiff);
                                        }
                                    }
                                }

                            }else{

                                cb.setSelected(false);
                                seek.setVisibility(View.GONE);

                                for(Difficulty diff : selectedDiff){
                                    if(diff.getId().equals(difName)){
                                        selectedDiff.remove(diff);
                                        break;
                                    }
                                }
                            }

                        }
                    });

                    seek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                        @Override
                        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                            String difName = cb.getText().toString();
                            Difficulty updatedDif = new Difficulty(difName,value);
                            for(int i = 0; i < selectedDiff.size(); i++){
                                if(selectedDiff.get(i).getId().equals(difName)){
                                    selectedDiff.set(i,updatedDif);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                        }
                    });

                    i++;
                }

                mView.findViewById(R.id.difficultySpinner).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);

                if(connected){
                    Toast.makeText(getApplicationContext(),"Getting POD categories...", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //Log.e("POD categories: ", String.valueOf(podCategs.size()));
        /*for(PODcategory podCateg : podCategs){

            CheckBox cb = new CheckBox(this);
            cb.setText(podCateg.getName());
            layout.addView(cb);
        }*/

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPod.this);



        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = (Button) mView.findViewById(R.id.saveDifficulties);
        Button mReset = (Button) mView.findViewById(R.id.cancelAction);

        mSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                difficulties = selectedDiff;
                Toast.makeText(getApplicationContext(),selectedDiff.get(0).getId(),Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),difficulties.get(0).getId(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void onClick(View view) {
        //if the clicked button is choose
        if (view == bSelectImage) {
            showFileChooser();
        }
        else if(view== bTakeImage){
            takePhoto();
        }

        else if(view== savePod){
            POD pod = new POD();
            pod.setName(((TextView) findViewById(R.id.podName)).getText().toString());
            pod.setDescription(((TextView) findViewById(R.id.editText4)).getText().toString());
            pod.setImage64(encodedImage);
            pod.setByteArrayFromImage(imageByte);
            pod.setLatLng(new LatLng(latitudeDataInt, longitudeDataInt));
            pod.setFilePath(filePath);

            String diffText = "";
            for(Difficulty diff : difficulties){
                pod.addDifficulty(diff);
                diffText += diff.getId();
                Log.d(this.getClass().getName(),"HELLOOOO" + diffText);
                Log.i(this.getClass().getName(),"HELLOOOO" + diffText);
            }

            LocalData.addPO(pod);
            finish();
            onBackPressed();
        }

    }

    /*
    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == bSelectImage) {
            showFileChooser();
        }
        else if(view== bTakeImage){
            takePhoto();
        }

        else if(view== savePoi){
            PO poi = new POI();
            poi.setName(((TextView) findViewById(R.id.poiName)).getText().toString());
            poi.setDescription(((TextView) findViewById(R.id.editText4)).getText().toString());
            poi.setImage64(encodedImage);
            poi.setByteArrayFromImage(imageByte);
            poi.setLatLng(new LatLng(latitudeDataInt, longitudeDataInt));
            poi.setPOI(true);
            LocalData.addPO(poi);
            finish();
            onBackPressed();
        }
    }
     */

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }


/* Move to LocalData
    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageReference.child( "/images/"+ PoiName.getText() + "/"+PoiName.getText() + "_" + getCurrentDate() +".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
    */

    public void cancelOnClick(View v) {
        finish();
        onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("LONGITUDEDATA", latitudeData);
        outState.putString("LATITUDDATA",longitudeData);
    }





    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        latitudeData = (String) savedInstanceState.getString("LONGITUDEDATA");
        longitudeData = (String) savedInstanceState.getString("LATITUDDATA");

        TextView longitudeView = (TextView) findViewById(R.id.longitudeText);
        TextView latitudeView = (TextView) findViewById(R.id.latitudeText);
        if (latitudeData != null && longitudeData != null) {
            latitudeView.setText(latitudeData);
            longitudeView.setText(longitudeData);
        }

    }



}
