package com.mkkabi.countersandyj.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TarifRegular implements Tarif {
    private List<Stage>stages;

    public TarifRegular(){
        stages = new ArrayList();
    }

    public List<Stage> getStages(){
        return stages;
    }

    public void addStage(int threshold, double price){
        stages.add(new Stage(threshold, price));
    }

    class Stage implements Serializable, Tarif.StageI{
        private int threshold;
        private double price;

        Stage(int threshold, double price){
            this.threshold = threshold;
            this.price = price;
        }

        private void setThreshold(int threshold){
            this.threshold = threshold;
        }
        private void setPrice(double price){
            this.price = price;
        }
        @Override
        public int getThreshold(){
            return this.threshold;
        }
        @Override
        public double getPrice(){
            return this.price;
        }
    }

    public double calculate(int previous, int current){
        int remainder = current-previous;
        double payment = 0;
        for(int i = this.getStages().size()-1; i>=0; i--){
            Stage stage = this.getStages().get(i);
            if(remainder > stage.threshold){
                int consumedInThisStage = remainder - stage.threshold;
                remainder -= consumedInThisStage;
                payment += consumedInThisStage*stage.price;
            }
        }
        return payment;
    }

    @Override
    public double calculate(int prevDay, int prevNight, int currDay, int currNight) {
        return calculate(prevDay+prevNight, currDay+currNight);
    }

}
