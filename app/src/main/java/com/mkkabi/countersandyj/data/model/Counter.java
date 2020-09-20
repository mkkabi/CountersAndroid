package com.mkkabi.countersandyj.data.model;

import java.io.Serializable;

public interface Counter extends Serializable, Comparable {
    // TODO show these details on the counter adapter Item
    double getOverallSpendings();
    double getLastPayment();
    double getOverallSavings();
    void setOverallSavings(double savings);
    int getOverallConsumption();

//    int getLastData();

    void setOverallSpendings(double spendings);
    void setLastPayment(double lastPayment);
//    void setLastData(int lastData);



    //    String getName();
    String toString();

    void setTarif(Tarif tarif);

    void setType(CounterType type);

    Household getCounterHouse();

    CounterType getCounterType();

    Tarif getTraif();

    int getID();

    int getImage();

    int getPreviousData();

    //    void setName(String name);
    void setPreviousData(int previousData);

    String getAccountNumber();

    void setAccountNumber(String string);

    int getPreviousNight();

    // DayNight counter specific actions
    void setPreviousNight(int prevNight);
}
