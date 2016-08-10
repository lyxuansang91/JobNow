package com.androidteam.jobnow.acitvity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.fragment.ProfileFragment;
import com.androidteam.jobnow.widget.CenteredToolbar;
import com.androidteam.jobnow.widget.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private CommonTabLayout tabbottom;
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_bottom, R.mipmap.ic_saved_bottom,
            R.mipmap.ic_applied_bottom, R.mipmap.ic_profile_bottom};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_bottom_selected, R.mipmap.ic_saved_bottom_selected,
            R.mipmap.ic_applied_bottom_selected, R.mipmap.ic_profile_bottom_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        CenteredToolbar toolbar = (CenteredToolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");
        InitUI();
        bindData();
        InitEvent();

    }

    private void bindData() {
        String[] mTitles = {getString(R.string.home), getString(R.string.saved), getString(R.string.applied), getString(R.string.profile)};

        for (String title : mTitles) {
            mFragments2.add(new ProfileFragment());
        }
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        tabbottom.setTabData(mTabEntities, this, R.id.fl_change, mFragments2);
        tabbottom.showMsg(1, 10);
        tabbottom.setCurrentTab(3);
    }

    private void InitEvent() {
        tabbottom.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
//                mTabLayout_1.setCurrentTab(position);
//                mTabLayout_2.setCurrentTab(position);
//                mTabLayout_4.setCurrentTab(position);
//                mTabLayout_5.setCurrentTab(position);
//                mTabLayout_6.setCurrentTab(position);
//                mTabLayout_7.setCurrentTab(position);
//                mTabLayout_8.setCurrentTab(position);
                Toast.makeText(ProfileActivity.this, "Selected " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void InitUI() {
        tabbottom = (CommonTabLayout) findViewById(R.id.tabbottom);

    }

}
