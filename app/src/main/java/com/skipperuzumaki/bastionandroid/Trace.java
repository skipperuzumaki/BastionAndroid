package com.skipperuzumaki.bastionandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

public class Trace {
    BluetoothLeScanner mBluetoothLeScanner;
    File Data;
    UuidHelper U;
    HashSet<String> UUIDS = new HashSet<String>();
    Trace(File Directory) {
        Data = new File(Directory.getPath() + "\\Trace.txt");
        U = new UuidHelper();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }
    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
            if (uuids != null) {
                for (ParcelUuid uuid : uuids) {
                    String res = U.Recouperate(uuid.toString());
                    if (res != "" && !(UUIDS.contains(res))){
                        UUIDS.add(res);
                        if (UUIDS.size() > 300){
                            UUIDS.clear();
                        }
                        res += ":";
                        long timestamp = System.currentTimeMillis();
                        timestamp /= 1000;
                        timestamp /= 86400;
                        res += String.valueOf(timestamp);
                        System.out.println(res);
                        if (Data.exists()){
                            try {
                                FileWriter Writer = new FileWriter(Data.getPath());
                                Writer.write(res);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                FileWriter Writer = new FileWriter(Data.getPath());
                                Writer.append(res);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e( "BLE", "Discovery onScanFailed: " + errorCode );
            super.onScanFailed(errorCode);
        }
    };
    void Start() {
        System.out.println("Started Scanning");
        mBluetoothLeScanner.startScan(mScanCallback);
    }
    void Stop() {
        mBluetoothLeScanner.stopScan(mScanCallback);
    }
}
