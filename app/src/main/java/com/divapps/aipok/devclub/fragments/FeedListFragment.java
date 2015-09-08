package com.divapps.aipok.devclub.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.divapps.aipok.devclub.R;
import com.divapps.aipok.devclub.activities.MainActivity;
import com.divapps.aipok.devclub.activities.Player;
import com.divapps.aipok.devclub.adapters.BaseItemAdapter;
import com.divapps.aipok.devclub.adapters.DataBindingAdapter;
import com.divapps.aipok.devclub.adapters.ItemAdapter;
import com.divapps.aipok.devclub.application.App;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.network.FeedsRequest;


/**
 * A placeholder fragment containing a simple view.
 */
public class FeedListFragment extends Fragment
        implements AdapterView.OnItemClickListener,
                   SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private static final int UNSELECTED = -1;
    private FeedsResponseModel model;
    private GridView gridView;
    private BaseItemAdapter adapter;
    private int currentSelectedItem = UNSELECTED;
    private NetworkImageView backgroundView;
    private SwipeRefreshLayout swipeLayout;

    public FeedListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        gridView.setOnItemClickListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) v.findViewById(R.id.list);
        adapter = new DataBindingAdapter(FeedListFragment.this);
        gridView.setAdapter(adapter);
        backgroundView = (NetworkImageView) v.findViewById(R.id.background);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return v;
    }

    @Override public void onRefresh() {
        reloadFeeds();
    }

    public void reloadFeeds() {
        swipeLayout.setRefreshing(true);
        //Start feed loading request...
        App.addRequestToQueueWithTag(App.getApplicationQueue(), new FeedsRequest(new Response.Listener<FeedsResponseModel>() {
            @Override
            public void onResponse(FeedsResponseModel response) {
                model = response;
                updateUI(true);
                swipeLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, error.getMessage());
                updateUI(false);
                swipeLayout.setRefreshing(false);
            }
        }), FeedsRequest.TAG);
    }

    private void updateUI(boolean success) {
        if(getView() != null) {
            if (success) {
                adapter.updateItems(model.items);
                backgroundView.setImageUrl(model.coverImage, App.getLoader());
                gridView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(currentSelectedItem == position)
            currentSelectedItem = UNSELECTED;
        else
            currentSelectedItem = position;
        adapter.notifyDataSetChanged();
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.play){
                final ItemModel model = adapter.getItem((Integer) v.getTag());
                Intent intent = new Intent(getActivity(), Player.class);
                intent.putExtra(Player.URL_TAG, model.getMediaUrl());
                startActivity(intent);
                Log.d(TAG, "Play button clicked and movie: " + model.getMediaUrl() + " will be started soon");
            }
        }
    };

    public void updateCollectionView() {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean current = pm.getBoolean(MainActivity.KEY_VIEW_REPRESENTATION, false);
        adapter = new ItemAdapter(FeedListFragment.this, current);

        if(current) {
            gridView.setNumColumns(getResources().getInteger(R.integer.items_per_row));
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setColumnWidth(100);
            int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.collection_horizontal_padding_multi_rows);
            int verticalPadding = getResources().getDimensionPixelSize(R.dimen.collection_vertical_padding);
            gridView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }else{
            gridView.setNumColumns(1);
            int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.collection_horizontal_padding);
            int verticalPadding = getResources().getDimensionPixelSize(R.dimen.collection_horizontal_padding);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
