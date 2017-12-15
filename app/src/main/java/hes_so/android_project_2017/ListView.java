package hes_so.android_project_2017;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.List;

import hes_so.android_project_2017.adapter.ArraySwipeAdapterSample;
import hes_so.android_project_2017.adapter.ListViewAdapter;

public class ListView extends AppCompatActivity {

    private android.widget.ListView mListView;
    private ListViewAdapter mAdapter;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("List View");
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_list_view);
        mListView = (android.widget.ListView) findViewById(R.id.listview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle("ListView");
            }
        }

        /**
         * The following comment is the sample usage of ArraySwipeAdapter.
         */
        List<PO> poList = LocalData.getPoList();

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
