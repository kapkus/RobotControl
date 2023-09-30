package com.pl.edu.prz.robotui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private final String DEVICE_MAC_ADDRESS = "00:22:01:00:12:A7";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public OutputStream outputStream;
    public InputStream inputStream;
    ImageView BT_icon;
    TextView BTSearchTxt;
    Button BTSearchBtn;
    private MyInterface listener;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        BTSearchTxt.setText("Szukam ...");
                        RunBt();

                    } else {
                        BTSearchTxt.setText("Nie można włączyć Bluetooth");
                    }
                }
            }
    );
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.e("device name", deviceName);
                Log.e("device MAC", deviceHardwareAddress);
                if (deviceHardwareAddress.equals(DEVICE_MAC_ADDRESS)) {
                    Log.e("Discovery", deviceName + " found!");
                    bluetoothAdapter.cancelDiscovery();
                    RunBt();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bluetooth_panel, container, false);
        BT_icon = v.findViewById(R.id.BT_icon);
        BTSearchTxt = v.findViewById(R.id.BT_search_txt);
        BTSearchBtn = v.findViewById(R.id.BT_search_btn);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (bluetoothAdapter == null) {
            BTSearchTxt.setText("Urządzenie nie wspiera komunikacji Bluetooth");
            BT_icon.setImageResource(R.drawable.ic_baseline_bluetooth_disabled);
            return;
        }

        BTSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activityResultLauncher.launch(enableBtIntent);
                } else {
                    BtDiscoverDevices();
                }
            }
        });
    }

    public void RunBt() {
        if (BTinit()) {
            BTconnect();
        }
    }

    public void BtDiscoverDevices() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        getActivity().registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
        if (!bluetoothAdapter.isDiscovering()) {
            Log.e("Lookfor", "searching");
        }

    }

    public boolean BTinit() {
        boolean found = false;

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice iterator : bondedDevices) {
            if (iterator.getAddress().equals(DEVICE_MAC_ADDRESS)) {
                device = iterator;
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
            Toast.makeText(getActivity().getApplicationContext(), "Połączenie udane", Toast.LENGTH_LONG).show();
            BTSearchTxt.setText("Connected");
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listener.passStreams(outputStream, inputStream);
        return connected;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyInterface) {
            listener = (MyInterface) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    public interface MyInterface {
        void passStreams(OutputStream out, InputStream in);
    }
}
