package com.androidteam.jobnow.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.widget.CenteredToolbar;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private ViewPager viewPager;

    public int[] tabs() {
        return new int[]{
                R.string.myProfile,
                R.string.exprience,
                R.string.skills
        };
    }

    private LinearLayout tab1, tab2, tab3;
    private ImageView ic_tab1, ic_tab2, ic_tab3;
    private TextView custom_text1, custom_text2, custom_text3;
    private int tabSelected = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, null);
        initUI(v);
        bindData();
        initEvent();
        return v;
    }

    private void initEvent() {
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 0;
                setPager();
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 1;
                setPager();
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected = 2;
                setPager();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabSelected = position;
                setTabSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPager() {
        viewPager.setCurrentItem(tabSelected);
        setTabSelected();
    }

    private void setTabSelected() {
        switch (tabSelected) {
            case 0:
                tab1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab_selected);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
            case 1:
                tab2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab1.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab2.setImageResource(R.mipmap.ic_exprience_selected);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
            case 2:
                tab3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab1.setBackgroundResource(R.drawable.bg_tab_profile);
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill_selected);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            default:
                tab1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                tab2.setBackgroundResource(R.drawable.bg_tab_profile);
                tab3.setBackgroundResource(R.drawable.bg_tab_profile);

                ic_tab1.setImageResource(R.mipmap.ic_profile_tab_selected);
                custom_text1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                ic_tab2.setImageResource(R.mipmap.ic_exprience);
                custom_text2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                ic_tab3.setImageResource(R.mipmap.ic_skill);
                custom_text3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                break;
        }
    }

    private void bindData() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        pages.add(FragmentPagerItem.of(getString(tabs()[0]), MyProfileFragment.class));
        pages.add(FragmentPagerItem.of(getString(tabs()[1]), ExprienceFragment.class));
        pages.add(FragmentPagerItem.of(getString(tabs()[2]), SkillsFragment.class));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);

        viewPager.setAdapter(adapter);
    }

    private void initUI(View v) {
        CenteredToolbar toolbar = (CenteredToolbar) v.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

//        viewPagerTab.setViewPager(viewPager);
        tab1 = (LinearLayout) v.findViewById(R.id.tab1);
        tab2 = (LinearLayout) v.findViewById(R.id.tab2);
        tab3 = (LinearLayout) v.findViewById(R.id.tab3);

        ic_tab1 = (ImageView) v.findViewById(R.id.ic_tab1);
        ic_tab2 = (ImageView) v.findViewById(R.id.ic_tab2);
        ic_tab3 = (ImageView) v.findViewById(R.id.ic_tab3);

        custom_text1 = (TextView) v.findViewById(R.id.custom_text1);
        custom_text2 = (TextView) v.findViewById(R.id.custom_text2);
        custom_text3 = (TextView) v.findViewById(R.id.custom_text3);
    }

}
