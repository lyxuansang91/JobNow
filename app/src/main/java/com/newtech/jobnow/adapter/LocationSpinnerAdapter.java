package com.newtech.jobnow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.newtech.jobnow.models.JobLocationResponse;

import java.util.List;

/**
 * Created by manhi on 21/1/2016.
 */
public class LocationSpinnerAdapter extends ArrayAdapter<JobLocationResponse.JobLocation> {
    private Context context;
    private List<JobLocationResponse.JobLocation> jobLocations;

    public LocationSpinnerAdapter(Context context, List<JobLocationResponse.JobLocation> jobLocations) {
        super(context, 0, jobLocations);
        this.context = context;
        this.jobLocations = jobLocations;
    }

    public JobLocationResponse.JobLocation getWordbyPosition(int position) {
        return getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JobLocationResponse.JobLocation item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        // Lookup view for data population
        TextView tvWord = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        tvWord.setText(item.Name);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        JobLocationResponse.JobLocation item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        // Lookup view for data population
        TextView tvWord = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        tvWord.setText(item.Name);

        return convertView;
    }
}