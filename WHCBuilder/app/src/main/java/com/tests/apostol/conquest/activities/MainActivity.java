package com.tests.apostol.conquest.activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tests.apostol.conquest.PageLifecycleInvokerListener;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.databases.DatabasePopulatorTask;
import com.tests.apostol.conquest.pages.armies.ArmiesPage;
import com.tests.apostol.conquest.pages.draft.DraftPage;
import com.tests.apostol.conquest.pages.lexicanum.LexPage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArmiesPage _arm;
    private DraftPage _draft;
    private LexPage _lex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar bar = (Toolbar) findViewById(R.id.main_bar);
        bar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(bar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_root);
        ActionBarDrawerToggle toggler = new ActionBarDrawerToggle(this, drawer, bar, R.string.main_drawer_open, R.string.main_drawer_close);
        drawer.addDrawerListener(toggler);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ConquestDbHelper _cdh = new ConquestDbHelper(this);
        DatabaseInterface tester = new DatabaseInterface(this, _cdh);

        if (!tester.tryQuery()) {
            DatabasePopulatorTask dt = new DatabasePopulatorTask(true, this, nm, _cdh);
            dt.execute();
        } else {
            DatabasePopulatorTask dt = new DatabasePopulatorTask(false, this, nm, _cdh);
            dt.execute();
        }

        TabLayout tab = (TabLayout) findViewById(R.id.main_tab);

        NavigationView nav = (NavigationView) findViewById(R.id.main_nav) ;
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                private String _exterm = getString(R.string.str_exterm);
                private String _config = getString(R.string.str_config);

                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    String title = item.getTitle().toString();

                    if (title == _exterm) {
                        close();
                        return true;
                    } else if (title == _config) {

                    }

                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });

        List<Fragment> fragments = new ArrayList<>();
        _arm = new ArmiesPage();
        _draft = new DraftPage();
        _lex = new LexPage();

        fragments.add(_lex);
        fragments.add(_arm);
        fragments.add(_draft);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.str_lex));
        titles.add(getString(R.string.str_armies));
        titles.add(getString(R.string.str_draft));

        final FragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        ViewPager vp = (ViewPager) findViewById(R.id.main_pager);
        vp.setAdapter(adapter);
        vp.setOffscreenPageLimit(3);
        vp.addOnPageChangeListener(new PageLifecycleInvokerListener(adapter));

        tab.setupWithViewPager(vp);
        tab.getTabAt(0).setIcon(R.drawable.icon_space_marines);
        tab.getTabAt(1).setIcon(R.drawable.icon_astra_militarum);
        tab.getTabAt(2).setIcon(R.drawable.icon_eldar);
    }

    private void close() {
        this.finishAffinity();
    }
}