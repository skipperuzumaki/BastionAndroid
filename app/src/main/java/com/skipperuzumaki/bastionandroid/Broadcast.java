package com.skipperuzumaki.bastionandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import java.io.IOException;
import java.util.UUID;

public class Broadcast {
    BluetoothAdapter mBtAdaptor;
    BluetoothServerSocket skt;
    Broadcast() {
        mBtAdaptor = BluetoothAdapter.getDefaultAdapter();
    }
    void Start(String uuid) throws IOException {
        System.out.println(UUID.fromString(uuid));
        skt = mBtAdaptor.listenUsingInsecureRfcommWithServiceRecord("ContactTracing", UUID.fromString(uuid));
    }
    void Stop() throws IOException {
        skt.close();
        System.out.println("Stopped Broadcasting");
    }
    void update(String uuid) throws IOException {
        Stop();
        Start(uuid);
    }
}
