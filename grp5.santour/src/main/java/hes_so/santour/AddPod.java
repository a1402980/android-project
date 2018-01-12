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
import android.widget.LinearLayout.LayoutParams;
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

/**
 * This class will manage the display and the actions on the "Add POD" view.
 **/
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

    /**
     * This method is executed when the Add POD view is created and loaded
     * @param savedInstanceState
     */
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
                /*longitudeDataInt = extras.getFloat("longitudeData");
                latitudeDataInt = extras.getFloat("latitudeData");*/
                try{
                    longitudeDataInt = Float.parseFloat(longitudeData);
                    latitudeDataInt = Float.parseFloat(latitudeData);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        } else {
            latitudeData = (String) savedInstanceState.getSerializable("longitudeData");
            longitudeData = (String) savedInstanceState.getSerializable("latitudeData");
            longitudeDataInt = Float.parseFloat(longitudeData);
            latitudeDataInt = Float.parseFloat(latitudeData);
        }

        //show the text data on the page
        TextView longitudeView = (TextView) findViewById(R.id.longitudeText);
        TextView latitudeView = (TextView) findViewById(R.id.latitudeText);
        if (latitudeData != null && longitudeData != null) {
            latitudeView.setText(latitudeData);
            longitudeView.setText(longitudeData);
        }

    }

    /**
     * Method to launch the camera to take a photo for POD
     */
    public void takePhoto() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);

    }

    /**
     * Method to show file chooser to upload an image from device gallery
     */
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     *  Method to handle the image chooser activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==0 && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

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


    public void onAddDifficultyClick(final View view) {

        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        DatabaseReference podCategRef;
        DatabaseReference connectedRef;

        podCategRef = mdatabase.getReference("PODcategory");

        connectedRef = mdatabase.getReference(".info/connected");

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

                    final TextView seekValueTxt = new TextView(getApplicationContext());
                    seekValueTxt.setId(i);
                    seekValueTxt.setTag("seekTxt" + i);
                    seekValueTxt.setText("Level : ");
                    seekValueTxt.setTextSize(20);
                    seekValueTxt.setTextColor(Color.BLACK);
                    seekValueTxt.setVisibility(View.GONE);
                    layout.addView(seekValueTxt);

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
                            TextView seekTxt = mView.findViewWithTag("seekTxt" + cb.getId());

                            String difName = cb.getText().toString();

                            if(!cb.isSelected()){
                                cb.setSelected(true);
                                seek.setVisibility(View.VISIBLE);
                                seekTxt.setVisibility(View.VISIBLE);
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
                                seekTxt.setVisibility(View.GONE);
                                seekTxt.setText("Level : ");

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
                            TextView seekTxt = mView.findViewWithTag("seekTxt" + cb.getId());
                            for(int i = 0; i < selectedDiff.size(); i++){
                                if(selectedDiff.get(i).getId().equals(difName)){
                                    selectedDiff.set(i,updatedDif);
                                    seekTxt.setText("Level : " + Integer.toString(value));
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

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPod.this);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = (Button) mView.findViewById(R.id.saveDifficulties);
        Button mReset = (Button) mView.findViewById(R.id.cancelAction);

        mSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                difficulties = selectedDiff;
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

        //Do actions, depending on which button calling that method was pressed
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

            LocalData.addPO(pod);

            finish();
            onBackPressed();
        }

    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;

    }

    public void cancelOnClick(View v) {
        LocalData.setTimerIsRunning(true);
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
