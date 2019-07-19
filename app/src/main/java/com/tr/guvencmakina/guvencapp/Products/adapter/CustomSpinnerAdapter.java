package com.tr.guvencmakina.guvencapp.Products.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.tr.guvencmakina.guvencapp.R;

import java.util.List;


public class CustomSpinnerAdapter extends BaseAdapter {
    int color1;
    private Context mContext;
    private List<String> vals;

    public CustomSpinnerAdapter(Context c, List<String> list) {
        mContext = c;
        this.vals = list;
    }


    public int getCount() {
        return vals.size();
    }

    public Object getItem(int position) {
        return vals.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View MyView = convertView;
        //Inflate the layout
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MyView = li.inflate(R.layout.list_item0, null);

        // Add The Text!!!
        TextView tv = (TextView) MyView.findViewById(R.id.item1);
        ImageView image = (ImageView) MyView.findViewById(R.id.image_view);
        tv.setText(vals.get(position));
        tv.setTextSize(13f);

        String title = vals.get(position);

        if (title != null && title.length() > 0) {
            ColorGenerator generator = ColorGenerator.DEFAULT;
            color1 = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound((String.valueOf(title.charAt(0))), color1);

            image.setImageDrawable(drawable);
        }

        return MyView;
    }

}
