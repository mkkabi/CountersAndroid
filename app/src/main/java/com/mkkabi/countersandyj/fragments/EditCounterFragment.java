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
import com.mkkabi.countersandyj.controller.Serializer;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.data.model.CounterType;
import com.mkkabi.countersandyj.data.model.Household;
import com.mkkabi.countersandyj.data.model.Tarif;
import com.mkkabi.countersandyj.data.model.TarifDayNight;
import com.mkkabi.countersandyj.utils.LoggerToaster;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mkkabi.countersandyj.controller.DataController.showInfoDialog;

public class EditCounterFragment extends CounterManagerFragment {
    private FragmentActivity listener;
    private Context context;
    // Views //
    private List<TextView> textViews;
    private Spinner counterTypesSpinner;
    private TextView addTarifThreshholdText;
    private ImageView dayNightInstructionsButton, addTarifImageButton;
    private CheckBox differentPercentCheckbox;
    private EditText accountNumber, counterName, firstThresholdPrice, percentOfNightTariffPrice;
    private LinearLayout tarifsHolder;
    private View holder;
    private static Counter counter;
    private static Household household;

    public static EditCounterFragment newInstance(Counter count) {
        EditCounterFragment editCounterFragment = new EditCounterFragment();
        Bundle args = new Bundle();
//        args.putInt("houseNumber", someInt);
//        args.putString("someTitle", someTitle);
        editCounterFragment.setArguments(args);
//        counterNumber = someInt;
        counter = count;
        return editCounterFragment;

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) this.listener = (FragmentActivity) context;
        textViews = new ArrayList<>();
//        counter = DataController.activeCounter;
        household = counter.getCounterHouse();
        LoggerToaster.printD(context, "debug", "counter selected "+counter.getAccountNumber());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.edit_counter);
//        getActivity().setTitle(super.getTitle());
        return inflater.inflate(R.layout.add_edit_counter, parent, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        initViews(view);
        if (counter!=null){
            setViewData();
        }
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

    private void setCheckbox(){
        if(counter.getCounterType() == CounterType.DAYNIGHT ) {
            TarifDayNight tarif = (TarifDayNight) counter.getTraif();
            if (tarif.getPercentagePriceForNight() != 50){
                differentPercentCheckbox.setChecked(true);
//                percentOfNightTariffPrice.setVisibility(TextView.VISIBLE);
                percentOfNightTariffPrice.setText(String.valueOf(tarif.getPercentagePriceForNight()));
            }
        }
    }

    private void setViewData(){
        int counterTypeIndex = Arrays.asList(CounterType.values()).indexOf(counter.getCounterType());
        counterTypesSpinner.setSelection(counterTypeIndex);
//        counterName.setText(counter.getAccountNumber());
        accountNumber.setText(String.valueOf(counter.getAccountNumber()));
        setCheckbox();
        setTariffThresholds();

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
    }

    private void setTariffThresholds(){
        Tarif tar = counter.getTraif();
        double baseTarifPrice = tar.getStages().get(0).getPrice();
        if (baseTarifPrice>0)
            firstThresholdPrice.setText(String.valueOf(baseTarifPrice));
        if (tar.getStages().size()>0){
            for (int i =1; i<tar.getStages().size(); i++){
                addNewThresholdTextViews(tar.getStages().get(i).getThreshold(), tar.getStages().get(i).getPrice());
            }
        }

    }

    private void addNewThresholdTextViews(int threshold, double price){
        LinearLayout previousRow = (LinearLayout) tarifsHolder.getChildAt(tarifsHolder.getChildCount() - 1);
        ImageView deleteImageInPreviousRow = (ImageView) previousRow.getChildAt(0);

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.tarifs, null);
        ((EditText)rowView.getChildAt(1)).setText(String.valueOf(threshold));
        ((EditText)rowView.getChildAt(2)).setText(String.valueOf(price));
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
    }

    private void addNewThresholdTextViews(){
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
    }

    private void initViews(View view) {
        dayNightInstructionsButton = view.findViewById(R.id.dayNightInstructionsButton);
        addTarifImageButton = view.findViewById(R.id.addTarifInfoButton);
        differentPercentCheckbox = view.findViewById(R.id.differentPercentCheckbox);
        firstThresholdPrice = view.findViewById(R.id.firstThresholdPrice);
        percentOfNightTariffPrice = view.findViewById(R.id.percentOfNightTariffPriceEditText);
        counterTypesSpinner = view.findViewById(R.id.counterTypeSpinner_addCounterFragment);
        addTarifThreshholdText = view.findViewById(R.id.addTarifThreshholdText);
        counterTypesSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, CounterType.values()));

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
            addNewThresholdTextViews();
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
            } else {
                tarif = createCounterTarifs(tarifsHolder);
            }
            assert counter != null;
//            counter.setName(counterName.getText().toString());
            counter.setType((CounterType)counterTypesSpinner.getSelectedItem());
            counter.setTarif(tarif);
            counter.setAccountNumber(accountNumber.getText().toString());
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

//    boolean checkFieldsInList(List<TextView> textViews) {
//        int error = 0;
//        for (int i = 0; i < textViews.size(); i++) {
//            if (fieldIsSet(textViews.get(i))) {
//                textViews.get(i).setHintTextColor(getResources().getColor(R.color.error));
//                error++;
//            }
//        }
//        return error == 0;
//    }

    @Override
    boolean tariffFieldsInLinearLayoutAreOK(LinearLayout layout) {
        int error = super.tariffFieldsInLinearLayoutAreOK(layout)?0:1;
        if (percentOfNightTariffPrice.getVisibility() == TextView.VISIBLE && fieldIsSet(percentOfNightTariffPrice)) {
            percentOfNightTariffPrice.setHintTextColor(getResources().getColor(R.color.error));
            error++;
        }
        return error == 0;
    }

//    @Override
//    boolean fieldIsSet(TextView field) {
//        return super.fieldIsSet(field);
//    }

//    public void showInfoDialog(Activity activity, int stringRes) {
//        super.showInfoDialog(activity, stringRes);
//    }

}