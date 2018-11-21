package com.cvsu.pms.plantmonitoringsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SchedulerFragment extends Fragment {
    Button button_on;
    Button button_off;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduler, container, false);

        button_on = rootView.findViewById(R.id.button_on);
        button_off = rootView.findViewById(R.id.button_off);



        button_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SettingFragment sf = new SettingFragment();
                sf.turnOnLed();
            }
        });

        button_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SettingFragment sf = new SettingFragment();
                sf.turnOffLed();
            }
        });

        return rootView;
    }
}
