package hes_so.android_project_2017;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.io.IOException;

public class AddPod extends AppCompatActivity implements View.OnClickListener{

    private Button bSelectImage;
    private Button bTakeImage;
    private Button buploadImage;
    private Button savePoi;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pod);

        setTitle("Add POD");

        storageReference= FirebaseStorage.getInstance().getReference();

        bSelectImage = (Button) findViewById(R.id.choosePicture);
        bTakeImage = (Button) findViewById(R.id.takePicture);
        savePoi = (Button) findViewById(R.id.savePOI);
        imageView = (ImageView) findViewById(R.id.imageView);

        PoiName = (EditText) findViewById(R.id.poiName);



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
    }



    public void onAddDifficultyClick(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPod.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_difficulties, null);

        Button mSave = (Button) mView.findViewById(R.id.saveDifficulties);

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

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
            POI poi = new POI();
            poi.setName(((TextView) findViewById(R.id.poiName)).getText().toString());
            poi.setDescription(((TextView) findViewById(R.id.editText4)).getText().toString());

            poi.setLatLng(new LatLng(latitudeDataInt, longitudeDataInt));
            poi.setFilePath(filePath);
            LocalData.addPOI(poi);
            finish();
            onBackPressed();
        }
    }

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
