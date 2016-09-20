package com.androidteam.jobnow.acitvity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.fragment.AppliedJobListFragment;
import com.androidteam.jobnow.fragment.MainFragment;
import com.androidteam.jobnow.fragment.ProfileFragment;
import com.androidteam.jobnow.fragment.SaveJobListFragment;
import com.androidteam.jobnow.widget.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
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
        InitUI();
        bindData();
        InitEvent();
    }

    private void bindData() {
        String[] mTitles = {getString(R.string.home), getString(R.string.saved), getString(R.string.applied), getString(R.string.profile)};
        for (int i = 0; i < mTitles.length; i++) {
            switch (i) {
                case 0:
                    mFragments2.add(new MainFragment());
                    break;
                case 1:
                    mFragments2.add(SaveJobListFragment.getInstance());
                    break;
                case 2:
                    mFragments2.add(AppliedJobListFragment.getInstance());
                    break;
                case 3:
                    mFragments2.add(new ProfileFragment());
                    break;
                default:
                    break;

            }
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        tabbottom.setTabData(mTabEntities, this, R.id.fl_change, mFragments2);
        //tabbottom.showMsg(1, 10);
        tabbottom.setCurrentTab(0);
    }

    private void InitEvent() {
    }

    private void InitUI() {
        tabbottom = (CommonTabLayout) findViewById(R.id.tabbottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
