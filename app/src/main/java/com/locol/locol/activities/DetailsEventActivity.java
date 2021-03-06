package com.locol.locol.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.locol.locol.R;
import com.locol.locol.fragments.CommentFragment;
import com.locol.locol.helpers.GPSTracker;
import com.locol.locol.networks.VolleySingleton;
import com.locol.locol.pojo.Account;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DetailsEventActivity extends ActionBarActivity implements OnScrollChangedCallback {
    Bundle extras;

    private Toolbar mToolbar;
    private Drawable mActionBarBackgroundDrawable;
    private View mHeader;
    private int mLastDampedScroll;

    private final static String TAG = "DetailsEventActivity";

    private String title;
    private Long startDate;
    private Long endDate;
    private String place;
    private String category;
    private String maxParticipants;
    private String organizer;
    private String description;
    private String url_thumbnail;
    //    private int loved;
//    private int joining;
    private String event_id;
    private boolean from_push;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        extras = getIntent().getExtras();

        event_id = extras.getString("EXTRA_EVENT_ID");

        title = extras.getString("EXTRA_FEED_TITLE");
        startDate = extras.getLong("EXTRA_FEED_START_DATE");
        endDate = extras.getLong("EXTRA_FEED_END_DATE");
        place = extras.getString("EXTRA_FEED_PLACE");
        category = extras.getString("EXTRA_FEED_CATEGORY");
        maxParticipants = extras.getString("EXTRA_FEED_MAX_PARTICIPANTS");
        organizer = extras.getString("EXTRA_FEED_ORGANIZER");
        description = extras.getString("EXTRA_FEED_DESCRIPTION");
        url_thumbnail = extras.getString("EXTRA_FEED_URL_THUMBNAIL");

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mActionBarBackgroundDrawable = mToolbar.getBackground();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                findViewById(R.id.img_close_panel).setAlpha(slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                final CommentFragment fragment = (CommentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_comment);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                Log.wtf("event_id DetailsEvent", event_id);
                query.getInBackground(event_id, new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            fragment.setEvent(object);
                            fragment.getAdapter().loadObjects();
                        } else {
                            // something went wrong
                            e.printStackTrace();
                            Toast.makeText(DetailsEventActivity.this, "Can not get comments!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        mHeader = findViewById(R.id.event_thumbnail);
        ObservableScrollable scrollView = (ObservableScrollable) findViewById(R.id.scroll_event_details);
        scrollView.setOnScrollChangedCallback(this);
        onScroll(-1, 0);

        if (extras != null) {
            NetworkImageView thumbnail = (NetworkImageView) findViewById(R.id.event_thumbnail);
            thumbnail.setDefaultImageResId(R.drawable.placeholder);
            thumbnail.setImageUrl(url_thumbnail, VolleySingleton.getInstance().getImageLoader());

            final TextView tvTitle = (TextView) findViewById(R.id.event_title);
            tvTitle.setText(title);

            TextView tvPlace = (TextView) findViewById(R.id.event_place);
            tvPlace.setText(place);

            TextView tvDate = (TextView) findViewById(R.id.event_date);
            DateFormat newDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            Date sDate = new Date(startDate);
            Date eDate = new Date(endDate);
            tvDate.setText("From " + newDateFormat.format(sDate) + "\nto " + newDateFormat.format(eDate));

            WebView wvDetails = (WebView) findViewById(R.id.event_description);
            wvDetails.loadDataWithBaseURL(
                    null,
                    "<style>img {display: inline; height: auto; max-width: 100%;}</style>" + description,
                    "text/html",
                    "UTF-8",
                    null);

            TextView tvCat = (TextView) findViewById(R.id.event_category);
            tvCat.setText(category);

            TextView tvOrganizer = (TextView) findViewById(R.id.organizer);
            tvOrganizer.setText(organizer);

            Button btnAddToCalendar = (Button) findViewById(R.id.btn_add_to_calendar);
            btnAddToCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar start = DateToCalendar(new Date(startDate));
                    Calendar end = DateToCalendar(new Date(startDate));
                    addEvent(DetailsEventActivity.this, title, place, start, end);
                }
            });

            ImageButton btnDirection = (ImageButton) findViewById(R.id.btn_direction);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GPSTracker gps = new GPSTracker(DetailsEventActivity.this);
                    if (gps.canGetLocation()) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + gps.getLatitude() + "," + gps.getLongitude() + "&daddr=" + place));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } else {
                        gps.showSettingsAlert();
                    }
                }
            });

            final Button btnYes = (Button) findViewById(R.id.btn_yes);
            final TextView rvspTitle = (TextView) findViewById(R.id.rvsp_title);

            final ParseUser user = ParseUser.getCurrentUser();
            final ParseRelation<ParseObject> relation = user.getRelation("join");
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    relation.add(ParseObject.createWithoutData("Event", event_id));
                    user.saveEventually();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.fromLocalDatastore();
                    query.getInBackground(event_id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            parseObject.increment("joining");
                            parseObject.saveEventually();
                        }
                    });

                    btnYes.setVisibility(View.GONE);
                    rvspTitle.setText(R.string.going_text);
                    rvspTitle.setAllCaps(false);
                }
            });

            ParseQuery query = relation.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("objectId", event_id);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (!list.isEmpty()) {
                        btnYes.setVisibility(View.GONE);
                        rvspTitle.setText(R.string.going_text);
                        rvspTitle.setAllCaps(false);
                    }
                }
            });
        }
    }

    public void addEvent(Context ctx, String title, String place, Calendar start, Calendar end) {
        Log.d(TAG, "AddUsingIntent.addEvent()");
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", title);
        intent.putExtra("eventLocation", place);
        intent.putExtra("beginTime", start.getTimeInMillis());
        intent.putExtra("endTime", end.getTimeInMillis());
        intent.putExtra("allDay", false);
        ctx.startActivity(intent);
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details_event, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(title)
                        .setContentDescription(Account.getUserName() + " has using LocoL. Install to explore more!")
//                                Account.getUserName() + " has loved \"" + title + "\"")
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                        .setImageUrl(Uri.parse(url_thumbnail))
                        .build();

                shareDialog.show(linkContent);
            }
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "onResume");
    }

    @Override
    public void onScroll(int l, int scrollPosition) {
        int headerHeight = mHeader.getHeight() - mToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateActionBarTransparency(ratio);
        updateParallaxEffect(scrollPosition);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        mToolbar.setBackground(mActionBarBackgroundDrawable);
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeader.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() == 0) {
//            this.finish();
//        } else {
//            getFragmentManager().popBackStack();
//        }

        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
