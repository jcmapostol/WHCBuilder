package com.tests.apostol.conquest.pages.deckbuilder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tests.apostol.conquest.HeaderAdapterHelper;
import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.Deck;
import com.tests.apostol.conquest.HeaderAdapter;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.pages.PageLifecyclable;
import com.tests.apostol.conquest.pages.armies.ArmiesPage;
import com.tests.apostol.conquest.pages.draft.DraftPage;

import java.util.ArrayList;
import java.util.List;

public class DeckbuilderPage extends Fragment implements PageLifecyclable {
    private DatabaseInterface _cm;

    private int _currentTab;
    private TabLayout _tabs;
    private List<DeckbuilderAdapter> _adapters;
    private Card.Types _currentType;

    private RecyclerView _list;
    private TextView _listBlocker;
    private DrawerLayout _drawer;
    private RecyclerView _lex;
    private View.OnClickListener _lexListener;

    private Deck _simulatable;
    private Deck _savable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        Bundle b = getArguments();
        String warlordName = b.getString(DraftPage.SELECTED_WARLORD);
        _cm = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()));
        Card warlordCard = _cm.getCardsByClause(warlordName).get(0);
        _cm.setDeckbuildingRestrictions(warlordCard);

        View v = inflater.inflate(R.layout.draft_page_db, container, false);

        _lex = (RecyclerView) v.findViewById(R.id.drawer_lex_list);

        _listBlocker = (TextView) v.findViewById(R.id.draft_page_db_blocker);
        _listBlocker.setVisibility(View.INVISIBLE);

        _drawer = (DrawerLayout) v.findViewById(R.id.draft_page_db_root);
        _drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.draft_page_db_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_tabs.getSelectedTabPosition() != 0) {
                    _drawer.openDrawer(GravityCompat.END);
                    getView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            matchLexToSelectedTab();
                        }
                    }, 500);
                }
            }
        });

        _lexListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _drawer.closeDrawer(GravityCompat.END);

                String name = ((TextView) v.findViewById(R.id.item_basic_text)).getText().toString();
                Card card = _cm.getCardsByClause(name).get(0);
                CardVhData wrapper = new CardVhData(card);
                _adapters.get(_tabs.getSelectedTabPosition()).addParent(wrapper);
                _listBlocker.setVisibility(View.INVISIBLE);
                _list.setVisibility(View.VISIBLE);
                updateCurrentTab();
            }
        };

        //<editor-fold desc="Tabs">
        _tabs = (TabLayout) v.findViewById(R.id.draft_page_db_tab);
        _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_crown));
        _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_person).setText("0").setTag(Card.Types.AR));
        _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_bolt).setText("0").setTag(Card.Types.EV));
        _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_sword).setText("0").setTag(Card.Types.AT));
        _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_castle).setText("0").setTag(Card.Types.SU));

        _adapters = new ArrayList<>();
        List<Card> cards = _cm.getSquad(warlordCard.getDatabaseId());
        warlordCard.setQuantity(1);
        cards.add(0, warlordCard);
        List<ParentObject> squad = DeckbuilderAdapterHelper.makeCardWrappers(_cm.getSquad(warlordCard.getDatabaseId()));
        _adapters.add(new DeckbuilderAdapter(squad, false, _tabs.getTabAt(0)));

        String deckName = b.getString(ArmiesPage.DECK_NAME);

        if (deckName == null) {
            _adapters.add(new DeckbuilderAdapter(new ArrayList<ParentObject>(), true, _tabs.getTabAt(1)));
            _adapters.add(new DeckbuilderAdapter(new ArrayList<ParentObject>(), true, _tabs.getTabAt(2)));
            _adapters.add(new DeckbuilderAdapter(new ArrayList<ParentObject>(), true, _tabs.getTabAt(3)));
            _adapters.add(new DeckbuilderAdapter(new ArrayList<ParentObject>(), true, _tabs.getTabAt(4)));
        } else {
            _adapters.add(new DeckbuilderAdapter(DeckbuilderAdapterHelper.makeCardWrappers(_cm.loadDeckPortion(deckName, Card.Types.AR)), true, _tabs.getTabAt(1)));
            _adapters.add(new DeckbuilderAdapter(DeckbuilderAdapterHelper.makeCardWrappers(_cm.loadDeckPortion(deckName, Card.Types.EV)), true, _tabs.getTabAt(2)));
            _adapters.add(new DeckbuilderAdapter(DeckbuilderAdapterHelper.makeCardWrappers(_cm.loadDeckPortion(deckName, Card.Types.AT)), true, _tabs.getTabAt(3)));
            _adapters.add(new DeckbuilderAdapter(DeckbuilderAdapterHelper.makeCardWrappers(_cm.loadDeckPortion(deckName, Card.Types.SU)), true, _tabs.getTabAt(4)));
        }

        if (warlordCard.getFaction() == Card.Factions.TY) {
            _tabs.addTab(_tabs.newTab().setIcon(R.drawable.icon_nerve).setText("0"));

            if (deckName == null || deckName.equals("")) {
                _adapters.add(new DeckbuilderAdapter(new ArrayList<ParentObject>(), true, _tabs.getTabAt(5)));
            } else {
                _adapters.add(new DeckbuilderAdapter(DeckbuilderAdapterHelper.makeCardWrappers(_cm.loadDeckPortion(deckName, Card.Types.SY)), true, _tabs.getTabAt(5)));
            }
        }

        final List<String> types = new ArrayList<>();
        types.add("units");
        types.add("events");
        types.add("attachments");
        types.add("supports");
        types.add("synapse");

        _tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ProgressBar pb = (ProgressBar) _drawer.findViewById(R.id.drawer_lex_progress);
                pb.setVisibility(View.VISIBLE);

                LinearLayout hidden = (LinearLayout) _drawer.findViewById(R.id.drawer_lex_root);
                hidden.setVisibility(View.INVISIBLE);

                DeckbuilderAdapter adapter = _adapters.get(tab.getPosition());
                _list.setAdapter(adapter);

                if (adapter.getCardTotal() > 0) {
                    _list.setVisibility(View.VISIBLE);
                    _listBlocker.setVisibility(View.INVISIBLE);
                } else {
                    _listBlocker.setVisibility(View.VISIBLE);
                    _list.setVisibility(View.INVISIBLE);
                }

                if (tab.getPosition() == 0) {
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    _currentType = (Card.Types)tab.getTag();
                    fab.setVisibility(View.VISIBLE);
                    _listBlocker.setText("Add " + types.get(_tabs.getSelectedTabPosition() - 1) + " using the button");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //</editor-fold>

        _list = (RecyclerView) v.findViewById(R.id.draft_page_db_list);
        _list.setAdapter(_adapters.get(0));
        _list.setVisibility(View.VISIBLE);
        _list.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onShowPage() {
    }

    @Override
    public void onHidePage() {
        saveDeck();
    }

    @Override
    public void onPause() {
        saveDeck();
        super.onDestroy();
    }

    public Deck getSimulatable() {
        return _simulatable;
    }
    public Deck getSavable() {
        return _savable;
    }
    private void updateCurrentTab() {
        TabLayout.Tab tab = _tabs.getTabAt(_tabs.getSelectedTabPosition());
        int count = _adapters.get(tab.getPosition()).getCardTotal();
        tab.setText(String.valueOf(count));
    }

    private void matchLexToSelectedTab() {
        if (_cm.tryQuery()) {
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.drawer_lex_progress);
            pb.setVisibility(View.INVISIBLE);

            LinearLayout hidden = (LinearLayout) getView().findViewById(R.id.drawer_lex_root);
            hidden.setVisibility(View.VISIBLE);

            if (_tabs.getSelectedTabPosition() != _currentTab) {
                _currentTab = _tabs.getSelectedTabPosition();
                List<ParentObject> headers = HeaderAdapterHelper.makeHeadersByFaction(_cm.getCardsByType(_currentType));
                HeaderAdapter adapter = (HeaderAdapter) _lex.getAdapter();

                if (adapter != null) {
                    adapter.overwriteParents(headers);
                } else {
                    _lex.setAdapter(new HeaderAdapter(headers, _lexListener));
                    _lex.setHasFixedSize(true);
                    _lex.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        }
    }

    private void saveDeck() {
        List<Card> cards = new ArrayList<>();
        List<Card> savables = new ArrayList<>();

        for (int i = 0; i < _adapters.size(); i++) {
            if (i != 0) {
                cards.addAll(_adapters.get(i).getCards());
                savables.addAll(_adapters.get(i).getCards());
            }
            else {
                List<Card> squadCards = _adapters.get(i).getCards();
                cards.addAll(squadCards.subList(1, squadCards.size()));
            }
        }

        _simulatable = new Deck(cards);
        _savable = new Deck(savables);
    }
}