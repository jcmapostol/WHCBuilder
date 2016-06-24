package com.tests.apostol.conquest.pages.draft;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tests.apostol.conquest.HeaderAdapterHelper;
import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.HeaderAdapter;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.activities.DraftActivity;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.pages.PageLifecyclable;

import java.util.ArrayList;
import java.util.List;

public class DraftPage extends Fragment implements PageLifecyclable {
    public final static String SELECTED_WARLORD = "com.tests.apostol.conquest.SELECTED_WARLORD";

    private DatabaseInterface _cm;
    private List<Card.Factions> _factionFilter;

    private View _root;
    private ProgressBar _rootBlocker;
    private RecyclerView _warlordList;
    private int _count = 9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _cm = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()));
        _factionFilter = new ArrayList<>();

        View v = inflater.inflate(R.layout.page_draft, container, false);

        _root = v.findViewById(R.id.page_draft_root);
        _rootBlocker = (ProgressBar) v.findViewById(R.id.page_draft_progress);
        _warlordList = (RecyclerView) v.findViewById(R.id.page_draft_list);

        ImageButton am = (ImageButton) v.findViewById(R.id.page_draft_am);
        ImageButton ch = (ImageButton) v.findViewById(R.id.page_draft_ch);
        ImageButton de = (ImageButton) v.findViewById(R.id.page_draft_de);
        ImageButton el = (ImageButton) v.findViewById(R.id.page_draft_el);
        ImageButton ne = (ImageButton) v.findViewById(R.id.page_draft_ne);
        ImageButton or = (ImageButton) v.findViewById(R.id.page_draft_or);
        ImageButton sm = (ImageButton) v.findViewById(R.id.page_draft_sm);
        ImageButton ta = (ImageButton) v.findViewById(R.id.page_draft_ta);
        ImageButton ty = (ImageButton) v.findViewById(R.id.page_draft_ty);

        am.setOnClickListener(new FactionSelectorListener(Card.Factions.AM));
        ch.setOnClickListener(new FactionSelectorListener(Card.Factions.CH));
        de.setOnClickListener(new FactionSelectorListener(Card.Factions.DE));
        el.setOnClickListener(new FactionSelectorListener(Card.Factions.EL));
        ne.setOnClickListener(new FactionSelectorListener(Card.Factions.NE));
        or.setOnClickListener(new FactionSelectorListener(Card.Factions.OS));
        sm.setOnClickListener(new FactionSelectorListener(Card.Factions.SM));
        ta.setOnClickListener(new FactionSelectorListener(Card.Factions.TA));
        ty.setOnClickListener(new FactionSelectorListener(Card.Factions.TY));
        return v;
    }

    @Override
    public void onShowPage() {
        getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (_cm.tryQuery()) {
                        _rootBlocker.setVisibility(View.INVISIBLE);
                        _root.setVisibility(View.VISIBLE);
                        if (_count != _factionFilter.size()) {
                            _count = _factionFilter.size();
                            _cm.setFactionFilter(_factionFilter);
                            buildList();
                        }
                    } else {
                        Toast.makeText(getContext(), "Wait for card processing to finish.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);
    }

    @Override
    public void onHidePage() {

    }

    private void buildList() {
        List<ParentObject> headers = HeaderAdapterHelper.makeHeadersByFaction(_cm.getCardsByType(Card.Types.WR));

        if (_warlordList.getAdapter() != null) {
            HeaderAdapter adapter = (HeaderAdapter) _warlordList.getAdapter();
            adapter.overwriteParents(headers);
            adapter.expandAll();
        } else {
            HeaderAdapter adapter = new HeaderAdapter(headers, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = ((TextView) v.findViewById(R.id.item_basic_text)).getText().toString();
                    Intent i = new Intent(getActivity(), DraftActivity.class);
                    i.putExtra(DraftPage.SELECTED_WARLORD, name);
                    startActivity(i);
                }
            });
            _warlordList.setAdapter(adapter);
            _warlordList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.expandAll();
        }
    }

    private class FactionSelectorListener implements View.OnClickListener {
        private Card.Factions _faction;
        public FactionSelectorListener(Card.Factions faction) {
            _faction = faction;
        }

        @Override
        public void onClick(View v) {
            ImageButton iv = (ImageButton) v;

            if (_factionFilter.contains(_faction)) {
                iv.setImageResource(_faction.getDarkIcon());
                _factionFilter.remove(_faction);
                iv.setBackgroundColor(Color.TRANSPARENT);
            } else {
                iv.setImageResource(_faction.getIconId());
                _factionFilter.add(_faction);
                iv.setBackgroundColor(_faction.getColor());
            }

            _cm.setFactionFilter(_factionFilter);
            buildList();
        }
    }
}