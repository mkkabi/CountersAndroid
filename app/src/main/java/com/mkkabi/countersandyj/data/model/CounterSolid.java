package com.mkkabi.countersandyj.data.model;

import android.util.Log;

import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;

public class CounterSolid extends AbstractCounter {

    public CounterSolid(){}
    public CounterSolid(Household h, CounterType type, Tarif tt) {

//        this.name = n;
        this.house = h;
        this.type = type;
        h.increaseCountersNumber();
        this.ID = h.getCountersNumber();
        this.tarif = tt;
        Log.d(DEBUG_TAG, "created counterAdapter "+this.accountNumber+" in house "+this.house.getName());
    }


    @Override
    public void setPreviousNight(int prevNight) {}

    @Override
    public int getPreviousNight(){
        return 0;
    }
}
