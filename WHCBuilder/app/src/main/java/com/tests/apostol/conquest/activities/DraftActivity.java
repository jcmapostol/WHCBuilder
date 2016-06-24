package com.tests.apostol.conquest.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.PageLifecycleInvokerListener;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.pages.armies.ArmiesPage;
import com.tests.apostol.conquest.pages.deckbuilder.DeckbuilderPage;
import com.tests.apostol.conquest.pages.deckbuilder.DetailsPage;
import com.tests.apostol.conquest.pages.draft.DraftPage;
import com.tests.apostol.conquest.pages.deckbuilder.SimulatorPage;

import java.util.ArrayList;
import java.util.List;

public class DraftActivity extends AppCompatActivity {
    private Card _warlord;
    private DatabaseInterface _cm;
    private DeckbuilderPage _dbPage;
    private SimulatorPage _simPage;
    private DetailsPage _detPage;

    private boolean _isEditing;
    private boolean _isSaving;
    private String _originalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);

        Intent intent = getIntent();
        String warlordName = intent.getStringExtra(DraftPage.SELECTED_WARLORD);
        _originalName = intent.getStringExtra(ArmiesPage.DECK_NAME);
        String deckDesc = intent.getStringExtra(ArmiesPage.DECK_DESC);
        _isEditing = intent.getBooleanExtra(ArmiesPage.IS_EDITING, false);

        _cm = new DatabaseInterface(this, new ConquestDbHelper(this));
        _warlord = _cm.getCardsByClause(warlordName).get(0);

        TextView tv = (TextView) findViewById(R.id.act_draft_warlord);
        tv.setText(_warlord.getName());
        tv.setTextColor(_warlord.getFaction().getTextColor());
        findViewById(R.id.act_draft_bannerroot).setBackgroundColor(_warlord.getFaction().getColor());
        ((ImageView) findViewById(R.id.act_draft_lbanner)).setImageResource(_warlord.getFaction().getIconId());
        ((ImageView) findViewById(R.id.act_draft_rbanner)).setImageResource(_warlord.getFaction().getIconId());

        _dbPage = new DeckbuilderPage();
        Bundle b = new Bundle();
        b.putString(DraftPage.SELECTED_WARLORD, _warlord.getName());
        if (_originalName != null) {
            b.putString(ArmiesPage.DECK_NAME, _originalName);
        }
        _dbPage.setArguments(b);

        _simPage = new SimulatorPage();
        _simPage.setDeckbuilderDependency(_dbPage);
        _simPage.setArguments(b);

        _detPage = new DetailsPage();
        Bundle b1 = new Bundle();
        b1.putString(ArmiesPage.DECK_NAME, _originalName);
        b1.putString(ArmiesPage.DECK_DESC, deckDesc);
        b1.putBoolean(ArmiesPage.IS_EDITING, _isEditing);
        b1.putString(DraftPage.SELECTED_WARLORD, _warlord.getName());
        _detPage.setArguments(b1);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(_dbPage);
        fragments.add(_simPage);
        fragments.add(_detPage);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.str_db));
        titles.add(getString(R.string.str_sim));
        titles.add(getString(R.string.str_details));

        final CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        ViewPager pager = (ViewPager) findViewById(R.id.act_draft_pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new PageLifecycleInvokerListener(adapter));

        TabLayout tab = (TabLayout) findViewById(R.id.act_draft_tab);
        tab.setupWithViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        if (_detPage.getName().length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unnamed deck will not be saved. Proceed?");
            builder.setPositiveButton(R.string.str_proceed, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    onActualBack();
                }
            });

            builder.setNeutralButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!_isEditing && (_originalName != null && !_originalName.equals(_detPage.getName())) && _cm.checkDeckExists(_detPage.getName())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Deck name conflicts with an existing saved deck. Deck will not be saved. Proceed?");

            builder.setPositiveButton(R.string.str_proceed, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    onActualBack();
                }
            });

            builder.setNeutralButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            _isSaving = true;
            super.onBackPressed();
        }
    }

    private void onActualBack() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String name = _detPage.getName();

        if (name.length() > 0 && _isSaving && !_cm.checkDeckExists(name) || ((_isEditing && _cm.checkDeckExists(name) && name.equals(_originalName)))) {
            if (_originalName != null && _cm.checkDeckExists(_originalName))
                _cm.deleteDeck(_originalName);

            _cm.saveDeck(name, _detPage.getDescription(), _warlord, _dbPage.getSavable().getCards());
            Toast.makeText(this, "Deck saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deck not saved.", Toast.LENGTH_SHORT).show();
        }
    }
}
