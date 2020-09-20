package com.mkkabi.countersandyj.data.model;

import java.io.Serializable;
import java.util.List;

public interface Tarif extends Serializable {
    List<? extends StageI> getStages();
    void addStage(int threshold, double price);
    double calculate(int previous, int current);
    double calculate(int prevDay, int prevNight, int currDay, int currNight);

    interface StageI {

        int getThreshold();
        double getPrice();
    }
}
