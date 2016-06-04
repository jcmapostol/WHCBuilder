package com.tests.apostol.testapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar bar = (Toolbar) findViewById(R.id.bar_main);
        bar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(bar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawer, bar, R.string.main_drawer_open, R.string.main_drawer_close);
        drawer.addDrawerListener(abdt);

        final Context c = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                CardManager.createAndGetInstance(c);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defineNavigationDrawer();
    }

    private void defineNavigationDrawer() {
        List<String> navItems = new ArrayList<>();
        List<Integer> resIds = new ArrayList<>();

        navItems.add(getString(R.string.str_lex));
        navItems.add(getString(R.string.str_draft));
        navItems.add(getString(R.string.str_reserve));
        navItems.add(getString(R.string.str_exterminatus));

        resIds.add(R.drawable.icon_eldar_dark);
        resIds.add(R.drawable.icon_tau_dark);
        resIds.add(R.drawable.icon_astra_militarum_dark);
        resIds.add(R.drawable.icon_space_marines_dark);

        MainDrawerAdapter da = new MainDrawerAdapter(this, navItems, resIds);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        final TabLayout tab = (TabLayout) findViewById(R.id.tab_main);
        ListView mainList = (ListView) findViewById(R.id.lv_drawer_main);

        mainList.setAdapter(da);
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2)
                    close();
                else {
                    drawer.closeDrawer(GravityCompat.START);

                    if (position < 2)
                        tab.getTabAt(position).select();
                }
            }
        });

        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DraftFragment());
        fragments.add(new LexFragment());

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.str_draft));
        titles.add(getString(R.string.str_lex));

        ViewPager vp = (ViewPager) findViewById(R.id.vp_main);
        vp.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), fragments, titles));
        tab.setupWithViewPager(vp);

        tab.getTabAt(0).setIcon(R.drawable.icon_astra_militarum);
        tab.getTabAt(1).setIcon(R.drawable.icon_eldar);
    }

    private void close() {
        this.finishAffinity();
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> _fragments;
        List<String> _titles;

        MainPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            _fragments = fragments;
            _titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return _fragments.get(position);
        }

        @Override
        public int getCount() {
            return _fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles.get(position);
        }
    }

}