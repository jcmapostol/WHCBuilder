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
public class DecklistAdapter extends BaseAdapter {
    private Context _context;
    private List<String> _data;
    private List<Integer> _quantities;
    private List<Integer> _imageResources;

    public DecklistAdapter(Context context, List<String> data, List<Integer> imageResources, List<Integer> qtys)
    {
        _context = context;
        _data = data;
        _quantities = qtys;
        _imageResources = imageResources;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null)
            v = ((LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.li_draft, null);

        TextView name = (TextView) v.findViewById(R.id.item_draft_name);
        name.setText(getItem(position).toString());

        TextView qty = (TextView) v.findViewById(R.id.btn_draft_qty);
        qty.setText(String.valueOf(_quantities.get(position)));

        ImageView image = (ImageView) v.findViewById(R.id.item_draft_faction);
        image.setImageResource(_imageResources.get(position));

        return v;
    }
}