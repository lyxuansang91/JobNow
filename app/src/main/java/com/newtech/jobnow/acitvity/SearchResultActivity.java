package com.newtech.jobnow.acitvity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.newtech.jobnow.R;
import com.newtech.jobnow.fragment.JobListFragment;
import com.newtech.jobnow.models.JobListRequest;

public class SearchResultActivity extends AppCompatActivity {

    public static final String TAG = SearchResultActivity.class.getSimpleName();
    public static final String KEY_JOB = "key_job";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initUI();
    }

    private void initUI() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.search_result);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(ft != null) {
            Bundle bundle = getIntent().getExtras();
            JobListRequest request = null;
            if(bundle != null) {
                request = (JobListRequest) bundle.getSerializable(KEY_JOB);
                Log.d(TAG, "job list request title: " + request.Title);
            }
            ft.replace(R.id.container, JobListFragment.getInstance(false, request));
            ft.commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
