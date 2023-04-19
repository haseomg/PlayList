package com.example.playlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View converView, ViewGroup parent) {
        View v = super.getView(position, converView, parent);
        if (position == getCount()) {
            ((TextView) v.findViewById(android.R.id.text1)).setText("");
            ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
        }
        return v;
    }

    public int getCount() {
        return super.getCount() - 1;
    }
}
