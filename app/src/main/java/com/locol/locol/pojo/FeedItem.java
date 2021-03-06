package com.locol.locol.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GiapNV on 3/11/15.
 * Project LocoL
 */
public class FeedItem implements Parcelable, Comparable<FeedItem> {
    private String id;
    private String title;
    private String startDate;
    private String endDate;
    private String urlThumbnail;
    private String location;
    private String category;
    private String max_participants;
    private String organizer;
    private String description;

    public int isLoved() {
        return loved;
    }

    public void setLoved(int loved) {
        this.loved = loved;
    }

    public int isJoining() {
        return joining;
    }

    public void setJoining(int joining) {
        this.joining = joining;
    }

    private int loved;
    private int joining;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaxParticipants() {
        return max_participants;
    }

    public void setMaxParticipants(String max_participants) {
        this.max_participants = max_participants;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getId() + "\n" + getTitle() + "\n" + getDescription() + "\n" + getLocation() + "\n" + getUrlThumbnail();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(urlThumbnail);
        dest.writeString(location);
        dest.writeString(category);
        dest.writeString(max_participants);
        dest.writeString(description);
    }

    @Override
    public int compareTo(@NonNull FeedItem another) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date thisDate = null, anotherDate = null;
        try {
            thisDate = dateFormat.parse(this.getStartDate());
            anotherDate = dateFormat.parse(another.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return thisDate != null ? -thisDate.compareTo(anotherDate) : 0;
    }
}
