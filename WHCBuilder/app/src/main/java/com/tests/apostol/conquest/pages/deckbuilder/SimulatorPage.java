package com.tests.apostol.conquest.pages.deckbuilder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.Deck;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.pages.PageLifecyclable;
import com.tests.apostol.conquest.pages.draft.DraftPage;

import java.util.List;

public class SimulatorPage extends Fragment implements PageLifecyclable {
    private DeckbuilderPage _db;
    private Deck _deck;
    private RecyclerView _list;
    private Card _warlordCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.draft_page_sim, container, false);
        setRetainInstance(true);

        _list = (RecyclerView) v.findViewById(R.id.draft_page_sim_list);
        _warlordCard = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()))
                .getCardsByClause(getArguments().getString(DraftPage.SELECTED_WARLORD))
                .get(0);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.draft_page_sim_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_deck != null) {
                    List<ParentObject> data = DeckbuilderAdapterHelper.makeCardWrappers(_deck.shuffleAndDraw(_warlordCard.getStartingHand()));

                    if (_list.getAdapter() == null) {
                        _list.setAdapter(new SimulatorAdapter(data));
                        _list.setHasFixedSize(true);
                        _list.setLayoutManager(new LinearLayoutManager(getContext()));
                    } else {
                        SimulatorAdapter adapter = (SimulatorAdapter) _list.getAdapter();
                        adapter.overwriteParents(data);
                    }
                }
            }
        });

        return v;
    }

    public void setDeckbuilderDependency(DeckbuilderPage page) {
        _db = page;
    }

    @Override
    public void onShowPage() {
        if (_db != null)
            _deck = _db.getSimulatable();
    }

    @Override
    public void onHidePage() {

    }


}