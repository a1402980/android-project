package hes_so.santour;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class Options extends AppCompatActivity {

    public int maxGPS;
    public int minGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        setTitle("Options");

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.navigation);
        nvDrawer.setCheckedItem(R.id.createTrack);
        drawerSetup(nvDrawer);
        nvDrawer.setCheckedItem(R.id.Options);

        maxGPS = LocalData.getGpsMaxrange();
        minGPS = LocalData.getGpsMinRange();

        final CheckBox minCb = (CheckBox)findViewById(R.id.GPSminCheckbox);
        final CheckBox maxCb = (CheckBox)findViewById(R.id.GPSmaxCheckbox);
        final DiscreteSeekBar minSeek = (DiscreteSeekBar)findViewById(R.id.GPSmin);
        final DiscreteSeekBar maxSeek = (DiscreteSeekBar)findViewById(R.id.GPSmax);

        Button saveButton = (Button)findViewById(R.id.saveOptions);


        if (minGPS != 0){
            minCb.setChecked(false);
            minSeek.setVisibility(View.VISIBLE);
            minSeek.setProgress(minGPS);

        }

        if (maxGPS != 0){
            maxCb.setChecked(false);
            maxSeek.setVisibility(View.VISIBLE);
            maxSeek.setProgress(maxGPS);
        }

        minCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = minCb.isChecked();
                if (isChecked == true){
                    minSeek.setVisibility(View.GONE);
                }else{
                    minSeek.setVisibility(View.VISIBLE);
                }

            }

        });

        maxCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = maxCb.isChecked();
                if (isChecked == true){
                    maxSeek.setVisibility(View.GONE);
                }else{
                    maxSeek.setVisibility(View.VISIBLE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean maxIsChecked = maxCb.isChecked();
                final boolean minIsChecked = minCb.isChecked();

                if (maxIsChecked == false){
                    LocalData.setGpsMaxrange(maxSeek.getProgress());
                }else{
                    LocalData.setGpsMaxrange(0);
                }


                if (minIsChecked == false){
                    LocalData.setGpsMinRange(minSeek.getProgress());
                }else{
                    LocalData.setGpsMinRange(0);
                }

                Toast.makeText(Options.this, "Options saved!", Toast.LENGTH_SHORT).show();

            }
        });

    }




    public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        //Class fragmentClass;
        View v = this.findViewById(android.R.id.content).getRootView();
        switch (menuItem.getItemId()){
            case R.id.createTrack:
                finish();
//                Intent intent = new Intent(this, SanTour.class);
//                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;

            case R.id.createPOD:
                finish();
                buttonAddPODOnClick(v);
                break;

            case R.id.createPOI:
                finish();
                buttonAddPOIOnClick(v);
                break;

            case R.id.POIPODlist:
                finish();
                Intent intent1 = new Intent(this, ListView.class);
                startActivityForResult(intent1, PICK_CONTACT_REQUEST);
                break;

            case R.id.Options:
                finish();
                Intent intent2 = new Intent(this, Options.class);
                startActivityForResult(intent2, PICK_CONTACT_REQUEST);
                break;


            default:
                //buttonSanTourClick(v);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
    private void drawerSetup(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }

    public void buttonAddPOIOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(Options.this, AddPoi.class);
        String longitude = LocalData.getActuellLongitute();
        String latitude = LocalData.getActuellLangitude();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonAddPODOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(Options.this, AddPod.class);
        String longitude = LocalData.getActuellLongitute();
        String latitude = LocalData.getActuellLangitude();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }


    static final int PICK_CONTACT_REQUEST = 1;
}
