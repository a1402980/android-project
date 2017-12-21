package hes_so.android_project_2017;

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

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        setTitle("Options");

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.navigation);
        nvDrawer.setCheckedItem(R.id.createTrack);
        drawerSetup(nvDrawer);
        nvDrawer.setCheckedItem(R.id.Options);
    }


    public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        //Class fragmentClass;
        View v = this.findViewById(android.R.id.content).getRootView();
        switch (menuItem.getItemId()){
            case R.id.createTrack:
                Intent intent = new Intent(this, SanTour.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;

            case R.id.createPOD:
                //buttonAddPODOnClick(v);
                break;

            case R.id.createPOI:
                //buttonAddPOIOnClick(v);
                break;

            case R.id.POIPODlist:

                Intent intent1 = new Intent(this, ListView.class);
                startActivityForResult(intent1, PICK_CONTACT_REQUEST);
                break;

            case R.id.Options:
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


    static final int PICK_CONTACT_REQUEST = 1;
}
