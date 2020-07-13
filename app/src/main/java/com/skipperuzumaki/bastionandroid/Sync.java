package com.skipperuzumaki.bastionandroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Sync {
    long Today;
    File DataFile;
    Sync(File Directory) {
        DataFile = new File(Directory.getPath() + "\\Trace.txt");
        long timestamp = System.currentTimeMillis();
        timestamp /= 1000;
        Today = timestamp / 86400;
    }
    void Start() throws IOException {
        System.out.println("Synchronizing");
        // Removing old Values
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
    }
}
