package com.mkkabi.countersandyj.data;

import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.Household;

public class CalculationViewModelBridge {
    private static Household household;
    private static Counter counter;

    // created to pass data to a ViewModel class for SQL queries
    public CalculationViewModelBridge(Counter counter, Household household){
        this.counter = counter;
        this.household = household;
    }

    public static void setHousehold(Household household) {
        CalculationViewModelBridge.household = household;
    }

    public static void setCounter(Counter counter) {
        CalculationViewModelBridge.counter = counter;
    }

    public static int getCounterID() {
    if(counter!=null){
        return counter.getID();
    }
        return 0;
    }

    public static long getHouseholdID(){
        if(household!=null){
            return household.getID();
        }
        return 0;
    }


}
