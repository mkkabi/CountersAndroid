package com.mkkabi.countersandyj.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.mkkabi.countersandyj.data.model.CalculationSimple;

import java.util.List;


@Dao
public interface CalculationSimpleDao {

    @Query("SELECT * from counter_calculation_simple ORDER BY date ASC")
    LiveData<List<CalculationSimple>> getStatsByDate();

    @Query("SELECT * from counter_calculation_simple WHERE counterID LIKE :counterID AND houseID LIKE :houseID ORDER BY date DESC, ID DESC")
    LiveData<List<CalculationSimple>> getStatsByCounter(int counterID, long houseID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalculationSimple calculation);

    @Query("DELETE FROM counter_calculation_simple")
    void deleteAll();

    @Query("DELETE FROM counter_calculation_simple WHERE ID LIKE :recordID")
    void deleteItem(int recordID);

    @Query("UPDATE counter_calculation_simple SET date=:date WHERE id=:id")
    void editDate(int id, String date);

    @Query("UPDATE counter_calculation_simple SET date=:date, previous=:previous, current=:current, " +
            "consumed=:consumed, payment=:paid WHERE id=:itemID")
    void updateRow(int itemID, String date, int previous, int current, int consumed, double paid);

}
