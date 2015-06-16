package com.divapps.aipok.devclub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.divapps.aipok.devclub.models.FeedsResponseModel;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.network.FeedsRequest;
import com.divapps.aipok.devclub.views.LoadingView;


/**
 * A placeholder fragment containing a simple view.
 */
public class FeedListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private static final int UNSELECTED = -1;
    private FeedsResponseModel model;
    private GridView gridView;
    private LoadingView loadingView;
    private ItemsAdapter adapter;
    private int currentSelectedItem = UNSELECTED;
    private NetworkImageView backgroundView;

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
        loadingView = (LoadingView) v.findViewById(R.id.loading_view);
        backgroundView = (NetworkImageView) v.findViewById(R.id.background);
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

            backgroundView.setImageUrl(model.coverImage, App.getLoader());

            adapter.notifyDataSetChanged();
            loadingView.hide();
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
            intent.putExtra(Player.URL_TAG, model.mediaUrl);
            startActivity(intent);
            Log.d(TAG, "Play button clicked and movie: " + model.mediaUrl + " will be started soon");
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
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.image = (NetworkImageView) convertView.findViewById(R.id.image);
                holder.play = (ImageButton) convertView.findViewById(R.id.play);
                holder.play.setOnClickListener(FeedListFragment.this);
                convertView.setTag(holder);
            }
            final Holder holder = (Holder) convertView.getTag();
            holder.play.setTag(position);
            final ItemModel model = getItem(position);
            if(model != null){
                holder.titleView.setText(TextUtils.isEmpty(model.title) ? null : model.title);
                holder.titleView.setVisibility(TextUtils.isEmpty(model.title) ? View.GONE : View.VISIBLE);

                holder.descriptionView.setText(TextUtils.isEmpty(model.summary) ? null : model.summary);
                holder.descriptionView.setVisibility(TextUtils.isEmpty(model.summary) ? View.GONE : View.VISIBLE);

                holder.date.setText(TextUtils.isEmpty(model.publicationDate) ? null : String.format("Posted: %s", model.publicationDate));
                holder.date.setVisibility(TextUtils.isEmpty(model.publicationDate) ? View.GONE : View.VISIBLE);

                if(!TextUtils.isEmpty(model.imageUrl)){
                    holder.image.setImageUrl(model.imageUrl, App.getLoader());
                }else{
                    holder.image.setImageUrl(null, App.getLoader());
                }
            }
            holder.separatorView.setVisibility(holder.titleView.getVisibility() == View.VISIBLE && holder.descriptionView.getVisibility() == View.VISIBLE
                    ? View.VISIBLE: View.GONE);

            final CardView  cardView = (CardView) convertView;
            cardView.setCardElevation(5.0f);
            ((CardView)convertView).setMaxCardElevation(10.0f);

            return convertView;
        }

        private class Holder{
            TextView titleView, descriptionView, date;
            View separatorView;
            NetworkImageView image;
            ImageButton play;
        }
    }
}
