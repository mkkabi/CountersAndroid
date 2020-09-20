package com.mkkabi.countersandyj.data;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mkkabi.countersandyj.data.model.CalculationDayNight;
import com.mkkabi.countersandyj.data.model.CalculationSimple;

import java.util.List;

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */
public class CalculationSimpleViewModel extends AndroidViewModel {

    private ClaculationSimpleCounterRepository mRepository;

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<CalculationSimple>> mAllCalculations;
    private LiveData<List<CalculationSimple>> calculationsByCounter;
    private LiveData<List<CalculationDayNight>> calculationsByCounterDayNight;

    public CalculationSimpleViewModel(Application application) {
        super(application);
        // @TODO need to find a better solution/design pattern instead of using a bridge class
        //  Using a linking class Bridge to transfer counterID and houseID for database Lookups
        // not a very standard way I suppose
        mRepository = new ClaculationSimpleCounterRepository(application, CalculationViewModelBridge.getCounterID(), CalculationViewModelBridge.getHouseholdID());
        mAllCalculations = mRepository.getAllWords();
        calculationsByCounter = mRepository.getStatsByCounter();
        calculationsByCounterDayNight = mRepository.getStatsByCounterDayNight();
    }

    LiveData<List<CalculationSimple>> getAllWords() {
        return mAllCalculations;
    }
    public LiveData<List<CalculationSimple>> getStatsByCounter() {
        return calculationsByCounter;
    }

    public void insert(CalculationSimple word) {
        mRepository.insert(word);
    }

    public void deleteRecords(int ID){
        mRepository.deleteItem(ID);
    }

    public void editDate(int ID, String date){
        mRepository.editDate(ID, date);
    }

    public void updateRow(int ID, String date, int previos, int current, int consumed, double paid){
        mRepository.updateRow(ID, date, previos, current, consumed, paid);
    }

    public LiveData<List<CalculationDayNight>> getCalculationsByCounterDayNight() {
        return calculationsByCounterDayNight;
    }

    public void insertDayNightCalculation(CalculationDayNight calc) {
        mRepository.insertDayNight(calc);
    }

    public void updateDayNight (int itemID, String date, int prevDay, int prevNight, int currDay, int currNight,
                                int consumedDay, int consumedNight, int consumedTotal,
                                double payment, double paymentRegular, double saved){
        mRepository.updateDayNight(itemID, date, prevDay, prevNight, currDay, currNight,
                consumedDay, consumedNight, consumedTotal,
                payment, paymentRegular, saved);
    }

    public void deleteDayNighItem(int ID){
        mRepository.deleteDayNighItem(ID);
    }


}