package com.cvsu.pms.plantmonitoringsystem;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.TreeMap;

import static com.cvsu.pms.plantmonitoringsystem.MainActivity.spinnerRes;

public class SchedulerFragment extends Fragment {

    private RecyclerView recyclerView;
    public static TreeMap<String, ArrayList<PlantSched>> schedules;
    public static ArrayList<PlantSched> tmpSched;
    public SchedulerAdapter schedulerAdapter;
    private TimePicker timePicker;
    private Spinner spinner;
    private String spinnerHead, timePicked;
    private String toRemove;
    FloatingActionButton fab;
    String TAG = "MyLog";

    SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");

    private Date parseTime(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduler, container, false);
        spinner = rootView.findViewById(R.id.spinner);

        spinnerHead = "";
        timePicked = "";
        toRemove = "";

        schedules = new TreeMap<>();
        schedules.putAll(PlantSched.updateLocalSchedules(getContext()));
        ArrayList<PlantSched> plantScheds = new ArrayList<>();

        tmpSched = new ArrayList<>(plantScheds);

        updateSpinner();

        recyclerView = rootView.findViewById(R.id.recyclerView);

        schedulerAdapter = new SchedulerAdapter(rootView.getContext());
        recyclerView.setAdapter(schedulerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                            @Override
                            public boolean canSwipeLeft(int position) {
                                return false;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    schedules.get(spinnerHead).remove(position);
                                    tmpSched.remove(position);
                                    schedules.putAll(PlantSched.updateLocalSchedules(getContext()));
                                    schedulerAdapter.notifyItemRemoved(position);
                                }
                                schedulerAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);

        rootView.findViewById(R.id.button_Add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlant();
            }
        });
        rootView.findViewById(R.id.button_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRemove = spinnerHead;
                schedules.remove(spinnerHead);
                tmpSched.clear();
                updateSpinner();
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addScheduleTime();
            }
        });
        //updateDurations();
        if(spinnerHead.isEmpty()){
            Log.d("durations","spinnerHead.empty");
            try{
                spinnerHead = schedules.firstKey();
                updateRecyclerView(spinnerHead);
            }catch (Exception e){
                Log.d("durations","spinner.e");
            }
            Log.d("durations",spinnerHead);
        }
        return rootView;
    }

    private void timePicked(String t){
        timePicked = t;
    }
    private void addScheduleTime(){
        if(spinnerHeadPos(spinnerHead)>-1){
            Log.d(TAG,"occupiedSpinner");
        }else{
            Log.d(TAG,"emptySpinner");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Add New Time");
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.add_time, null);
        builder.setView(dialogView);

        timePicker = dialogView.findViewById(R.id.time);
        timePicker.setIs24HourView(false);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                timePicked(i+":"+(i1<10?"0"+i1:i1));
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String str = timePicked.replaceAll("\\s+","");
                if(!str.isEmpty() && spinnerHeadPos(spinnerHead)>-1){
                    schedules.get(spinnerHead).add(new PlantSched(str));
                    updateRecyclerView(spinnerHead);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                timePicked("");
            }
        });
        builder.create().show();

    }
    private void updateDurations(){
        Log.d("durations","start");
        for(PlantSched tmp_sched:tmpSched){
            String timeStop = tmp_sched.getTime();
            Calendar now = Calendar.getInstance();
            long diffMinutes = 0;
            long diffHours = 0;
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = parseTime(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
                d2 = parseTime(timeStop);

                //in milliseconds4
                long diff = d2.getTime() - d1.getTime();

                diffMinutes = diff / (60 * 1000) % 60;
                diffHours = diff / (60 * 60 * 1000) % 24;


            } catch (Exception e) {
                //e.printStackTrace();
                Log.e("durations",e.getMessage());
            }
            Log.d("durations",diffHours+":"+diffMinutes);
            if(diffHours< 0L || diffMinutes< 0L){
                diffHours = 24L + diffHours;
                diffMinutes = 59L + diffMinutes;
            }
            String hr = Long.toString(diffHours)+"hr";
            String mins = Long.toString(diffMinutes)+"min";
            if (diffHours> 1L){
                hr+="s";
            }
            if (diffMinutes> 1L){
                mins+="s";
            }
            hr = diffHours== 0L ?"":hr+" ";
            tmp_sched.setDuration(hr+mins);
        }
        Log.d("durations","end");
    }

    private void addPlant(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Add a Plant");
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.add_plant, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextInputEditText plant = dialogView.findViewById(R.id.plantName);
                String str = plant.getText().toString();
                if(!str.replaceAll("\\s","").isEmpty()){
                    schedules.put(str, new ArrayList<PlantSched>());
                    spinnerHead = str;
                    updateSpinner();
                    spinner.setSelection(spinnerHeadPos(str));
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    private void updateSpinner(){
        schedules.putAll(PlantSched.updateLocalSchedules(getContext(),toRemove));

        toRemove = "";
        ArrayList<String> plantNames = new ArrayList<>(schedules.keySet());

        ArrayAdapter<String> plants = new ArrayAdapter<>(getContext(), spinnerRes, plantNames);
        spinner.setAdapter(plants);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("MyLog","Spinner: "+adapterView.getItemAtPosition(i).toString());
                updateRecyclerView(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateRecyclerView(String plantName){
        Log.d("MyLog","updateRecyclerView");
        spinnerHead = plantName;
        tmpSched.clear();
        tmpSched.addAll(schedules.get(plantName));
        updateDurations();
        schedulerAdapter.notifyDataSetChanged();
    }

    private int spinnerHeadPos(String str){
        if(!str.isEmpty()){
            int pos = -1;
            for(String e:schedules.keySet()){
                pos++;
                if(e.equals(str)){
                    return pos;
                }
            }
        }
        return -1;
    }
}
