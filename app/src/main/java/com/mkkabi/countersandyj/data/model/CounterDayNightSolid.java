package com.mkkabi.countersandyj.data.model;

import android.util.Log;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class CounterDayNightSolid extends AbstractCounter {

    int previousNight;

    public CounterDayNightSolid(){}

    public CounterDayNightSolid(Household h, CounterType type, Tarif tt) {

//        this.name = n;
        this.house = h;
        this.type = type;
        h.increaseCountersNumber();
        this.ID = h.getCountersNumber();
        this.tarif = tt;
        Log.d(DEBUG_TAG, "created counterAdapter "+this.getAccountNumber()+" in house "+this.house.getName());
    }

    @Override
    public void setPreviousNight(int prevNight) {
        previousNight = prevNight;
    }

    @Override
    public int getPreviousNight(){
        return this.previousNight;
    }

    @Override
    public int getOverallConsumption(){
        return previousNight + previousData;
    }
}
