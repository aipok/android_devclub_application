package com.divapps.aipok.devclub.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Vitali Nartov on 10.06.2015.
 * Feed item model
 */
public class ItemModel extends BaseObservable {
    private String title;
    private String publicationDate;
    private String summary;
    private String duration;
    private String mediaUrl;
    private String imageUrl;

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Bindable
    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Bindable
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
