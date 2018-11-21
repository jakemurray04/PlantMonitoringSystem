package com.cvsu.pms.plantmonitoringsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

public class EnvironmentFragment extends Fragment {
    ProgressBar progressBarTemperature, progressBarHumidity, progressBarPlot1, progressBarPlot2, progressBarPlot3;
    TextView temp, humi, plot1, plot2, plot3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_environment, container, false);

        progressBarTemperature = view.findViewById(R.id.progressBarTemperature);
        progressBarTemperature.setProgress(69);
        temp = view.findViewById(R.id.textTemperature);
        temp.setText(Integer.toString(progressBarTemperature.getProgress()) + " °C");

        progressBarHumidity = view.findViewById(R.id.progressBarHumidity);
        progressBarHumidity.setProgress(69);
        humi = view.findViewById(R.id.textHumidity);
        humi.setText(Integer.toString(progressBarHumidity.getProgress()) + " %");

        progressBarPlot1 = view.findViewById(R.id.progressBarPlot3);
        progressBarPlot1.setProgress(61);
        plot1 = view.findViewById(R.id.text_plot1);
        plot1.setText(Integer.toString(progressBarPlot1.getProgress()) + " %");

        progressBarPlot2 = view.findViewById(R.id.progressBarPlot2);
        progressBarPlot2.setProgress(62);
        plot2 = view.findViewById(R.id.text_plot2);
        plot2.setText(Integer.toString(progressBarPlot2.getProgress()) + " %");

        progressBarPlot3 = view.findViewById(R.id.progressBarPlot1);
        progressBarPlot3.setProgress(63);
        plot3 = view.findViewById(R.id.text_plot3);
        plot3.setText(Integer.toString(progressBarPlot3.getProgress()) + " %");

        return view;
    }
}
