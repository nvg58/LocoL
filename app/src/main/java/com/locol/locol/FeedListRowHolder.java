package com.locol.locol;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import butterknife.InjectView;

/**
 * Created by GiapNV on 3/11/15.
 * Project LocoL
 */
public class FeedListRowHolder extends RecyclerView.ViewHolder {
    protected NetworkImageView thumbnail;
    protected TextView title;
    protected TextView date;
    protected TextView place;
    protected TextView category;
    protected Button btnDetails;
    protected LinearLayout body;
    protected ImageButton btnLove;
    protected boolean isLoved;

    public FeedListRowHolder(View itemView) {
        super(itemView);
        this.thumbnail = (NetworkImageView) itemView.findViewById(R.id.eventThumbnail);
        this.title = (TextView) itemView.findViewById(R.id.eventTitle);
        this.date = (TextView) itemView.findViewById(R.id.eventDate);
        this.place = (TextView) itemView.findViewById(R.id.eventPlace);
        this.category = (TextView) itemView.findViewById(R.id.eventCategory);
        this.btnDetails= (Button) itemView.findViewById(R.id.btnDetails);
        this.body = (LinearLayout) itemView.findViewById(R.id.eventBody);
        this.btnLove = (ImageButton) itemView.findViewById(R.id.btnLove);
        this.isLoved = false;
    }
}
