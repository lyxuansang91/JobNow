package com.newtech.jobnow.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newtech.jobnow.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailJobsFragment extends Fragment {

    public DetailJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_jobs, container, false);
    }
}
