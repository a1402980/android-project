package hes_so.android_project_2017.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;


import java.util.List;

import hes_so.android_project_2017.R;

/**
 * Sample usage of ArraySwipeAdapter.
 * @param <T>
 */
public class ArraySwipeAdapterSample<T> extends ArraySwipeAdapter {
    public ArraySwipeAdapterSample(Context context, int resource) {
        super(context, resource);
    }

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArraySwipeAdapterSample(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    private Context mContext;

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);

    }

    public ArraySwipeAdapterSample(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public ArraySwipeAdapterSample(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
