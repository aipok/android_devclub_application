package com.divapps.aipok.devclub.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vitali Nartov on 10.06.2015.
 * Feed item model
 */
public class FeedsResponseModel {

    public String title;
    public String description;
    public final List<FeedModel> items;

    public FeedsResponseModel(){
        items = new ArrayList<>();
    }

}
