package com.skipperuzumaki.bastionandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    File FileDirectory;
    Crypto Cryptography;
    Broadcast Broadcaster;
    Encoder Encode;
    UuidHelper UuidH;
    Boolean Running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    device.fetchUuidsWithSdp();
                }
                else if (BluetoothDevice.ACTION_UUID.equals(action)) {
                    BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Parcelable[] uuids =intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                    for (Parcelable ep : uuids) {
                        String uuid = ep.toString();
                        System.out.println(uuid);
                    }
                }
            }};
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter1);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_UUID);
        registerReceiver(mReceiver, filter2);
        setContentView(R.layout.activity_main);
        FileDirectory = getFilesDir();
        Broadcaster = new Broadcast();
        Running = false;
        UuidH = new UuidHelper();
        Encode = new Encoder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Cryptography = new Crypto(FileDirectory);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final Button button = (Button) findViewById(R.id.Toggle);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (!Running) {
                    Running = true;
                    button.setText("Stop Contact Tracing");
                    try {
                        Broadcaster.Start(UuidH.Generate(Encode.Encode(Cryptography.Encrypt())));
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Running = false;
                    button.setText("Start Contact Tracing");
                    try {
                        Broadcaster.Stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}


