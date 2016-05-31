package com.tests.apostol.testapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class LexicanumFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    int _squadIndex = 0;
    String _currentSet = "";
    List<Pair<Card, Integer>> _currentSquad;
    DrawerLayout _drawer;
    ImageView _squadPic;
    TextView _squadQty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lexicanum, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        _drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_lex);
        _squadPic = (ImageView)getActivity().findViewById(R.id.fragment_lex_squad_image);
        _squadQty = (TextView) getActivity().findViewById(R.id.fragment_lex_squad_quantity);

        ListView lexList = (ListView) getView().findViewById(R.id.lv_drawer_lex);
        ImageView back = (ImageView)getActivity().findViewById(R.id.fragment_lex_squad_back);
        ImageView forward = (ImageView)getActivity().findViewById(R.id.fragment_lex_squad_forward);

        String[] dataFrom = new String[] { CardDbContract.CardListEntry.COL_NAME, CardDbContract.CardListEntry.COL_FACTION };
        int[] bindTo = new int[] { R.id.item_lex_name, R.id.item_lex_faction };
        Cursor c = CardManager.getInstance().queryAllCards();

        _drawer.openDrawer(GravityCompat.END);
        LexicanumDrawerAdapter adapter = new LexicanumDrawerAdapter(getContext(), R.layout.li_lex, c, dataFrom, bindTo, 0);
        lexList.setAdapter(adapter);

        lexList.setOnItemClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        _drawer.closeDrawer(GravityCompat.END);

        CardManager cm = CardManager.getInstance();
        View squadView = getActivity().findViewById(R.id.fragment_lex_squad);
        ImageView img = (ImageView) getActivity().findViewById(R.id.fragment_lex_card);
        String packageName = getContext().getPackageName();
        String cardName = ((TextView) view.findViewById(R.id.item_lex_name)).getText().toString();

        Card card = cm.selectCardByName(cardName);
        _currentSet = card.getSet().getDatabaseName();
        String imgRes = _currentSet + card.getCardNumber();
        int actualId = getContext().getResources().getIdentifier(imgRes, "drawable", packageName);
        TextView alt = (TextView) getActivity().findViewById(R.id.fragment_lex_alttext);

        if (actualId == 0) {
            String cardText = "Name: " + cardName + "\n\n";
            cardText += "Faction: " + card.getFaction().getFullName() + "\n\n";
            cardText += "Loyalty: " + card.getLoyalty().getName() + "\n\n";
            cardText += "Type: " + card.getType().getFullName() + "\n\n";
            cardText += "Text: " + card.getText() + "\n\n";
            cardText += "Set: " + card.getSet().getName() + " " + card.getCardNumber();
            alt.setText(cardText);
        }
        else {
            img.setImageResource(actualId);
            alt.setText("");
        }

        if (card.getType() == Card.CardTypes.WR) {
            _currentSquad = cm.selectSquadByWarlordName(card.getName());
            _squadIndex = 0;
            squadView.setVisibility(View.VISIBLE);
            updateSquadDisplay();
        } else if (squadView.getVisibility() == View.VISIBLE) {
            squadView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSquadDisplay() {
        int resId = getContext().getResources().getIdentifier(_currentSquad.get(_squadIndex).first.getSet().getDatabaseName() + _currentSquad.get(_squadIndex).first.getCardNumber(), "drawable", getContext().getPackageName());
        _squadQty.setText(_currentSquad.get(_squadIndex).second + "x");
        _squadPic.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_lex_squad_back:
                if (_squadIndex == 0)
                    _squadIndex = _currentSquad.size() - 1;
                else
                    _squadIndex--;

                updateSquadDisplay();
                break;
            case R.id.fragment_lex_squad_forward:
                if (_squadIndex == _currentSquad.size() - 1)
                    _squadIndex = 0;
                else
                    _squadIndex++;

                updateSquadDisplay();
                break;
        }
    }
}