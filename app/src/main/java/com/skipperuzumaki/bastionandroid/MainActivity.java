package com.skipperuzumaki.bastionandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    boolean Running;
    Sync Syncronisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Running = false;
        try {
            Syncronisation = new Sync(getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        int permissionCheckBt = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        if (permissionCheckBt != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN)){
                Toast.makeText(this, "Bluetooth Permission is Required", Toast.LENGTH_SHORT).show();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 1);
            }
        }
        int permissionCheckLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheckLoc != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.Toggle);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (!Running) {
                    Running = true;
                    button.setText("Stop Contact Tracing");
                    startService();
                }
                else{
                    Running = false;
                    button.setText("Start Contact Tracing");
                    stopService();
                }
            }
        });
        final Button btn = (Button) findViewById(R.id.Sync);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String temp = Syncronisation.Start();
                    if (temp != null) {
                        AlertNegative(temp);
                    }
                    else{
                        AlertPositive();
                    }
                } catch (IOException e) {
                    AlertPositive();
                }
            }
        });
        final Button Keybtn = (Button) findViewById(R.id.Key);
        Keybtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    AlertKey();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void startService() {
        Intent serviceIntent = new Intent(this, Foreground.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, Foreground.class);
        stopService(serviceIntent);
    }
    public void AlertPositive(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("You have not come in contact with any infectious person");
        dialog.setTitle("Result");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // No Need TO do anything
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
    public void AlertNegative(String Disease){
        Disease = "Sorry You came in Contact with a person suffering form " + Disease;
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(Disease);
        dialog.setTitle("Result");
        dialog.setNegativeButton("Understood",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Todo Maybe : add information about the disease in question
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
    public void AlertKey() throws IOException, NoSuchAlgorithmException {
        File Data = new File(getFilesDir().getPath() + "\\Keys.txt");
        String algorithm = "ChaCha20";
        byte[] keyBytes;
        if (Data.exists()){
            keyBytes = Files.readAllBytes(Data.toPath());
        }
        else{
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            SecretKey Key = keyGen.generateKey();
            keyBytes = Key.getEncoded();
            FileOutputStream stream = new FileOutputStream(Data.getPath());
            stream.write(keyBytes);
            stream.close();
        }
        Encoder E = new Encoder();
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(E.Encode(keyBytes));
        dialog.setTitle("Key");
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // No Need TO do anything
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
}


