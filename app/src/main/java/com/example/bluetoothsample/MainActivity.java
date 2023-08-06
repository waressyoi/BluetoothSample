package com.example.bluetoothsample;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.getDefaultAdapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonNFC;
    private Button buttonAddress;
    private Intent bluetoothIntent;
    private IBluetoothDeviceService iBluetoothDeviceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNFC = findViewById(R.id.button_nfc);
        buttonNFC.setOnClickListener(this);

        buttonAddress = findViewById(R.id.button_service);
        buttonAddress.setOnClickListener(this);

        bluetoothIntent = new Intent(getApplication(), BluetoothDeviceService.class);
        // Serviceの開始
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(bluetoothIntent);
//            startService(intent);
        } else {
            startService(bluetoothIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(bluetoothIntent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_nfc) {
            Intent intent = new Intent(MainActivity.this, NFCReaderActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button_service) {
            Intent intent = new Intent("com.example.bluetoothsample.BluetoothDeviceService");
            intent.setPackage("com.example.bluetoothsample");
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                iBluetoothDeviceService = IBluetoothDeviceService.Stub.asInterface(service);
                String address = iBluetoothDeviceService.getBluetoothAddress();
                Log.d(TAG, "onServiceConnected: " + address);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            iBluetoothDeviceService = null;
        }
    };
}