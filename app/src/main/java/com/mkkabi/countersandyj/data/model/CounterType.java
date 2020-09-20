package com.mkkabi.countersandyj.data.model;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.UtilitiesAccounting;

public enum CounterType {

   WATER (R.string.water,"/resources/images/water.png", R.drawable.water, R.drawable.waterbackground1),
   GAS (R.string.gas,"/resources/images/gas.png", R.drawable.gas, R.drawable.gasbackground1),
   ELECTRICITY (R.string.electricity,"resources/images/electricity.png", R.drawable.electricity, R.drawable.electricitybackground1),
   TRASH (R.string.trash,"resources/images/trashico.png", R.drawable.trashico, R.drawable.trashbackground1),
   HOTWATER (R.string.hotwater,"resources/images/hotwaterico.png", R.drawable.hotwaterico, R.drawable.hotwaterbackground1),
   DAYNIGHT (R.string.daynight,"resources/images/daynightico.png", R.drawable.daynightico, R.drawable.daynight);
//   CUSTOM ("custom","resources/images/tachometer.png", "R.drawable.gas");


   private String icoLocation;
   private String name;
   private String image;
    private int imageInt, backgroundImage;
    int nameResource;

   CounterType(int nameResource,String ico, int imageInt, int backgroundImage) {
       this.nameResource = nameResource;
       this.icoLocation = ico;
       this.imageInt = imageInt;
       this.backgroundImage = backgroundImage;
   }

   public int ico() {
       return this.imageInt;
   }

   public int getBackgroundImage(){return this.backgroundImage;}

    public int getNameResource() {
        return nameResource;
    }

    @Override
    public String toString() {
        return UtilitiesAccounting.getAppContext().getString(this.nameResource);
    }

}
