package com.cvsu.pms.plantmonitoringsystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static android.view.View.inflate;

public class SettingFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice device;
    BluetoothAdapter mBluetoothAdapter;
    ListView lv;
    ArrayList<String> mylist;
    String address = null;


    BluetoothSPP bt = new BluetoothSPP(getContext());

    private ProgressDialog progress;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Button disconnectButton;
    TextView textName;
    TextView textStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device, container, false); //modified
        setRetainInstance(true);
        disconnectButton = (Button) rootView.findViewById(R.id.button_Disconnect);
        textName = rootView.findViewById(R.id.textView_deviceName);
        textStatus = rootView.findViewById(R.id.textView_Status);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        mylist = new ArrayList<>();

        //Check if device supports bt
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getContext(), "This device doesn't support bluetooth.", Toast.LENGTH_SHORT);
        }

        //Check if bt is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            //List paired devices in Listview
            for (BluetoothDevice device : pairedDevices) {
                //BluetoothDevice wew = device;
                String name = device.getName() + "\n" + device.getAddress();
                mylist.add(name);
            }
            lv = rootView.findViewById(R.id.device_list);
            //ListAdapter ca = new CustomAdapter(getContext(), mylist);
            ArrayAdapter ca = new ArrayAdapter(getContext(), R.layout.simple_textview, mylist);
            lv.setAdapter(ca);
            lv.setOnItemClickListener(clickListener);

        }

        disconnectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
                msg("Disconnected");
                disconnectButton.setEnabled(false);
                textName.setText("Null");
                textStatus.setText("Not connected");
            }
        });
        return rootView; //added
    }

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            address = info.substring(info.length() - 17);
            new ConnectBT().execute();


            // Make an intent to start next activity.
            //Intent i = new Intent(DeviceList.this, ledControl.class);

            //Change the activity.
            //i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            //startActivity(i);
        }
    };


    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
         //return to the first layout

    }

    public void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(getContext(), "Connecting...", "Please wait.");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = mBluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
                disconnectButton = ((Activity) getContext()).findViewById(R.id.button_Disconnect);
                TextView textName = ((Activity) getContext()).findViewById(R.id.textView_deviceName);
                TextView textStatus = ((Activity) getContext()).findViewById(R.id.textView_Status);

                disconnectButton.setEnabled(true);
                textName.setText("wew");
                textStatus.setText("Connected");
            }
            progress.dismiss();
        }
    }

}


