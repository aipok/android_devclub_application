package com.divapps.aipok.devclub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.divapps.aipok.devclub.databinding.FeedItemBinding;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.network.FeedsRequest;


/**
 * A placeholder fragment containing a simple view.
 */
public class FeedListFragment extends Fragment
        implements AdapterView.OnItemClickListener,
                   View.OnClickListener,
                   SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private static final int UNSELECTED = -1;
    private FeedsResponseModel model;
    private GridView gridView;
    private ItemsAdapter adapter;
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
        adapter = new ItemsAdapter(getActivity());
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

            backgroundView.setImageUrl(model.coverImage, App.getLoader());

            adapter.notifyDataSetChanged();
            if (success)
                gridView.setVisibility(View.VISIBLE);
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

    private class ItemsAdapter extends BaseAdapter{

        private final LayoutInflater li;

        public ItemsAdapter(Context context) {
            li = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return model != null ? model.items.size(): 0;
        }

        @Override
        public ItemModel getItem(int position) {
            try{
                return model.items.get(position);
            }catch (NullPointerException | ArrayIndexOutOfBoundsException e){
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FeedItemBinding holder;
            if(convertView == null) {
                holder = FeedItemBinding.inflate(li, parent, false);
                convertView = holder.getRoot();
                convertView.setTag(holder);
            }else
                holder = (FeedItemBinding) convertView.getTag();

            holder.play.setTag(position);
            final ItemModel model = getItem(position);
            holder.setItem(model);
            if(App.isPhone() && getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                ImageUtils.Size size = ImageUtils.calculateSizeBasedOnWidthAndAspectRatio(convertView.getMeasuredWidth(), 615, 461);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size.getWidth(), size.getHeight());
                holder.image.setLayoutParams(params);
            }
            if(model != null){
                if(!TextUtils.isEmpty(model.getImageUrl())){
                    holder.image.setImageUrl(model.getImageUrl(), App.getLoader());
                }else{
                    holder.image.setImageUrl(null, App.getLoader());
                }
            }
            holder.separator.setVisibility(App.isTablet() && holder.title.getVisibility() == View.VISIBLE && holder.description.getVisibility() == View.VISIBLE
                    ? View.VISIBLE: View.GONE);

            return convertView;
        }
    }
}
