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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Sync {
    long Today;
    File DataFile;
    Crypto Cryptography;
    Encoder Encode;
    List<String> Contracted;
    Sync(File Directory) throws IOException, NoSuchAlgorithmException {
        Cryptography = new Crypto(Directory);
        Encode = new Encoder();
        DataFile = new File(Directory.getPath() + "\\Trace.txt");
        long timestamp = System.currentTimeMillis();
        timestamp /= 1000;
        Today = timestamp / 86400;
    }
    String Start() throws IOException {
        System.out.println("Synchronizing");
        // Removing old values
        final List<String> ls = null;
        StringBuilder Data = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(DataFile));
        String Line;
        while ((Line = br.readLine()) != null) {
            String Timestamp = Line.substring(27);
            if(Today - Long.parseLong(Timestamp) < 30) {
                Data.append(Line);
                ls.add(Line.substring(0,27));
                Data.append('\n');
            }
        }
        br.close();
        FileWriter Writer = new FileWriter(DataFile.getPath());
        Writer.write(Data.toString());
        // fetching data nad comparing to present data
        // TODO : add last checked feature
        final String[] ret = {null};
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
                                    for (String msg : ls){
                                        try {
                                            if (!Cryptography.Verify(Encode.Decode(msg),Encode.Decode(i.toString()))){
                                                ret[0] = document.getId().toString();
                                            }
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
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                }
            });
        return ret[0];
    }
}
