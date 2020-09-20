package com.mkkabi.countersandyj.data.model;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractCounter implements Counter {

    protected CounterType type;
    protected Household house;
    protected int ID, previousData;
    protected String accountNumber;
    protected Tarif tarif;
    protected double overallSpendings;
    protected double lastPayment;
    protected double overallSavings;

    @Override
    public int getOverallConsumption(){
        return previousData;
    }
    @Override
    public double getOverallSavings(){
        return overallSavings;
    }

    @Override
    public void setOverallSavings(double savings){
        this.overallSavings = savings;
    }

    @Override
    public double getOverallSpendings() {
        return overallSpendings;
    }

    @Override
    public double getLastPayment() {
        return lastPayment;
    }


    protected int lastData;

    @Override
    public String getAccountNumber(){
        return this.accountNumber;
    }
    @Override
    public int getImage() {
        return type.ico();
    }
    @Override
    public int getID(){
        return ID;
    }
    @Override
    public int getPreviousData() {
        return this.previousData;
    }
    @Override
    public Household getCounterHouse(){return this.house;}
    @Override
    public CounterType getCounterType(){
        return this.type;
    }
    @Override
    public Tarif getTraif(){return this.tarif;}

    // SETTERS
    @Override
    public void setTarif(Tarif tarif){
        this.tarif = tarif;
    }
    @Override
    public void setPreviousData(int previousData) {
        this.previousData = previousData;
    }
    @Override
    public void setType(CounterType type) {
        this.type = type;
    }
    @Override
    public void setAccountNumber(String accountNumber){this.accountNumber = accountNumber;}
    @Override
    public void setOverallSpendings(double overallSpendings) {
        this.overallSpendings = overallSpendings;
    }
    @Override
    public void setLastPayment(double lastPayment) {
        this.lastPayment = lastPayment;
    }

    @NotNull
    @Override
    public String toString() {
        return type.name()+": "+accountNumber;
    }
    @Override
    public int compareTo(@NotNull Object o) {
        if(o instanceof Counter)
            return getID() - (((Counter)o).getID());
        return -1;
    }
}
