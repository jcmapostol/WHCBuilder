package com.tests.apostol.testapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DraftFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_draft, container, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAdded()) {
            View v = getView();
            TabLayout tl = (TabLayout) v.findViewById(R.id.page_draft_tab);
            tl.addTab(tl.newTab().setIcon(R.drawable.icon_crown));
            tl.addTab(tl.newTab().setIcon(R.drawable.icon_person));
            tl.addTab(tl.newTab().setIcon(R.drawable.icon_bolt));
            tl.addTab(tl.newTab().setIcon(R.drawable.icon_sword));
            tl.addTab(tl.newTab().setIcon(R.drawable.icon_castle));
        }
    }
}