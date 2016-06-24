package com.tests.apostol.conquest.pages.lexicanum;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.HeaderAdapter;
import com.tests.apostol.conquest.HeaderAdapterHelper;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;

import java.util.List;

public class LexPage extends Fragment {
    private DatabaseInterface _cm;

    private DrawerLayout _drawer;

    private ImageView _cardImage;
    private Button _cardButton;
    private ImageButton _forward;
    private ImageButton _back;

    private LinearLayout _altRoot;
    private TextView _altText;
    private ImageView _bIcon;
    private ImageView _tIcon;

    private Card _warlordCard;
    private List<Card> _squad;
    private int _index;
    private String _hale;
    private String _bloody;

    private View _queryRoot;
    private EditText _query;
    private ImageButton _queryFake;

    private RecyclerView _drawerList;
    private List<ParentObject> _defaultData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_lex, container, false);

        _cm = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()));
        _bloody = getString(R.string.str_lex_bloody);
        _hale = getString(R.string.str_lex_hale);

        //<editor-fold desc="Set up Alternate Text">
        _cardImage = (ImageView) v.findViewById(R.id.page_lex_image);
        _altRoot = (LinearLayout) v.findViewById(R.id.page_lex_alt_root);
        _altText = (TextView) v.findViewById(R.id.page_lex_alt_text);
        _tIcon = (ImageView) v.findViewById(R.id.page_lex_alt_ticon);
        _bIcon = (ImageView) v.findViewById(R.id.page_lex_alt_bicon);
        //</editor-fold>

        //<editor-fold desc="Set up drawer">
        _drawer = (DrawerLayout) v.findViewById(R.id.page_lex_root);
        _drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        _drawerList = (RecyclerView) v.findViewById(R.id.drawer_lex_list);
        //</editor-fold>

        //<editor-fold desc="Set up squad view">
        _cardButton = (Button) v.findViewById(R.id.page_lex_btn_qty);
        _cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String cur = b.getText().toString();

                if (cur.equals(_bloody)) {
                    showBloody();
                } else if (cur.equals(_hale)) {
                    showHale();
                }
            }
        });

        _forward = (ImageButton) v.findViewById(R.id.page_lex_btn_fwd);
        _forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewNextInSquad();
            }
        });

        _back = (ImageButton) v.findViewById(R.id.page_lex_btn_back);
        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPreviousInSquad();
            }
        });
        //</editor-fold>

        //<editor-fold desc="Set up drawer query view">
        _queryRoot = v.findViewById(R.id.drawer_lex_query_root);

        _queryFake = (ImageButton) v.findViewById(R.id.drawer_lex_btn_query);
        _queryFake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQueryView();
            }
        });

        ImageButton queryActual = (ImageButton) v.findViewById(R.id.drawer_lex_btn_actualquery);
        queryActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performQuery(_query.getText().toString());
            }
        });

        _query = (EditText) v.findViewById(R.id.drawer_lex_query_edit);
        ImageButton queryCancel = (ImageButton) v.findViewById(R.id.drawer_lex_query_btn_cancel);
        queryCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelQuery();
            }
        });
        //</editor-fold>

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.page_lex_btn_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _drawer.openDrawer(GravityCompat.END);
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeLexProgressSpinner();
                    }
                }, 500);
            }
        });

        hideSquadView();

        return v;
    }

    private void removeLexProgressSpinner() {
        if (_cm.tryQuery()) {
            ProgressBar pb = (ProgressBar) _drawer.findViewById(R.id.drawer_lex_progress);
            pb.setVisibility(View.INVISIBLE);

            LinearLayout hidden = (LinearLayout) _drawer.findViewById(R.id.drawer_lex_root);
            hidden.setVisibility(View.VISIBLE);

            if (_drawerList.getAdapter() == null) {
                List<ParentObject> data = HeaderAdapterHelper.makeHeadersBySet(_cm.getCards());
                _defaultData = data;
                _drawerList.setLayoutManager(new LinearLayoutManager(getContext()));
                _drawerList.setAdapter(new HeaderAdapter(data, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(v);
                    }
                }));
                _drawerList.setHasFixedSize(true);
            }
        } else {
            Toast.makeText(getContext(), "Wait for card processing to finish.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openQueryView() {
        _queryRoot.setVisibility(View.VISIBLE);
        _queryFake.setVisibility(View.INVISIBLE);
    }
    private void cancelQuery() {
        _queryRoot.setVisibility(View.INVISIBLE);
        _queryFake.setVisibility(View.VISIBLE);
    }
    private void performQuery(String query) {
        if (query.length() > 0) {
            List<ParentObject> headers = HeaderAdapterHelper.makeHeadersByFaction(_cm.getCardsByClause(query));
            HeaderAdapter adapter = (HeaderAdapter) _drawerList.getAdapter();
            adapter.overwriteParents(headers);
        } else {
            HeaderAdapter adapter = (HeaderAdapter) _drawerList.getAdapter();
            adapter.overwriteParents(_defaultData);
        }
    }

    private void viewNextInSquad() {
        if (_index == _squad.size())
            _index = 0;
        else
            _index++;

        updateSquadView();
    }
    private void viewPreviousInSquad() {
        if (_index == 0)
            _index = _squad.size();
        else
            _index--;

        updateSquadView();
    }
    private void updateSquadView() {
        if (_index == 0) {
            _cardImage.setImageResource(_warlordCard.getImageId());
            _cardButton.setText(R.string.str_lex_bloody);
        } else {
            _altRoot.setVisibility(View.INVISIBLE);
            Card c = _squad.get(_index - 1);
            int qty = _squad.get(_index - 1).getQuantity();
            _cardImage.setImageResource(c.getImageId());

            if (qty == 1)
                _cardButton.setText(qty + " Copy in Squad");
            else
                _cardButton.setText(qty + " Copies in Squad");
        }
    }
    private void hideSquadView() {
        _cardButton.setVisibility(View.INVISIBLE);
        _forward.setVisibility(View.INVISIBLE);
        _back.setVisibility(View.INVISIBLE);
    }
    private void showSquadView() {
        _cardButton.setVisibility(View.VISIBLE);
        _forward.setVisibility(View.VISIBLE);
        _back.setVisibility(View.VISIBLE);

    }

    private void showBloody() {
        if (_warlordCard != null) {
            _altText.setText(_warlordCard.getBreakdown());
            _altRoot.setVisibility(View.VISIBLE);
            _cardButton.setText(_hale);
        }
    }
    private void showHale() {
        if (_warlordCard != null) {
            _altRoot.setVisibility(View.INVISIBLE);
            _cardButton.setText(_bloody);
        }
    }

    private void onItemClicked(View v) {
        String name = ((TextView) v.findViewById(R.id.item_basic_text)).getText().toString();
        Card card = _cm.getCardsByClause(name).get(0);
        _cardImage.setImageResource(card.getImageId());

        if (card.getImageId() != R.drawable.card_back) {
            _altRoot.setVisibility(View.INVISIBLE);
        } else {
            _altRoot.setVisibility(View.VISIBLE);
            _altText.setText(card.getBreakdown());
        }

        _drawer.closeDrawer(GravityCompat.END);
        _tIcon.setBackgroundColor(card.getFaction().getColor());
        _tIcon.setImageResource(card.getFaction().getIconId());
        _bIcon.setBackgroundColor(card.getFaction().getColor());
        _bIcon.setImageResource(card.getFaction().getIconId());

        if (card.getType() == Card.Types.WR) {
            _index = 0;
            _warlordCard = card;
            _cardButton.setText(R.string.str_lex_bloody);
            _cardButton.setBackgroundColor(card.getFaction().getColor());
            _cardButton.setTextColor(card.getFaction().getTextColor());
            _squad = _cm.getSquad(card.getDatabaseId());

            showSquadView();
        } else {
            hideSquadView();
        }
    }
}