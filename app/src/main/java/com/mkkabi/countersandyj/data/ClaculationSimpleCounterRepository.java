package com.mkkabi.countersandyj.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.mkkabi.countersandyj.data.db.CalculationDayNightDao;
import com.mkkabi.countersandyj.data.db.CalculationSimpleDao;
import com.mkkabi.countersandyj.data.db.CounterCalculationDatabase;
import com.mkkabi.countersandyj.data.model.CalculationDayNight;
import com.mkkabi.countersandyj.data.model.CalculationSimple;

import java.util.List;

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */

public class ClaculationSimpleCounterRepository {

    private CalculationSimpleDao CalculationSimpleDao;
    private CalculationDayNightDao CalculationDayNightDao;
    private LiveData<List<CalculationSimple>> mCalculationSimple;
    private LiveData<List<CalculationSimple>> calculationsByCounter;
    private LiveData<List<CalculationDayNight>> calculationsDayNightByCounter;

    // Note that in order to unit test the ClaculationSimpleCounterRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ClaculationSimpleCounterRepository(Application application, int counterID, long houseID) {
        CounterCalculationDatabase db = CounterCalculationDatabase.getDatabase(application);

        CalculationSimpleDao = db.calculationSimpleDao();
        CalculationDayNightDao = db.calculationDayNightDao();
        mCalculationSimple = CalculationSimpleDao.getStatsByDate();
        calculationsByCounter = CalculationSimpleDao.getStatsByCounter(counterID, houseID);
        calculationsDayNightByCounter = CalculationDayNightDao.getStatsByCounter(counterID, houseID);
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<CalculationSimple>> getAllWords() {
        return mCalculationSimple;
    }

    LiveData<List<CalculationSimple>> getStatsByCounter() {
        return calculationsByCounter;
    }

    LiveData<List<CalculationDayNight>> getStatsByCounterDayNight() {
        return calculationsDayNightByCounter;
    }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    void insert(CalculationSimple calculation) {
        new insertAsyncTask(CalculationSimpleDao).execute(calculation);
    }

    void deleteItem(int ID) {
        new deleteItemTask(CalculationSimpleDao, ID).execute();
    }

    void editDate(int ID, String date) {
        new editDateTask(CalculationSimpleDao, ID, date).execute();
    }

    void updateRow(int id, String date, int previos, int current, int consumed, double paid) {
        new updateRowTask(CalculationSimpleDao, id, date, previos, current, consumed, paid).execute();
    }

    void insertDayNight(CalculationDayNight calculation) {
        new insertAsyncTaskDayNight(CalculationDayNightDao).execute(calculation);
    }

    void updateDayNight(int itemID, String date, int prevDay, int prevNight, int currDay, int currNight,
                        int consumedDay, int consumedNight, int consumedTotal,
                        double payment, double paymentRegular, double saved) {
        new updateAsyncTaskDayNight(CalculationDayNightDao, itemID, date, prevDay, prevNight, currDay, currNight,
                consumedDay, consumedNight, consumedTotal,
                payment, paymentRegular, saved).execute();
    }

    void deleteDayNighItem(int ID){
        new deleteDayNightItemTask(CalculationDayNightDao, ID).execute();
    }

    private static class insertAsyncTask extends AsyncTask<CalculationSimple, Void, Void> {

        private CalculationSimpleDao mAsyncTaskDao;

        insertAsyncTask(CalculationSimpleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CalculationSimple... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteItemTask extends AsyncTask<CalculationSimple, Void, Void> {
        private CalculationSimpleDao mAsyncTaskDao;
        private int itemID;

        deleteItemTask(CalculationSimpleDao dao, int ID) {
            mAsyncTaskDao = dao;
            itemID = ID;
        }

        @Override
        protected Void doInBackground(CalculationSimple... calculationSimples) {
            mAsyncTaskDao.deleteItem(itemID);
            return null;
        }
    }

    private static class editDateTask extends AsyncTask<CalculationSimple, Void, Void> {
        private CalculationSimpleDao mAsyncTaskDao;
        private int itemID;
        private String date;

        editDateTask(CalculationSimpleDao dao, int ID, String datum) {
            mAsyncTaskDao = dao;
            itemID = ID;
            date = datum;
        }

        @Override
        protected Void doInBackground(CalculationSimple... calculationSimples) {
            mAsyncTaskDao.editDate(itemID, date);
            return null;
        }
    }

    private static class updateRowTask extends AsyncTask<CalculationSimple, Void, Void> {
        private CalculationSimpleDao mAsyncTaskDao;
        private int itemID, previous, current, consumed;
        private String date;
        private double paid;

        updateRowTask(CalculationSimpleDao dao, int id, String datum, int previos, int current, int consumed, double paid) {
            mAsyncTaskDao = dao;
            itemID = id;
            date = datum;
            this.previous = previos;
            this.current = current;
            this.consumed = consumed;
            this.paid = paid;
        }

        @Override
        protected Void doInBackground(CalculationSimple... calculationSimples) {
            mAsyncTaskDao.updateRow(itemID, date, previous, current, consumed, paid);
            return null;
        }
    }

    private static class insertAsyncTaskDayNight extends AsyncTask<CalculationDayNight, Void, Void> {

        private CalculationDayNightDao mAsyncTaskDao;

        insertAsyncTaskDayNight(CalculationDayNightDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CalculationDayNight... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTaskDayNight extends AsyncTask<CalculationDayNight, Void, Void> {
        int itemID, prevDay, prevNight, currDay, currNight, consumedDay, consumedNight, consumedTotal;
        String date;
        double payment, paymentRegular, saved;
        private CalculationDayNightDao mAsyncTaskDao;

        public updateAsyncTaskDayNight(CalculationDayNightDao mAsyncTaskDao, int itemID, String date, int prevDay, int prevNight, int currDay, int currNight,
                                       int consumedDay, int consumedNight, int consumedTotal,
                                       double payment, double paymentRegular, double saved) {
            this.itemID = itemID;
            this.prevDay = prevDay;
            this.prevNight = prevNight;
            this.currDay = currDay;
            this.currNight = currNight;
            this.consumedDay = consumedDay;
            this.consumedNight = consumedNight;
            this.consumedTotal = consumedTotal;
            this.date = date;
            this.payment = payment;
            this.paymentRegular = paymentRegular;
            this.saved = saved;
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(final CalculationDayNight... params) {
            mAsyncTaskDao.updateRow(itemID, date, prevDay, prevNight, currDay, currNight,
                    consumedDay, consumedNight, consumedTotal,
                    payment, paymentRegular, saved);
            return null;
        }
    }

    private static class deleteDayNightItemTask extends AsyncTask<CalculationDayNight, Void, Void> {
        private CalculationDayNightDao mAsyncTaskDao;
        private int itemID;

        deleteDayNightItemTask(CalculationDayNightDao dao, int ID) {
            mAsyncTaskDao = dao;
            itemID = ID;
        }

        @Override
        protected Void doInBackground(CalculationDayNight... params) {
            mAsyncTaskDao.deleteItem(itemID);
            return null;
        }
    }
}