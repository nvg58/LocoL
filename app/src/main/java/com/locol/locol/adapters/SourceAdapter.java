package com.locol.locol.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.locol.locol.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.lang.reflect.Field;

/**
 * Created by GiapNV on 5/1/15.
 * Project LocoL
 */
public class SourceAdapter extends ParseQueryAdapter<ParseObject> {
    private boolean careToColor = true;

    public SourceAdapter(Context context, QueryFactory<ParseObject> parseQuery, boolean careToColor) {
        super(context, parseQuery);
        this.careToColor = careToColor;
    }

    public SourceAdapter(Context context, QueryFactory<ParseObject> parseQuery) {
        super(context, parseQuery);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_item, parent, false);
        }
        super.getItemView(object, v, parent);

        ImageView image = (ImageView) v.findViewById(R.id.image);
        Drawable d = null;

        try {
            Class res = R.drawable.class;
            Field field = res.getField(object.getString("slug"));
            int drawableId = field.getInt(null);
            d = getContext().getResources().getDrawable(drawableId);
        } catch (Exception e) {
            Log.e("SourceAdapter", "Failure to get drawable id.", e);
        }

        image.setImageDrawable(d);

        TextView text = (TextView) v.findViewById(R.id.text);
        text.setText(object.getString("name"));

        TextView id = (TextView) v.findViewById(R.id.hidden_save_id);
        id.setText(object.getObjectId());

        return v;
    }

}
