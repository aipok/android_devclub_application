package com.divapps.aipok.devclub.adapters;

import android.content.pm.ActivityInfo;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.divapps.aipok.devclub.R;
import com.divapps.aipok.devclub.application.App;
import com.divapps.aipok.devclub.fragments.FeedListFragment;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.utils.ImageUtils;

/**
 * Created by Vitali Nartov on 08/09/15.
 * Basic feed item adapter
 */
public class ItemAdapter extends BaseItemAdapter {
    private final int layout;

    public ItemAdapter(FeedListFragment fragment, boolean isGrid) {
        super(fragment);
        layout = isGrid ? R.layout.feed_item_grid: R.layout.feed_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = li.inflate(layout, parent, false);
            Holder holder = new Holder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.image = (NetworkImageView) convertView.findViewById(R.id.image);
            holder.play = (ImageButton) convertView.findViewById(R.id.play);
            holder.play.setOnClickListener((View.OnClickListener) fragment);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
            holder.separatorView = convertView.findViewById(R.id.separator);
            if(layout == R.layout.feed_item_grid){
                ImageUtils.Size size = ImageUtils.calculateSizeBasedOnWidthAndAspectRatio(convertView.getMeasuredWidth(), 615, 461);
                convertView.setMinimumHeight(size.getHeight());
            }
            convertView.setTag(holder);
        }
        final Holder holder = (Holder) convertView.getTag();
        holder.play.setTag(position);
        final ItemModel model = getItem(position);

        if(App.isPhone() && context.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            ImageUtils.Size size = ImageUtils.calculateSizeBasedOnWidthAndAspectRatio(convertView.getMeasuredWidth(), 615, 461);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size.getWidth(), size.getHeight());
            holder.image.setLayoutParams(params);
        }
        if(model != null){
            holder.titleView.setText(TextUtils.isEmpty(model.getTitle()) ? null : model.getTitle());
            holder.titleView.setVisibility(TextUtils.isEmpty(model.getTitle()) ? View.GONE : View.VISIBLE);

            if(holder.descriptionView != null) {
                holder.descriptionView.setText(TextUtils.isEmpty(model.getSummary()) ? null : model.getSummary());
                holder.descriptionView.setVisibility(TextUtils.isEmpty(model.getSummary()) ? View.GONE : View.VISIBLE);
            }
            holder.date.setText(TextUtils.isEmpty(model.getPublicationDate()) ? null
                    : String.format("Posted: %s", model.getPublicationDate()));
            holder.date.setVisibility(TextUtils.isEmpty(model.getPublicationDate()) ? View.GONE : View.VISIBLE);

            if(!TextUtils.isEmpty(model.getImageUrl())){
                holder.image.setImageUrl(model.getImageUrl(), App.getLoader());
            }else{
                holder.image.setImageUrl(null, App.getLoader());
            }
        }
        if(holder.separatorView != null)
            holder.separatorView.setVisibility(App.isTablet()
                    && holder.titleView.getVisibility() == View.VISIBLE
                    && holder.descriptionView.getVisibility() == View.VISIBLE
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
