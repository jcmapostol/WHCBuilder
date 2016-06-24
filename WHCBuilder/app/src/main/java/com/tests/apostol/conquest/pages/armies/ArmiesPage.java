package com.tests.apostol.conquest.pages.armies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.databases.DbContract;
import com.tests.apostol.conquest.pages.PageLifecyclable;

import java.util.ArrayList;
import java.util.List;

public class ArmiesPage extends Fragment implements PageLifecyclable {
    public static final String DECK_NAME = "com.tests.apostol.conquest.DECK_NAME";
    public static final String DECK_DESC = "com.tests.apostol.conquest.DECK_DESC";
    public static final String IS_EDITING = "com.tests.apostol.conquest.IS_EDITING";

    private DatabaseInterface _db;
    private RecyclerView _list;
    private TextView _blocker;
    private ProgressBar _progress;
    private boolean _wasPaused = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_arm, container, false);

        _db = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()));
        _list = (RecyclerView) v.findViewById(R.id.page_arm_list);
        _blocker = (TextView) v.findViewById(R.id.page_arm_blocker);
        _progress = (ProgressBar) v.findViewById(R.id.page_arm_progress);
        return v;
    }

    public void buildDecklists() {
        if (_db.tryQuery()) {
            Cursor c = _db.loadAllDecks();
            c.moveToFirst();
            List<ParentObject> decks = new ArrayList<>();

            if (c.getCount() > 0) {
                if (_wasPaused) {
                    _wasPaused = false;
                    while (!c.isAfterLast()) {
                        String deckName = c.getString(c.getColumnIndexOrThrow(DbContract.Preambles.NAME));
                        String deckDesc = c.getString(c.getColumnIndexOrThrow(DbContract.Preambles.DESC));
                        String warlordName = c.getString(c.getColumnIndexOrThrow(DbContract.Cards.NAME));
                        ArmyVhData wrapper = new ArmyVhData(deckName, deckDesc, _db.getCardsByClause(warlordName).get(0));
                        decks.add(wrapper);
                        c.moveToNext();
                    }
                    c.close();

                    if (_list.getAdapter() == null) {
                        _list.setAdapter(new ArmyPageListAdapter(decks, _db));
                        _list.setLayoutManager(new LinearLayoutManager(getContext()));
                        _list.setHasFixedSize(true);
                    } else {
                        ArmyPageListAdapter adapter = (ArmyPageListAdapter) _list.getAdapter();
                        adapter.overwriteParents(decks);
                    }

                    _list.setVisibility(View.VISIBLE);
                    _progress.setVisibility(View.INVISIBLE);
                    _blocker.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            _progress.setVisibility(View.INVISIBLE);
            _list.setVisibility(View.INVISIBLE);
            _blocker.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        super.onResume();

        if (_db != null) {
            buildDecklists();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _wasPaused = true;
    }

    @Override
    public void onShowPage() {
    }

    @Override
    public void onHidePage() {
    }
}