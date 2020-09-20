package com.mkkabi.countersandyj.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TarifDayNight implements Tarif {
    private List<Stage> stages;
    private int percentagePriceForNight;

    public TarifDayNight(int percentage){
        this.percentagePriceForNight = percentage;
        stages = new ArrayList();
    }

    public List<Stage> getStages(){
        return stages;
    }

    public void setPercentage(int percentage){
        percentagePriceForNight = percentage;
    }

    public int getPercentagePriceForNight(){return this.percentagePriceForNight;}

    public void addStage(int threshold, double price){
        stages.add(new Stage(threshold, price));
    }

    class Stage implements Serializable, Tarif.StageI{
        private int threshold;
        private double price;
        private double priceNight;

        Stage(int threshold, double price){
            this.threshold = threshold;
            this.price = price;
            this.priceNight = (price * percentagePriceForNight)/100;
        }

        public void setThreshold(int threshold){
            this.threshold = threshold;
        }
        public void setPrice(double price){
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


    public double calculate(int previousDay, int previousNight, int currentDay, int currentNight){
        int consumptionDay = currentDay-previousDay;
        int consumptionNight = currentNight-previousNight;
        int totalConsumption = consumptionDay+consumptionNight;

        float percentOfTotalConsumptionDay = (float)consumptionDay*100/totalConsumption;
        float percentOfTotalConsumptionNight = (float)consumptionNight*100/totalConsumption;

        float paymentDay = 0, paymentNight = 0;
        int remainder = totalConsumption;

        System.out.println("consumption day "+consumptionDay + "consumption night "+consumptionNight);
        System.out.println("percent of total consump - Day "+ percentOfTotalConsumptionDay);
        System.out.println("percent of total consump - Night "+ percentOfTotalConsumptionNight);
        for(int i = this.getStages().size()-1; i>=0; i--){
            Stage stage = this.getStages().get(i);
            if(remainder > stage.threshold){
                int stageAmount = remainder-stage.threshold;
                System.out.println("stage amount "+stageAmount);
                remainder -= stageAmount;
                System.out.println("remainder "+ remainder);
                paymentDay += stageAmount*percentOfTotalConsumptionDay/100*stage.price;
                System.out.println("paymentDay "+ paymentDay);
                paymentNight += stageAmount*percentOfTotalConsumptionNight/100*stage.priceNight;
                System.out.println("paymentNight "+paymentNight);
            }
        }
        return paymentDay + paymentNight;
    }
}

