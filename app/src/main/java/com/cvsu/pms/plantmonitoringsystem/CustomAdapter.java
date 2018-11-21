package com.cvsu.pms.plantmonitoringsystem;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomAdapter extends ArrayAdapter<String>  {
    private int layout;


    public CustomAdapter(Context context, List<String> name) {
        super(context, R.layout.custom_listview, name);

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.custom_listview, parent, false);
        String device = getItem(position);
        TextView devicetext = customView.findViewById(R.id.text_btdevice);
        devicetext.setText(device);
        devicetext.setHorizontallyScrolling(true);
        devicetext.setSelected(true);
        return customView;
    }
}



