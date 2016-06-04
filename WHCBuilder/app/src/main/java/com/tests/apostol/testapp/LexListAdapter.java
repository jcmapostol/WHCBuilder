package com.tests.apostol.testapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LexListAdapter extends SimpleCursorAdapter {
    public LexListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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

        Card.Factions fac = Card.Factions.valueOf(factionText);
        name.setTextColor(fac.getTextColor());
        view.setBackgroundColor(fac.getColor());
        faction.setImageResource(fac.getIconId());
    }
}