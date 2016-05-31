package com.tests.apostol.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 5/26/2016.
 */
public class MainDrawerAdapter extends BaseAdapter {
    private Context _context;
    private List<String> _menuStrings;
    private List<Integer> _quantities;
    private List<Integer> _imageResources;

    public MainDrawerAdapter(Context context, List<String> navStrings, List<Integer> imageResources)
    {
        _context = context;
        _menuStrings = navStrings;
        _imageResources = imageResources;
    }

    @Override
    public int getCount() {
        return _menuStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return _menuStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null)
            v = ((LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.li_main, null);

        TextView name = (TextView) v.findViewById(R.id.item_nav_text);
        name.setText(getItem(position).toString());

        ImageView image = (ImageView) v.findViewById(R.id.item_nav_icon);
        image.setImageResource(_imageResources.get(position));
        return v;
    }
}