package com.tests.apostol.testapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class LexFragment extends Fragment {
    private LinearLayout _altTextLayout;
    private TextView _altText;
    private Button _squadQty;
    private ImageView _cardImage;
    private ImageView _topBanner;
    private ImageView _botBanner;
    private ImageButton _forward;
    private ImageButton _back;

    private Card _warlordCard;
    private List<Pair<Card, Integer>> _squad;
    private int _index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_lex, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAdded()) {
            final DrawerLayout searchDrawer = (DrawerLayout) getView().findViewById(R.id.dl_lex);
            FloatingActionButton fb = (FloatingActionButton) getView().findViewById(R.id.button_lex_search);
            _altTextLayout = (LinearLayout) getView().findViewById(R.id.page_card_alttextlayout);
            _altText = (TextView) getView().findViewById(R.id.page_card_alttext);
            _squadQty = (Button) getView().findViewById(R.id.page_card_squadqty);
            _topBanner = (ImageView) getView().findViewById(R.id.page_card_botbanner);
            _botBanner = (ImageView) getView().findViewById(R.id.page_card_topbanner);
            _cardImage = (ImageView) getView().findViewById(R.id.page_card_image);
            _forward = (ImageButton) getView().findViewById(R.id.page_card_fwd);
            _back = (ImageButton) getView().findViewById(R.id.page_card_back);

            _forward.setVisibility(View.INVISIBLE);
            _back.setVisibility(View.INVISIBLE);
            _forward.setClickable(false);
            _back.setClickable(false);

            //<editor-fold desc="Squad Button Listener">
            _squadQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((TextView)v).getText().equals("Show Bloody")) {
                        _altTextLayout.setVisibility(View.VISIBLE);
                        _altText.setText(_warlordCard.getBreakdown());
                        ((TextView)v).setText("Show Hale");
                    }
                    else if (((TextView)v).getText().equals("Show Hale")) {
                        _altTextLayout.setVisibility(View.INVISIBLE);
                        ((TextView)v).setText("Show Bloody");
                    }
                }
            });
            //</editor-fold>
            //<editor-fold desc="Forward Image Button Listener">
            _forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_index == _squad.size())
                        _index = 0;
                    else
                        _index++;

                    updateSquadView();
                }
            });
            //</editor-fold>
            //<editor-fold desc="Back Image Button Listener">
            _back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_index == 0)
                        _index = _squad.size();
                    else
                        _index--;

                    updateSquadView();
                }
            });
            //</editor-fold>
            //<editor-fold desc="Drawer Listener">
            searchDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    if (CardManager.getInstance() != null) {
                        activateCardList();
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
            //</editor-fold>

            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchDrawer.openDrawer(GravityCompat.END);
                }
            });
        }
    }

    private void updateSquadView() {
        if (_index == 0) {
            String packageName = getContext().getPackageName();
            int actualId = getContext().getResources().getIdentifier(_warlordCard.getDrawableIdString(), "drawable", packageName);
            _cardImage.setImageResource(actualId);
            _squadQty.setText("Show Bloody");
        } else {
            _altTextLayout.setVisibility(View.INVISIBLE);
            Card c = _squad.get(_index - 1).first;
            int qty = _squad.get(_index - 1).second;
            String resId = c.getSet().getDatabaseName() + "" + c.getCardNumber();
            String packageName = getContext().getPackageName();
            int actualId = getContext().getResources().getIdentifier(resId, "drawable", packageName);
            _cardImage.setImageResource(actualId);

            if (qty == 1)
                _squadQty.setText(qty + " Copy in Squad");
            else
                _squadQty.setText(qty + " Copies in Squad");
        }
    }
    private void activateCardList() {
        ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progress_search);
        pb.setVisibility(View.INVISIBLE);

        LinearLayout hidden = (LinearLayout) getView().findViewById(R.id.ll_search);
        hidden.setVisibility(View.VISIBLE);

        ListView list = (ListView) getActivity().findViewById(R.id.lv_drawer_lex);

        if (list.getAdapter() == null) {
            String[] dataFrom = new String[]{CardDbContract.CardListEntry.COL_NAME, CardDbContract.CardListEntry.COL_FACTION};
            int[] bindTo = new int[]{R.id.item_lex_name, R.id.item_lex_faction};
            Cursor c = CardManager.getInstance().queryAllCards();
            LexListAdapter adapter = new LexListAdapter(getContext(), R.layout.li_lex, c, dataFrom, bindTo, 0);

            list.setAdapter(adapter);

            //<editor-fold desc="List Item Click">
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String cardName = ((TextView) view.findViewById(R.id.item_lex_name)).getText().toString();
                    CardManager cm = CardManager.getInstance();
                    Card c = cm.selectCardByName(cardName);
                    String resId = c.getSet().getDatabaseName() + "" + c.getCardNumber();
                    String packageName = getContext().getPackageName();
                    int actualId = getContext().getResources().getIdentifier(resId, "drawable", packageName);

                    _topBanner.setBackgroundColor(c.getFaction().getColor());
                    _botBanner.setBackgroundColor(c.getFaction().getColor());
                    _topBanner.setImageResource(c.getFaction().getIconId());
                    _botBanner.setImageResource(c.getFaction().getIconId());

                    if (actualId != 0) {
                        _cardImage.setImageResource(actualId);
                        _altTextLayout.setVisibility(View.INVISIBLE);
                    } else {
                        _cardImage.setImageResource(R.drawable.card_back);
                        _altTextLayout.setVisibility(View.VISIBLE);
                        _altText.setText(c.getBreakdown());
                    }

                    if (c.getType() == Card.CardTypes.WR) {
                        _index = 0;
                        _warlordCard = c;
                        _squadQty.setText("Show Bloody");
                        _squadQty.setBackgroundColor(c.getFaction().getColor());
                        _squadQty.setTextColor(c.getFaction().getTextColor());
                        _squad = cm.selectSquadByWarlordName(cardName);
                        _squadQty.setVisibility(View.VISIBLE);
                        _forward.setVisibility(View.VISIBLE);
                        _back.setVisibility(View.VISIBLE);
                        _squadQty.setClickable(true);
                        _forward.setClickable(true);
                        _back.setClickable(true);
                    } else {
                        _squadQty.setVisibility(View.INVISIBLE);
                        _forward.setVisibility(View.INVISIBLE);
                        _back.setVisibility(View.INVISIBLE);
                        _squadQty.setClickable(false);
                        _forward.setClickable(false);
                        _back.setClickable(false);
                    }
                }
            });
            //</editor-fold>
        }
    }
}