package com.mkkabi.countersandyj.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "counter_calculation_day_night")
public class CalculationDayNight {

    @NotNull
    @ColumnInfo
    String date;
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;
    @NotNull
    @ColumnInfo
    private double payment, paymentWithRegualarTarif, savings;
    @NotNull
    @ColumnInfo
    private int counterID, previousDay, currentDay, previousNight, currentNight, consumedDay, consumedNight, consumedOverall;
    @NonNull
    @ColumnInfo
    private long houseID;

    public CalculationDayNight(@NotNull String date, @NotNull int previousDay, @NotNull int previousNight,
                               @NotNull int currentDay, @NotNull int currentNight,
                               @NotNull double payment, @NotNull double paymentWithRegualarTarif, @NotNull double savings,
                               @NotNull long houseID, @NotNull int counterID) {
        this.previousDay = previousDay;
        this.previousNight = previousNight;
        this.currentDay = currentDay;
        this.currentNight = currentNight;

        this.consumedDay = currentDay - previousDay;
        this.consumedNight = currentNight - previousNight;
        this.consumedOverall = (currentDay - previousDay) + (currentNight + previousNight);

        this.payment = payment;
        this.paymentWithRegualarTarif = paymentWithRegualarTarif;
        this.savings = savings;
        this.houseID = houseID;
        this.counterID = counterID;

        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getPaymentWithRegualarTarif() {
        return paymentWithRegualarTarif;
    }

    public void setPaymentWithRegualarTarif(double paymentWithRegualarTarif) {
        this.paymentWithRegualarTarif = paymentWithRegualarTarif;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public long getHouseID() {
        return houseID;
    }

    public void setHouseID(int houseID) {
        this.houseID = houseID;
    }

    public int getCounterID() {
        return counterID;
    }

    public void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    public int getPreviousDay() {
        return previousDay;
    }

    public void setPreviousDay(int previousDay) {
        this.previousDay = previousDay;
    }

    // SETTERS//

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getPreviousNight() {
        return previousNight;
    }

    public void setPreviousNight(int previousNight) {
        this.previousNight = previousNight;
    }

    public int getCurrentNight() {
        return currentNight;
    }

    public void setCurrentNight(int currentNight) {
        this.currentNight = currentNight;
    }

    public int getConsumedDay() {
        return consumedDay;
    }

    public void setConsumedDay(int consumedDay) {
        this.consumedDay = consumedDay;
    }

    public int getConsumedNight() {
        return consumedNight;
    }

    public void setConsumedNight(int consumedNight) {
        this.consumedNight = consumedNight;
    }

    public int getConsumedOverall() {
        return consumedOverall;
    }

    public void setConsumedOverall(int consumedOverall) {
        this.consumedOverall = consumedOverall;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
