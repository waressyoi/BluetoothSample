package com.example.bluetoothsample;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.getDefaultAdapter;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class NFCReaderActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
    }

    @Override
    protected void onResume() {
        super.onResume();

        nfcAdapter = getDefaultAdapter(this);
        if (nfcAdapter == null) {
            return;
        }

        nfcAdapter.enableReaderMode(this, new CustomReaderCallback(), FLAG_READER_NFC_A, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);
    }

    private class CustomReaderCallback implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered(Tag tag) {
            Log.d(TAG, "onTagDiscovered: " + tag);

            IsoDep isoDep = IsoDep.get(tag);

            try {
                isoDep.connect();

                byte[] aid = new byte[]{
                        0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06
                };
                byte[] dat = new byte[]{
                        (byte) 0x01, 0x10, 0x00
                };

                Log.d(TAG, "onTagDiscovered: " + byteToHex(isoDep.transceive(aid)));

                Log.d(TAG, "onTagDiscovered: " + byteToHex(isoDep.transceive(dat)));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String byteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}