package com.mkkabi.countersandyj.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "counter_calculation_simple")
public class CalculationSimple {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NotNull
    @ColumnInfo
    private double payment;

    @ColumnInfo
    private long houseID;
    @NotNull
    @ColumnInfo
    private int counterID;
    @NotNull
    @ColumnInfo
    private int previous;
    @NotNull
    @ColumnInfo
    private int current;
    @NotNull
    @ColumnInfo
    private int consumed;

    @NotNull
    @ColumnInfo
    String date;

    public CalculationSimple(@NotNull int previous, @NotNull int current) {
        this.previous = previous;
        this.current = current;
//        this.consumed = current-previous;
//        this.rate = rate;
//        this.payment = payment;
//        this.date = new Date(System.currentTimeMillis()).toString();
//        this.counter = counter;
//        this.house = house;
//        this.counterID = counterID;
//        this.houseID = house.getID();
    }

    public int getId() {
        return id;
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }

    public int getConsumed() {
        return consumed;
    }

    public double getPayment() {
        return payment;
    }

    public int getCounterID() {
        return counterID;
    }

    public long getHouseID() {
        return houseID;
    }

    public String getDate() {
        return date;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setConsumed(int consumed) {
        this.consumed = consumed;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    public void setHouseID(long houseID) {
        this.houseID = houseID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

}
