package com.divapps.aipok.devclub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.network.FeedsRequest;
import com.divapps.aipok.devclub.views.LoadingView;


/**
 * A placeholder fragment containing a simple view.
 */
public class FeedListFragment extends Fragment {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private FeedsResponseModel model;
    private GridView gridView;
    private LoadingView loadingView;
    private ItemsAdapter adapter;

    public FeedListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(model == null)
            reloadFeeds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) v.findViewById(R.id.list);
        adapter = new ItemsAdapter(getActivity());
        gridView.setAdapter(adapter);
        loadingView = (LoadingView) v.findViewById(R.id.loading_view);
        return v;
    }

    public void reloadFeeds() {
        gridView.setVisibility(View.GONE);
        loadingView.show();
        //Start feed loading request...
        App.addRequestToQueueWithTag(App.getApplicationQueue(), new FeedsRequest(new Response.Listener<FeedsResponseModel>() {
            @Override
            public void onResponse(FeedsResponseModel response) {
                model = response;
                updateUI(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, error.getMessage());
                updateUI(false);
            }
        }), FeedsRequest.TAG);
    }

    private void updateUI(boolean success) {
        if(getView() != null) {
            adapter.notifyDataSetChanged();
            loadingView.hide();
            if (success)
                gridView.setVisibility(View.VISIBLE);
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
            if(convertView == null) {
                convertView = li.inflate(R.layout.feed_item, parent, false);
                Holder holder = new Holder();
                holder.titleView = (TextView) convertView.findViewById(R.id.title);
                holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
                holder.separatorView = convertView.findViewById(R.id.separator);
                convertView.setTag(holder);
            }
            final Holder holder = (Holder) convertView.getTag();
            final ItemModel model = getItem(position);
            if(model != null){
                holder.titleView.setText(TextUtils.isEmpty(model.title)? null: model.title);
                holder.titleView.setVisibility(TextUtils.isEmpty(model.title)? View.GONE: View.VISIBLE);

                holder.descriptionView.setText(TextUtils.isEmpty(model.description)? null: model.description);
                holder.descriptionView.setVisibility(TextUtils.isEmpty(model.description)? View.GONE: View.VISIBLE);
            }
            holder.separatorView.setVisibility(
                holder.titleView.getVisibility() == View.VISIBLE
             && holder.descriptionView.getVisibility() == View.VISIBLE
                    ? View.VISIBLE: View.GONE);

            return convertView;
        }

        private class Holder{
            TextView titleView;
            TextView descriptionView;
            View separatorView;
        }
    }
}
