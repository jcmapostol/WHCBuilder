package com.tests.apostol.testapp;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by user on 5/26/2016.
 */
public class LexicanumDrawerAdapter extends SimpleCursorAdapter {
    public LexicanumDrawerAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags)
    {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        ImageView faction = (ImageView) view.findViewById(R.id.item_lex_faction);
        TextView name = (TextView) view.findViewById(R.id.item_lex_name);

        String nameText = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String factionText = cursor.getString(cursor.getColumnIndexOrThrow("faction"));

        name.setText(nameText);

        switch (factionText) {
            case "AM":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                faction.setImageResource(R.drawable.icon_astra_militarum);
                break;
            case "CH":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#CC3700"));
                faction.setImageResource(R.drawable.icon_chaos);
                break;
            case "DE":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#8A2BE2"));
                faction.setImageResource(R.drawable.icon_dark_eldar);
                break;
            case "EL":
                name.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.parseColor("#E5E500"));
                faction.setImageResource(R.drawable.icon_eldar_dark);
                break;
            case "OR":
                name.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.parseColor("#00CC00"));
                faction.setImageResource(R.drawable.icon_orks);
                break;
            case "NE":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#006400"));
                faction.setImageResource(R.drawable.icon_necrons);
                break;
            case "SM":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#000099"));
                faction.setImageResource(R.drawable.icon_space_marines);
                break;
            case "TA":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#00CCCC"));
                faction.setImageResource(R.drawable.icon_tau_dark);
                break;
            case "TY":
                name.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#660000"));
                faction.setImageResource(R.drawable.icon_tyranids);
                break;
            case "NA":
                name.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.WHITE);
                faction.setImageResource(R.drawable.icon_blank);
                break;
        }
    }
}