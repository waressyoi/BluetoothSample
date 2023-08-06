package com.example.bluetoothsample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class ApduService extends HostApduService {
    String _TAG = "ApduService";

    class APDUdatas {
        public byte CLA;
        public byte INS;
        public byte P1;
        public byte P2;
        public byte[] raw;
    }

    private byte[] RESPONSE_SUCCESS = new byte[]{(byte) 0x90, (byte) 0x00};

    private byte[] RESPONSE_EXECUTION_ERROR = new byte[]{(byte) 0x64, (byte) 0xFF};
    private byte[] RESPONSE_INVALID_COMMAND = new byte[]{(byte) 0x69, (byte) 0xFF};

    private byte[] RESPONSE_FILE_NOT_FOUND = new byte[]{(byte) 0x6A, (byte) 0x82};
    private byte[] RESPONSE_WRITE_ERROR = new byte[]{(byte) 0x65, (byte) 0x00};

    private byte[] RESPONSE_AUTHENTICATION_ERROR = new byte[]{(byte) 0x63, (byte) 0x00};
    private byte[] RESPONSE_FILE_FULL = new byte[]{(byte) 0x63, (byte) 0x81};

    private byte[] RESPONSE_NO_FUNCTION = new byte[]{(byte) 0x6A, (byte) 0x81};
    private byte[] RESPONSE_COMMAND_ERROR = new byte[]{(byte) 0x69, (byte) 0x85};
    private byte[] RESPONSE_INVALID_INS = new byte[]{(byte) 0x6D, (byte) 0x00};
    private byte[] RESPONSE_INVALID_CLA = new byte[]{(byte) 0x6E, (byte) 0x00};
    private byte[] RESPONSE_INVALID_LC_LE = new byte[]{(byte) 0x67, (byte) 0x00};

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(_TAG, "onDestroy");
    }

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        Log.d(_TAG, "processCommandApdu");

        //SELECT FILE
        if (apdu[0] == (byte) 0x00 && apdu[1] == (byte) 0xA4 && apdu[2] == (byte) 0x04 && apdu[3] == (byte) 0x00) {
            Log.d(_TAG, "processCommandApdu : SELECT FILE");
            return RESPONSE_SUCCESS;
        }

        Calendar cal = Calendar.getInstance();
        return new byte[] { (byte)cal.get(Calendar.HOUR_OF_DAY), (byte)cal.get(Calendar.MINUTE), (byte)cal.get(Calendar.SECOND), (byte)0x90, 0x00 };
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(_TAG, "onDeactivated");
    }
}
