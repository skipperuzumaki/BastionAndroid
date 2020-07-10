package com.skipperuzumaki.bastionandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class Broadcast {
    BluetoothAdapter mBtAdaptor;
    BluetoothLeAdvertiser adv;
    AdvertiseSettings settings;
    Broadcast() {
        mBtAdaptor = BluetoothAdapter.getDefaultAdapter();
        adv = mBtAdaptor.getBluetoothLeAdvertiser();
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_POWER )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_LOW )
                .setConnectable( false )
                .build();
    }
    void Start(String uuid) throws IOException {
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( uuid ) );
        System.out.println(pUuid);
        AdvertiseData.Builder databuild = new AdvertiseData.Builder();
        databuild.setIncludeDeviceName(false);
        databuild.addServiceUuid(pUuid);
        AdvertiseData data = databuild.build();
        System.out.println(data);
        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };
        adv.startAdvertising( settings, data, advertisingCallback );
    }
    void Stop() throws IOException {
        System.out.println("Stopped Broadcasting");
    }
    void update(String uuid) throws IOException {
        Stop();
        Start(uuid);
    }
}
