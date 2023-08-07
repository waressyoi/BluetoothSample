package com.example.bluetoothsample;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class BluetoothDeviceService extends Service {

    private static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private Context context;

    private IBluetoothDeviceService.Stub mStub = new IBluetoothDeviceService.Stub() {
        @Override
        public String getBluetoothAddress() {

            String address = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);

            if (address == null) {
                address = getBluetoothMacAddress();
            }
            return address;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        bluetoothManager = context.getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String channelId = "default";
        String title = context.getString(R.string.app_name);

        // 通知からActivityを起動できるようにする
        Intent notifyIntent = new Intent(this, MainActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification　Channel 設定
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId, title, NotificationManager.IMPORTANCE_DEFAULT);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);

                Notification notification = new Notification.Builder(context, channelId)
                        .setContentTitle(title)
                        // android標準アイコンから
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentText("MediaPlay")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .build();

                // startForeground
                startForeground(1, notification);
            }
        } else {

            if (notificationManager != null) {

                Notification notification = new Notification.Builder(context)
                        .setContentTitle(title)
                        // android標準アイコンから
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setContentText("MediaPlay")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .build();

                // startForeground
                startForeground(1, notification);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub.asBinder();
    }

    @SuppressLint("HardwareIds")
    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            @SuppressLint("DiscouragedPrivateApi")
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);

            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bluetoothMacAddress;
    }
}
