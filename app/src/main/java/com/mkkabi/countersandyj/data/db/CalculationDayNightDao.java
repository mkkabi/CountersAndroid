package com.mkkabi.countersandyj.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mkkabi.countersandyj.data.model.CalculationDayNight;

import java.util.List;


@Dao
public interface CalculationDayNightDao {

    @Query("SELECT * from counter_calculation_day_night ORDER BY date ASC")
    LiveData<List<CalculationDayNight>> getStatsByDate();

    @Query("SELECT * from counter_calculation_day_night WHERE counterID LIKE :counterID AND houseID LIKE :houseID ORDER BY date DESC, ID DESC")
    LiveData<List<CalculationDayNight>> getStatsByCounter(int counterID, long houseID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalculationDayNight calculation);

    @Query("DELETE FROM counter_calculation_day_night")
    void deleteAll();

    @Query("DELETE FROM counter_calculation_day_night WHERE ID LIKE :recordID")
    void deleteItem(int recordID);

    @Query("UPDATE counter_calculation_day_night SET date=:date WHERE id=:id")
    int editDate(int id, String date);

    @Query("UPDATE counter_calculation_day_night SET date=:date, previousDay=:prevDay, previousNight=:prevNight, " +
            "currentDay=:currDay, currentNight=:currNight, consumedDay=:consumedDay, consumedNight=:consumedNight, " +
            "consumedOverall=:consumedTotal, payment=:payment, paymentWithRegualarTarif=:paymentRegular, savings=:saved WHERE id=:itemID")
    void updateRow(int itemID, String date, int prevDay, int prevNight, int currDay, int currNight, int consumedDay,
                   int consumedNight, int consumedTotal, double payment, double paymentRegular, double saved);

}
