package com.androidteam.jobnow.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.MyApplication;
import com.androidteam.jobnow.common.APICommon;
import com.androidteam.jobnow.models.JobListReponse;
import com.androidteam.jobnow.models.JobListRequest;
import com.androidteam.jobnow.models.JobObject;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment implements OnMapReadyCallback {


    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final String TAG = MapListFragment.class.getSimpleName();
    private GoogleMap mMap;

    public MapListFragment() {
        // Required empty public constructor
    }

    private void initUI() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        if (ft != null) {
            ft.add(R.id.container, mMapFragment);
            ft.commit();
        }
        mMapFragment.getMapAsync(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_list, container, false);
        initUI();
        return rootView;
    }

    private void initData() {
        final JobListRequest jobListRequest = new JobListRequest(1, "ASC", null, null, null, null,
                null, null, null);

        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<JobListReponse> getJobList = service.getJobListByParam(
                APICommon.getSign(APICommon.getApiKey(), JobListRequest.PATH_URL),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                jobListRequest.page,
                jobListRequest.Order,
                jobListRequest.Title,
                jobListRequest.Location,
                jobListRequest.Skill,
                jobListRequest.MinSalary,
                jobListRequest.FromSalary,
                jobListRequest.ToSalary,
                jobListRequest.industryID);
        getJobList.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                try {
                    if (response.body() != null && response.body().code == 200) {
                        if (response.body().result != null && response.body().result.data != null && response.body().result.data.size() > 0) {
                            List<JobObject> lstJobs = response.body().result.data;
                            JobObject firstJob = lstJobs.get(0);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(
                                    new LatLng(firstJob.latitude, firstJob.longtitude));
                            mMap.moveCamera(cameraUpdate);


                            for (JobObject jobObject : lstJobs) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(jobObject.latitude, jobObject.longtitude))
                                        .title(jobObject.Title)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                                        .snippet(jobObject.FromSalary + "-" + jobObject.ToSalary + " USD");
                                mMap.addMarker(markerOptions);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "(on failed): " + t.toString());
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            initData();
        }

    }
}
