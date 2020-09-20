package com.mkkabi.countersandyj.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mkkabi.countersandyj.UtilitiesAccounting;
import com.mkkabi.countersandyj.data.model.Household;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Serializer {

    private static Serializer instance = new Serializer();

    public static Serializer getInstance() {
        Log.d("debug", "Serializer.getInstance() before first if");
        if (instance == null) {
            Log.d("debug", "in Serializer.getInstance() after first if");
            synchronized (Serializer.class) {
                if (instance == null) {
                    Log.d("debug", "in Serializer.getInstance() after second if");
                    instance = new Serializer();
                    if(DataController.debugOn){
                        Toast.makeText(UtilitiesAccounting.getAppContext(), "message from Serializer.getInstance", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return instance;
    }

    private Serializer() {
        this.restoreObjects(UtilitiesAccounting.getAppContext());
    }

    private void restoreObjects(Context context){
        Log.d("debug", "Serializer.restoreObjects() called ");
        String filePath = context.getFilesDir().getPath() + "/appsaves";
        File file = new File(filePath);
        try (ObjectInputStream loader = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                Household house = (Household) loader.readObject();
                Log.d("debug", "restored from save "+house);
                Household.households.add(house);
            }
        } catch (FileNotFoundException f) {
//            Logger.getGlobal().log(Level.INFO, f.toString());
        } catch (IOException | ClassNotFoundException e) {
//            Logger.getGlobal().log(Level.INFO, e.toString());

        }
    }

    public static <T> void saveApp(Context context) {
        String filePath = context.getFilesDir().getPath().toString() + "/appsaves";
        File file = new File(filePath);

        try (ObjectOutputStream saver = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Household t : Household.households) {
                saver.writeObject(t);
                if(DataController.debugOn){
                    Log.d("debug", "saved "+t.toString());
                    Toast.makeText(UtilitiesAccounting.getAppContext(), "saved "+t.toString(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (FileNotFoundException e) {
//            Logger.getGlobal().log(Level.INFO, e.toString());
        } catch (IOException io) {
//            Logger.getGlobal().log(Level.INFO, io.toString());
        }
    }

    static <T> void saveObjects(Context context, List<T> objects) {
        String filePath = context.getFilesDir().getPath().toString() + "/appsaves";
        File file = new File(filePath);

        try (ObjectOutputStream saver = new ObjectOutputStream(new FileOutputStream(file))) {
            for (T t : objects) {
                saver.writeObject(t);
                Log.d("debug", "saved "+t.toString());
            }
        } catch (FileNotFoundException e) {
//            Logger.getGlobal().log(Level.INFO, e.toString());
        } catch (IOException io) {
//            Logger.getGlobal().log(Level.INFO, io.toString());
        }
    }
}
