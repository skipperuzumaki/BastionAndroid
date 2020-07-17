package com.skipperuzumaki.bastionandroid;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Sync {
    long Today;
    File DataFile;
    List<String> Contracted;
    Sync(File Directory) {
        DataFile = new File(Directory.getPath() + "\\Trace.txt");
        long timestamp = System.currentTimeMillis();
        timestamp /= 1000;
        Today = timestamp / 86400;
    }
    void Start() throws IOException {
        System.out.println("Synchronizing");
        // Removing old Values
        /*
        StringBuilder Data = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(DataFile));
        String Line;
        while ((Line = br.readLine()) != null) {
            String Timestamp = Line.substring(27);
            if(Today - Long.parseLong(Timestamp) < 30) {
                Data.append(Line);
                Data.append('\n');
            }
        }
        br.close();
        FileWriter Writer = new FileWriter(DataFile.getPath());
        Writer.write(Data.toString());
        */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Contact_Tracing")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId() != "Diseases") {
                                for (Object i : document.getData().values()){
                                }
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                }
            });

    }
}
