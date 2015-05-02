package com.locol.locol.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.locol.locol.viewa.ExpandableHeightGridView;
import com.locol.locol.viewa.ExpandableHeightListView;
import com.locol.locol.R;
import com.locol.locol.activities.ComingSoonActivity;
import com.locol.locol.activities.MostFavouriteActivity;
import com.locol.locol.activities.NewEventsActivity;
import com.locol.locol.activities.TrendingActivity;
import com.locol.locol.adapters.CategoryAdapter;
import com.locol.locol.adapters.ListCategoryAdapter;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {


    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        TextView btn_trending = (TextView) v.findViewById(R.id.btn_trending);
        btn_trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TrendingActivity.class));
            }
        });

        TextView btn_new = (TextView) v.findViewById(R.id.btn_new);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewEventsActivity.class));
            }
        });

        TextView btn_coming_soon = (TextView) v.findViewById(R.id.btn_coming_soon);
        btn_coming_soon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ComingSoonActivity.class));
            }
        });

        TextView btn_most_favourite = (TextView) v.findViewById(R.id.btn_most_favourite);
        btn_most_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MostFavouriteActivity.class));
            }
        });

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        ExpandableHeightGridView gridview = (ExpandableHeightGridView) v.findViewById(R.id.gridview);
        gridview.setExpanded(true);

        ParseUser user = ParseUser.getCurrentUser();
        final ParseRelation<ParseObject> relation = user.getRelation("interest");
        ParseQueryAdapter.QueryFactory<ParseObject> parseQuery = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                return relation.getQuery();
            }
        };
        final CategoryAdapter adapter = new CategoryAdapter(getActivity(), parseQuery, false);
        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            @Override
            public void onLoading() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseObject> list, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // start Category activity
            }
        });

        // all categories
        final ListCategoryAdapter listCategoryAdapter = new ListCategoryAdapter(getActivity(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Category");
                return query;
            }
        });
        ExpandableHeightListView lv= (ExpandableHeightListView) v.findViewById(R.id.list_view);
        lv.setExpanded(true);
        lv.setAdapter(listCategoryAdapter);
        listCategoryAdapter.loadObjects();

        return v;
    }


}
