package com.mkkabi.countersandyj.data.model;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Household<T extends Counter> implements Serializable {

    public static List<Household> households = new ArrayList<>();
    private String name;
    private List<T> counters;
    private final long ID;
    private int countersNumber;

    public Household(String n) {
        this.ID = System.currentTimeMillis();
        name = n;
        System.out.println("created Household with name " + n);
        counters = new ArrayList<>();
        countersNumber = 0;
        households.add(this);
    }
    
    void increaseCountersNumber(){
        countersNumber ++;
    }

    public void addCounter(T c){
        Log.d("debug", "Household.addCounter() added Counter "+c.toString()+" to "+this.getName());
        counters.add(c);
    }
    
    int getCountersNumber(){
        return this.countersNumber;
    }

    public long getID(){
        return this.ID;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return this.name;
    }

    public List<T> getCounters() {
        return this.counters;
    }

}
