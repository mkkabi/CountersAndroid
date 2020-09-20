package com.mkkabi.countersandyj.data.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mkkabi.countersandyj.data.model.CalculationDayNight;
import com.mkkabi.countersandyj.data.model.CalculationSimple;


@Database(entities = {CalculationSimple.class, CalculationDayNight.class}, version = 1)
public abstract class CounterCalculationDatabase extends RoomDatabase {

    public abstract CalculationSimpleDao calculationSimpleDao();
    public abstract CalculationDayNightDao calculationDayNightDao();

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile CounterCalculationDatabase INSTANCE;

    public static CounterCalculationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CounterCalculationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CounterCalculationDatabase.class, "counters_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     *
     * If you want to populate the database only when the database is created for the 1st time,
     * override RoomDatabase.Callback()#onCreate
     */
    private static Callback sRoomDatabaseCallback = new Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.
//            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Populate the database in the background.
     * If you want to start with more words, just add them.
     */
//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final CalculationSimpleDao mDao;
//
//        PopulateDbAsync(CounterCalculationDatabase db) {
//            mDao = db.calculationSimpleDao();
//        }
//
//        @Override
//        protected Void doInBackground(final Void... params) {
//            // Start the app with a clean database every time.
//            // Not needed if you only populate on creation.
////                        mDao.deleteAll();
//
//            //            Word word = new Word("Hello");
//            //            mDao.insert(word);
//            //            word = new Word("World");
//            //            mDao.insert(word);
////            mDao.insert(new CalculationSimple(56, 999));
//            return null;
//        }
//    }

}