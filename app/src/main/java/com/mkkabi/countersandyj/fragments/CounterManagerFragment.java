package com.mkkabi.countersandyj.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterDayNightSolid;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.data.model.Tarif;
import com.mkkabi.countersandyj.data.model.TarifRegular;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import java.util.List;

public abstract class CounterManagerFragment extends Fragment {

    abstract void saveCounter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_edit_counter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save_counter:
                saveCounter();
                return true;
            case R.id.action_faq:
                DataController.showInfoDialog(getActivity(), R.string.faq_text);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        saveCounter();
    }

    TarifRegular createCounterTarifs(LinearLayout layout) {
        TarifRegular tarif = new TarifRegular();
        for (int i = 0; i < layout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) layout.getChildAt(i);
            int threshold = Integer.parseInt(((TextView) row.getChildAt(1)).getText().toString());
            double price = Double.parseDouble(((TextView) row.getChildAt(2)).getText().toString());
            tarif.addStage(threshold, price);
        }
        return tarif;
    }

    boolean checkFieldsInList(List<TextView> textViews) {
        int error = 0;
        for (int i = 0; i < textViews.size(); i++) {
            if (fieldIsSet(textViews.get(i))) {
                textViews.get(i).setHintTextColor(getResources().getColor(R.color.error));
                error++;
            }
        }
        return error == 0;
    }

    boolean tariffFieldsInLinearLayoutAreOK(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) layout.getChildAt(i);
            for (int y = 1; y < 3; y++) {
                TextView textView = (TextView) row.getChildAt(y);
                if (fieldIsSet(textView)) {
                    textView.setHintTextColor(getResources().getColor(R.color.error));
                    return false;
                }
            }
        }
        return true;
    }

    boolean fieldIsSet(TextView field) {
        String string = field.getText().toString();
        return string.equals("") || string.isEmpty();
    }

    Counter createCounter(Context context, Household house, CounterType type, Tarif tarif) {
        if (house != null)
            return DataController.createNewCounter(context, house, type, tarif);
        LoggerToaster.printD(context, "debug", "house is Null");
        return null;
    }

    CounterDayNightSolid createCounterDayNight(Context context, Household house, Tarif tarif) {
        if (house != null)
            return DataController.createNewCounterDayNight(context, house, tarif);
        return null;
    }
}
