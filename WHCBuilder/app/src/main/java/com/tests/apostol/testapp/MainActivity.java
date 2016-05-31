package com.tests.apostol.testapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CardManager _executor;
    private Toolbar _bar;
    private int _currentFragment = 0;
    private boolean _canView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _bar = (Toolbar) findViewById(R.id.conquest_toolbar);
        setSupportActionBar(_bar);
        _bar.setNavigationIcon(R.drawable.icon_tau_dark);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawer, _bar, R.string.main_drawer_open, R.string.main_drawer_close);
        drawer.addDrawerListener(abdt);

        final Context c = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteDatabase(CardDatabaseHelper.DATABASE_NAME);
                _executor = CardManager.createAndGetInstance(c);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        List<String> navStrings = new ArrayList<>();
        navStrings.add("Deckbuild");
        navStrings.add("Lexicanum");
        navStrings.add("Exterminatus");

        List<Integer> resIds = new ArrayList<>();
        resIds.add(R.drawable.icon_tau_dark);
        resIds.add(R.drawable.icon_eldar_dark);
        resIds.add(R.drawable.icon_space_marines_dark);

        MainDrawerAdapter da = new MainDrawerAdapter(this, navStrings, resIds);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ListView mainList = (ListView) findViewById(R.id.lv_drawer_main);
        mainList.setAdapter(da);
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean didLoadFragment = false;

                if (_currentFragment != position) {
                    _currentFragment = position;

                    Fragment fragment = null;

                    if (position == 0) {
                        didLoadFragment = true;
                        fragment = new DeckbuilderFragment();
                    } else if (position == 1 && CardManager.getInstance().isReady) {
                        didLoadFragment = true;
                        fragment = new LexicanumFragment();
                    }

                    if (fragment != null)
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                }

                if (position == 2)
                    close();
                else if (didLoadFragment){
                    drawer.closeDrawer(GravityCompat.START);
                    //_bar.animate().translationY(-_bar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
                }
            }
        });

        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void close() {
        this.finishAffinity();
    }
}