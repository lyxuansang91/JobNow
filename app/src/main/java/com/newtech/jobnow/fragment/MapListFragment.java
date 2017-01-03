package com.newtech.jobnow.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.acitvity.DetailJobsActivity;
import com.newtech.jobnow.acitvity.MyApplication;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.models.JobListReponse;
import com.newtech.jobnow.models.JobListRequest;
import com.newtech.jobnow.models.JobObject;
import com.newtech.jobnow.models.MapJobListReponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = MapListFragment.class.getSimpleName();
    private GoogleMap mMap;
    private HashMap<Marker, Integer> lstMarker = new HashMap<>();
    private int PERMISSION_REQUEST_MAP = 111;
    private LatLng VIETNAM = new LatLng(20.993462, 105.846858);
    private Location mLastLocation = null;
    private GoogleApiClient mGoogleClient = null;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleClient == null) {
            mGoogleClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_MAP);
            } else {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                //initData();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                        Log.d(TAG, "job id: " + lstMarker.get(marker));
                        intent.putExtra("jobId", lstMarker.get(marker));
                        startActivity(intent);
                    }
                });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        JobObject jobObject = (JobObject) marker.getTag();
                        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                        if (view != null) {
                            TextView tvInfowWindowTitle = (TextView) view.findViewById(R.id.tvInfoWindowTitle);
                            String title = jobObject.Title;
                            tvInfowWindowTitle.setText(title);

                            TextView tvInfoWindowLocation = (TextView) view.findViewById(R.id.tvInfoWindowLocation);
                            String location = jobObject.LocationName;
                            tvInfoWindowLocation.setText(location);

                            TextView tvInfoWindowMoney = (TextView) view.findViewById(R.id.tvInfoWindowSalary);
                            String money = jobObject.FromSalary + "-" + jobObject.ToSalary + " USD";
                            tvInfoWindowMoney.setText(money);
                        }
                        return view;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
            }

        }
    }


    private void getListJobInLocation(double lat, double lng) {
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<MapJobListReponse> call = service.getListJobInLocation(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobs/getListJobInLocation"),
                APICommon.getAppId(),
                APICommon.getDeviceType(),
                lat,
                lng);
        call.enqueue(new Callback<MapJobListReponse>() {
            @Override
            public void onResponse(Response<MapJobListReponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().code == 200) {
                    //success
                    if (response.body().result != null && response.body().result != null && response.body().result.size() > 0) {
                        List<JobObject> lstJobs = response.body().result;

                        for (JobObject jobObject : lstJobs) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(jobObject.latitude, jobObject.longtitude))
                                    .title(jobObject.Title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                                    .snippet(jobObject.FromSalary + "-" + jobObject.ToSalary + " USD");

                            Marker marker = mMap.addMarker(markerOptions);
                            marker.setTag(jobObject);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_MAP) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                //initData();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getActivity(), DetailJobsActivity.class);
                        Log.d(TAG, "job id: " + lstMarker.get(marker));
                        intent.putExtra("jobId", lstMarker.get(marker));
                        startActivity(intent);
                    }
                });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        JobObject jobObject = (JobObject) marker.getTag();
                        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                        if (view != null) {
                            TextView tvInfowWindowTitle = (TextView) view.findViewById(R.id.tvInfoWindowTitle);
                            String title = jobObject.Title;
                            tvInfowWindowTitle.setText(title);

                            TextView tvInfoWindowLocation = (TextView) view.findViewById(R.id.tvInfoWindowLocation);
                            String location = jobObject.LocationName;
                            tvInfoWindowLocation.setText(location);

                            TextView tvInfoWindowMoney = (TextView) view.findViewById(R.id.tvInfoWindowSalary);
                            String money = jobObject.FromSalary + "-" + jobObject.ToSalary + " USD";
                            tvInfoWindowMoney.setText(money);
                        }
                        return view;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
                if (mLastLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    getListJobInLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(VIETNAM));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    getListJobInLocation(VIETNAM.latitude, VIETNAM.longitude);
                }


            } else {
                // Permission was denied or request was cancelled
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_MAP);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            getListJobInLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(VIETNAM));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            getListJobInLocation(VIETNAM.latitude, VIETNAM.longitude);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleClient != null) {
            mGoogleClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleClient != null) {
            mGoogleClient.disconnect();
        }
    }
}
