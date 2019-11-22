package com.example.kitesurfingapp;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class InternalStorage{

    private InternalStorage() {}

    //Scriem datele cachuite intr-un fisier temporar
    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    //Citim datele cachuite
    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {

        if(fileExist(context, key)) {

            FileInputStream fis = context.openFileInput(key);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return object;
        } else {

            return null;
        }
    }

    //Determinam daca fisierul cu nume fName exista in pachet
    private static boolean fileExist(Context context, String fName){
        File file = context.getFileStreamPath(fName);
        return file.exists();
    }
}
