package com.newtech.jobnow.acitvity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newtech.jobnow.R;
import com.newtech.jobnow.common.APICommon;
import com.newtech.jobnow.config.Config;
import com.newtech.jobnow.models.DetailJobResponse;
import com.newtech.jobnow.models.JobObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    public static final String KEY_JOB_ID = "jobId";
    private int jobId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getDetailJob() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true, false);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.Pref, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(Config.KEY_ID, 0);
        APICommon.JobNowService service = MyApplication.getInstance().getJobNowService();
        Call<DetailJobResponse> call = service.getDetailJob(
                APICommon.getSign(APICommon.getApiKey(), "api/v1/jobs/getJobDetail"),
                APICommon.getAppId(), APICommon.getDeviceType(), userId, jobId);
        call.enqueue(new Callback<DetailJobResponse>() {
            @Override
            public void onResponse(Response<DetailJobResponse> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().code == 200) {
                    Double lattitude = response.body().result.latitude;
                    Double longtitude = response.body().result.longtitude;
                    String title = response.body().result.Title;

                    if (lattitude != null && longtitude != null) {
                        LatLng currentLatLng = new LatLng(lattitude, longtitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(currentLatLng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                                .snippet(response.body().result.FromSalary + "-" +
                                        response.body().result.ToSalary + " USD");
                        mMap.addMarker(markerOptions).setTag(response.body().result);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    JobObject jobObject = (JobObject) marker.getTag();
                    View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    if(view != null) {
                        TextView tvInfowWindowTitle = (TextView)view.findViewById(R.id.tvInfoWindowTitle);
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
            Bundle bundle = getIntent().getExtras();
            jobId = bundle.getInt(KEY_JOB_ID, 0);
            if (jobId != 0) {
                getDetailJob();
            }
        }
    }
}
