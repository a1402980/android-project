package hes_so.santour;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.List;

import hes_so.santour.adapter.ArraySwipeAdapterSample;
import hes_so.santour.adapter.ListViewAdapter;

public class ListView extends AppCompatActivity {

    private android.widget.ListView mListView;
    private ListViewAdapter mAdapter;
    private Context mContext = this;
    private  List<PO> poList;

    //check if this activity is active
    static boolean active = false;

    @Override
    protected void onStart() {
        super.onStart();
        active = true;

        mListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(poList.size() == mListView.getChildCount())
                {
                    for (int i = 0; i< mListView.getChildCount(); i++)
                    {
                        if (poList.get(i).isPOI()) {
                            LinearLayout ll =  (LinearLayout)((SwipeLayout)mListView.getChildAt(i)).getChildAt(1);
                            ll.setBackgroundColor(Color.BLUE);
                        }else
                        {
                           LinearLayout ll =  (LinearLayout)((SwipeLayout)mListView.getChildAt(i)).getChildAt(1);
                           ll.setBackgroundColor(Color.RED);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("List View");
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_list_view);
        mListView = (android.widget.ListView) findViewById(R.id.listview2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle("ListView");
            }
        }

        /**
         * The following comment is the sample usage of ArraySwipeAdapter.
         */
        poList = LocalData.getPoList();

        if(poList == null)
            poList = new ArrayList<>();

        mAdapter = new ListViewAdapter(mContext);
        refreshData(poList);


        if (mAdapter != null) {
            mAdapter.setMode(Attributes.Mode.Single);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout)(mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });



        NavigationView nvDrawer = (NavigationView) findViewById(R.id.navigation);
        nvDrawer.setCheckedItem(R.id.POIPODlist);
        drawerSetup(nvDrawer);
    }


    public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        //Class fragmentClass;
        View  v = this.findViewById(android.R.id.content).getRootView();
        switch (menuItem.getItemId()){
            case R.id.createTrack:
                if (!active){
                    buttonSanTourClick(v);
                }

                break;

            case R.id.createPOD:
                buttonAddPODOnClick(v);
                break;

            case R.id.createPOI:
                buttonAddPOIOnClick(v);
                break;

            case R.id.POIPODlist:

                Intent intent = new Intent(this, ListView.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;

            case R.id.Options:
                Intent intent2 = new Intent(this, Options.class);
                startActivityForResult(intent2, PICK_CONTACT_REQUEST);
                break;


            default:
                buttonSanTourClick(v);
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
    public void buttonSanTourClick(View v){
        Intent intent = new Intent(ListView.this, SanTour.class);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonAddPOIOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(ListView.this, AddPoi.class);
        String longitude = LocalData.getActuellLongitute();
        String latitude = LocalData.getActuellLangitude();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void buttonAddPODOnClick(View v) {
        LocalData.setTimerIsRunning(false);
        Intent intent = new Intent(ListView.this, AddPod.class);
        String longitude = LocalData.getActuellLongitute();
        String latitude = LocalData.getActuellLangitude();
        intent.putExtra("longitudeData", longitude);
        intent.putExtra("latitudeData", latitude);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }



    private void refreshData(List<PO> poList) {
        String[] adapterData = new String[poList.size()];
        for (int i = 0; i< poList.size(); i++)
        {
            adapterData[i] = poList.get(i).getId();

        }
        //mAdapter = new ListViewAdapter(this);
        //mListView.setAdapter(mAdapter);
        //mAdapter.setMode(Attributes.Mode.Single);

        mListView.setAdapter(new ArraySwipeAdapterSample<String>(this, R.layout.listview_item, R.id.position, adapterData));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }


    public void onDeletePO(View view) {
        SwipeLayout swipe = (SwipeLayout) ((ViewGroup) view.getParent()).getParent();
        LinearLayout ll = (LinearLayout) swipe.getChildAt(1);

        TextView tv = null;
        for (int i = 0; i<ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            if (v instanceof AppCompatTextView) {
                if ((v.getId()+"").equals(R.id.position+"")) ;
                {
                    tv = (TextView) v;
                    break;
                }
            }
        }
        if (tv != null ) {
            String info = tv.getText().toString();
            Log.d("POI NAME", tv.getText().toString());
            LocalData.removePO(info);
        }
        refreshData(LocalData.getPoList());
        mAdapter.notifyDatasetChanged();
    }






}
