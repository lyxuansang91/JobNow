package com.androidteam.jobnow.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidteam.jobnow.R;
import com.androidteam.jobnow.acitvity.DetailJobsActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = MapListFragment.class.getSimpleName();
    private GoogleMap mMap;
    private HashMap<Marker, Integer> lstMarker = new HashMap<>();

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
            LocationManager locationManager = (LocationManager) getContext().getSystemService(
                    Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);


            if (ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Log.d(TAG, "location: " + location);
            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //initData();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                    Log.d(TAG, "job id: " + lstMarker.get(marker));
                    intent.putExtra("jobId", lstMarker.get(marker));
//                    startActivity(intent);
//                    return true;
                    return false;
                }
            });
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        getListJobInLocation(latitude, longitude);
    }

    private void getListJobInLocation(double lat, double lng) {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<JobListReponse> call = service.getListJobInLocation(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobs/getListJobInLocation"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                lat,
                lng);
        call.enqueue(new Callback<JobListReponse>() {
            @Override
            public void onResponse(Response<JobListReponse> response, Retrofit retrofit) {
                if(response.body() != null && response.body().code == 200) {
                    //success
                    if (response.body().result != null && response.body().result.data != null && response.body().result.data.size() > 0) {
                        List<JobObject> lstJobs = response.body().result.data;

                        for (JobObject jobObject : lstJobs) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(jobObject.latitude, jobObject.longtitude))
                                    .title(jobObject.Title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                                    .snippet(jobObject.FromSalary + "-" + jobObject.ToSalary + " USD");

                            Marker marker = mMap.addMarker(markerOptions);
                            lstMarker.put(marker, jobObject.id);
                        }
                    }
                } else
                    Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
