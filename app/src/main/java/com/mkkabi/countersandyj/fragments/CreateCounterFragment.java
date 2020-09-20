package com.mkkabi.countersandyj.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.controller.Serializer;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterDayNightSolid;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.data.model.Tarif;
import com.mkkabi.countersandyj.data.model.TarifDayNight;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mkkabi.countersandyj.controller.DataController.showInfoDialog;


public class CreateCounterFragment extends CounterManagerFragment {
    private FragmentActivity listener;
    private Context context;
    // Views //
    private List<TextView> textViews;
    private Spinner counterTypesSpinner;
    private TextView addTarifThreshholdText;
    private ImageView dayNightInstructionsButton, addTarifImageButton;
    private CheckBox differentPercentCheckbox;
    private EditText accountNumber, counterName, percentOfNightTariffPrice;
    private LinearLayout tarifsHolder;
    private View holder;
    private static Household household;

    public static CreateCounterFragment newInstance(int someInt, String someTitle) {
        CreateCounterFragment createCounterFragment = new CreateCounterFragment();
        Bundle args = new Bundle();
        args.putInt("houseNumber", someInt);
        args.putString("someTitle", someTitle);
        createCounterFragment.setArguments(args);
        return createCounterFragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.listener = (FragmentActivity) context;
        textViews = new ArrayList<>();
        household = DataController.activeHouse;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.create_new_counter);
        return inflater.inflate(R.layout.add_edit_counter, parent, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        initViews(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(View view) {
        dayNightInstructionsButton = view.findViewById(R.id.dayNightInstructionsButton);
        addTarifImageButton = view.findViewById(R.id.addTarifInfoButton);
        differentPercentCheckbox = view.findViewById(R.id.differentPercentCheckbox);
        percentOfNightTariffPrice = view.findViewById(R.id.percentOfNightTariffPriceEditText);
        counterTypesSpinner = view.findViewById(R.id.counterTypeSpinner_addCounterFragment);
        addTarifThreshholdText = view.findViewById(R.id.addTarifThreshholdText);
        counterTypesSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, CounterType.values()));
        counterTypesSpinner.setSelection(0);
        counterTypesSpinner.setBackgroundColor(getResources().getColor(R.color.spinner));
        counterTypesSpinner.setPrompt(context.getResources().getString(R.string.select_counter_type));
        counterTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                if (CounterType.values()[position] == CounterType.DAYNIGHT) {
                    dayNightInstructionsButton.setVisibility(TextView.VISIBLE);
                    differentPercentCheckbox.setVisibility(CheckBox.VISIBLE);
                } else {
                    dayNightInstructionsButton.setVisibility(TextView.GONE);
                    differentPercentCheckbox.setVisibility(CheckBox.GONE);
                    percentOfNightTariffPrice.setVisibility(EditText.GONE);
                }
            }
        });

//        counterName = view.findViewById(R.id.counterName_addCounterFragment);
        accountNumber = view.findViewById(R.id.counterAccountNumber_addCounterFragment);
        tarifsHolder = view.findViewById(R.id.tarifsHolder);
//        textViews.add(counterName);
        textViews.add(accountNumber);
        textViews.add(accountNumber);

        differentPercentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                percentOfNightTariffPrice.setVisibility(EditText.VISIBLE);
            }
            if (!buttonView.isChecked())
                percentOfNightTariffPrice.setVisibility(EditText.GONE);
        });

        addTarifThreshholdText.setOnClickListener(v -> {
            LinearLayout previousRow = (LinearLayout) tarifsHolder.getChildAt(tarifsHolder.getChildCount() - 1);
            ImageView deleteImageInPreviousRow = (ImageView) previousRow.getChildAt(0);

            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.tarifs, null);
            ImageView deleteImage = (ImageView) rowView.getChildAt(0);
            deleteImage.setVisibility(ImageView.VISIBLE);
            deleteImage.setOnClickListener(c -> {
                tarifsHolder.removeView(rowView);
                if (deleteImageInPreviousRow != null)
                    deleteImageInPreviousRow.setVisibility(ImageView.VISIBLE);
            });

            if (deleteImageInPreviousRow != null) {
                deleteImageInPreviousRow.setVisibility(ImageView.GONE);
            }
            tarifsHolder.addView(rowView, tarifsHolder.getChildCount());
        });

        dayNightInstructionsButton.setOnClickListener(v->{
            showInfoDialog(getActivity(), R.string.daynight_instructions_text);
        });
        addTarifImageButton.setOnClickListener(v->{
            showInfoDialog(getActivity(), R.string.tarifs_instructions_text);
        });
    }

    @Override
    void saveCounter(){
        if (checkFieldsInList(textViews) && tariffFieldsInLinearLayoutAreOK(tarifsHolder)) {
            Tarif tarif;
            if (counterTypesSpinner.getSelectedItem() == CounterType.DAYNIGHT) {
                tarif = createCounterTarifsDayNight(tarifsHolder);
                CounterDayNightSolid counter = createCounterDayNight(context, household, tarif);
                counter.setAccountNumber(accountNumber.getText().toString());
            } else {
                tarif = createCounterTarifs(tarifsHolder);
                Counter counter = createCounter(context,  household, (CounterType)counterTypesSpinner.getSelectedItem(), tarif);
                counter.setAccountNumber(accountNumber.getText().toString());
            }
        } else {
            Toast.makeText(context, R.string.missing_field_value, Toast.LENGTH_SHORT).show();
            return;
        }
        Serializer.saveApp(context);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private TarifDayNight createCounterTarifsDayNight(LinearLayout layout) {
        TarifDayNight tarif = fieldIsSet(percentOfNightTariffPrice) ? new TarifDayNight(50):
                new TarifDayNight(Integer.parseInt(percentOfNightTariffPrice.getText().toString()));

        for (int i = 0; i < layout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) layout.getChildAt(i);
            int threshold = Integer.parseInt(((TextView) row.getChildAt(1)).getText().toString());
            double price = Double.parseDouble(((TextView) row.getChildAt(2)).getText().toString());
            tarif.addStage(threshold, price);
        }
        return tarif;
    }


    @Override
    boolean tariffFieldsInLinearLayoutAreOK(LinearLayout layout) {
        int error = super.tariffFieldsInLinearLayoutAreOK(layout)?0:1;
        if (percentOfNightTariffPrice.getVisibility() == TextView.VISIBLE && fieldIsSet(percentOfNightTariffPrice)) {
            percentOfNightTariffPrice.setHintTextColor(getResources().getColor(R.color.error));
            error++;
        }
        return error == 0;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
//        ( (HouseActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Your title");
    }

}