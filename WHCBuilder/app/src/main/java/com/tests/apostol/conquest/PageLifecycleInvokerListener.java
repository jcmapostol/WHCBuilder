package com.tests.apostol.conquest;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.tests.apostol.conquest.pages.PageLifecyclable;

public class PageLifecycleInvokerListener implements ViewPager.OnPageChangeListener {
    private int _currentPage;
    private FragmentPagerAdapter _adapter;

    public PageLifecycleInvokerListener(FragmentPagerAdapter adapter) {
        _adapter = adapter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (_adapter.getItem(_currentPage) instanceof PageLifecyclable) {
            PageLifecyclable hide = (PageLifecyclable) _adapter.getItem(_currentPage);
            hide.onHidePage();
        }

        if (_adapter.getItem(position) instanceof PageLifecyclable) {
            PageLifecyclable l = (PageLifecyclable) _adapter.getItem(position);
            l.onShowPage();
        }

        _currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
