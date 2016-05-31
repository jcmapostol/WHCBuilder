package com.tests.apostol.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 5/26/2016.
 */
public class DeckbuilderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deckbuilder, container, false);

        List<String> sig = new ArrayList<>();
        sig.add("Sicarius' Chosen");
        sig.add("Fury of Sicarius");
        sig.add("Cato's Stronghold");
        sig.add("Tallassarian Tempest Blade");

        List<Integer> imgRes = new ArrayList<>();
        imgRes.add(R.drawable.icon_space_marines);
        imgRes.add(R.drawable.icon_space_marines);
        imgRes.add(R.drawable.icon_space_marines);
        imgRes.add(R.drawable.icon_space_marines);

        List<String> cho = new ArrayList<>();
        cho.add("Drop Pod Assault");

        List<Integer> choImg = new ArrayList<>();
        choImg.add(R.drawable.icon_space_marines);

        DecklistAdapter ca1 = new DecklistAdapter(getActivity(), sig, imgRes, new ArrayList<Integer>(Arrays.asList(4, 2, 1, 1)));
        DecklistAdapter ca2 = new DecklistAdapter(getActivity(), cho, choImg, new ArrayList<Integer>(Arrays.asList(3)));

        ListView lv1 = (ListView) v.findViewById(R.id.lv_deck_signature);
        ListView lv2 = (ListView) v.findViewById(R.id.lv_deck_chosen);

        lv1.setAdapter(ca1);
        lv2.setAdapter(ca2);

        TextView warlord = (TextView) v.findViewById(R.id.tv_warlord_name);
        warlord.setText("Captain Cato Sicarius");

        ImageView faction = (ImageView) v.findViewById(R.id.iv_warlord_faction);
        faction.setImageResource(R.drawable.icon_space_marines);

        ImageView faction2 = (ImageView) v.findViewById(R.id.iv_warlord_faction2);
        faction2.setImageResource(R.drawable.icon_space_marines);

        return v;
    }
}