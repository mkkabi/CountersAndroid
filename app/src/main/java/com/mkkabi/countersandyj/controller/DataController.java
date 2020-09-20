package com.mkkabi.countersandyj.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterDayNightSolid;
import com.mkkabi.countersandyj.data.model.CounterSolid;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.data.model.Tarif;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class DataController {

    private static DataController instance = new DataController();
    public static List<Household> households = Household.households;
    public static Household activeHouse;
    public static Counter activeCounter;
    private static boolean refreshRequired;   // a flag to detect if other activity needs to be refreshed
    public static boolean debugOn = false;

    public static DataController getInstance() {
        if (instance == null) {
            synchronized (DataController.class) {
                if (instance == null) {
                    instance = new DataController();
                }
            }
        }
        return instance;
    }

    private DataController() {
    }

    public static void createNewHousehold(Context context, String name){
        Household house = new Household(name);
        Serializer.saveObjects(context, Household.households);
        Log.d("debug", "DataController.createNewHousehold - created new House with name "+ name);
    }

    public static Counter createNewCounter(Context context, Household house, CounterType type, Tarif tarif) {
        Counter counter = new CounterSolid(house, type, tarif);
        house.addCounter(counter);
        Log.d(DEBUG_TAG, "DataController.createNewCounter() created counter "+counter.getAccountNumber()+" in "+counter.getCounterHouse());
        Serializer.saveObjects(context, Household.households);
        setRefreshRequired();
        return counter;
    }

    public static CounterDayNightSolid createNewCounterDayNight(Context context, Household house, Tarif tarif) {
        CounterDayNightSolid counter = new CounterDayNightSolid(house, CounterType.DAYNIGHT, tarif);
        house.addCounter(counter);
        Log.d(DEBUG_TAG, "DataController.createNewCounter() created counter "+counter.getAccountNumber()+" in "+counter.getCounterHouse());
        Serializer.saveObjects(context, Household.households);
        setRefreshRequired();
        return counter;
    }

    public static void deleteCounter(Counter counter, Context context, Runnable onCancel){
        showConfirmationDialog(context, onCancel, ()->{
            removeCounterFromHouse(counter);
            Serializer.saveObjects(context, Household.households);
        });
        setRefreshRequired();
        System.out.println("removed "+counter.getAccountNumber()+" from "+counter.getCounterHouse());
    }

    private static void removeCounterFromHouse(Counter counter){
        counter.getCounterHouse().getCounters().remove(counter);
    }

    public static void removeHousehold(Household house, Context context, Runnable onCancel){
        DataController.showConfirmationDialog(context, onCancel, ()->{
            Household.households.remove(house);
            Serializer.saveObjects(context, Household.households);
        });
    }

    public static void moveCounter(Counter counter, Household newHouse){
//        Household oldHouse = counter.getCounterHouse();
        removeCounterFromHouse(counter);
        newHouse.addCounter(counter);
    }

    private static void showConfirmationDialog(Context context, Runnable onCancel, Runnable onConfirm){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(R.string.are_you_sure);
        ad.setPositiveButton(R.string.remove, (dialog, arg1)-> {
                Toast.makeText(context, R.string.item_removed, Toast.LENGTH_LONG).show();
                onConfirm.run();
                onCancel.run();
        });

        ad.setNegativeButton(R.string.cancel, (d, a)->onCancel.run());

        ad.setCancelable(true);
        ad.setOnCancelListener((d)->{
            Toast.makeText(context, R.string.action_canceled, Toast.LENGTH_LONG).show();
                onCancel.run();
        });
        ad.show();
    }

    public static void createFile(String uri){
        //context.getFilesDir().getPath().toString()
        File file = new File(uri);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d("debug", e.toString());
        }
    }

    public static boolean needToRefresh(){
        return refreshRequired;
    }

    public static void setRefreshRequired(){
        refreshRequired = true;
    }

    public void refreshDone(){refreshRequired = false;}


    public static void showInfoDialog(Activity activity, int stringRes) {
        LoggerToaster.printD(activity.getApplicationContext(),DEBUG_TAG, "inside showalertDialog ");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {});
        builder.setMessage(stringRes);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showInfoDialog(Activity activity, String message) {
        LoggerToaster.printD(activity.getApplicationContext(),DEBUG_TAG, "inside showalertDialog ");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {});
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}












