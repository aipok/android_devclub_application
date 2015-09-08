package com.divapps.aipok.devclub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.divapps.aipok.devclub.fragments.FeedListFragment;
import com.divapps.aipok.devclub.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vitali Nartov on 08/09/15.
 *
 */
public abstract class BaseItemAdapter extends BaseAdapter {
    protected final FeedListFragment fragment;
    protected final Context context;
    protected final LayoutInflater li;
    protected final List<ItemModel> items = new ArrayList<>();


    public BaseItemAdapter(FeedListFragment fragment) {
        li = LayoutInflater.from(fragment.getActivity());
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    public void updateItems(List<ItemModel> items){
        if(this.items.size() > 0) this.items.clear();

        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items != null ? items.size(): 0;
    }

    @Override
    public ItemModel getItem(int position) {
        try{
            return items.get(position);
        }catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
