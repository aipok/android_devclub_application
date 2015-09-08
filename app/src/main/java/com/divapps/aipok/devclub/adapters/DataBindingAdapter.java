package com.divapps.aipok.devclub.adapters;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.divapps.aipok.devclub.application.App;
import com.divapps.aipok.devclub.databinding.FeedItemBinding;
import com.divapps.aipok.devclub.fragments.FeedListFragment;
import com.divapps.aipok.devclub.models.ItemModel;
import com.divapps.aipok.devclub.utils.ImageUtils;

/**
 * Created by Vitali Nartov on 08/09/15.
 * Adapter with DataBinding support
 */
public class DataBindingAdapter extends BaseItemAdapter {

    public DataBindingAdapter(FeedListFragment fragment) {
        super(fragment);
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
        holder.setFragment(fragment);
        if(App.isPhone() && context.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
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
        holder.separator.setVisibility(App.isTablet()
                && holder.title.getVisibility() == View.VISIBLE
                && holder.description.getVisibility() == View.VISIBLE
                ? View.VISIBLE: View.GONE);

        return convertView;
    }
}
