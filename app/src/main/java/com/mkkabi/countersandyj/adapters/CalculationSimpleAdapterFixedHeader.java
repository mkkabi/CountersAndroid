package com.mkkabi.countersandyj.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.mkkabi.countersandyj.R;
import com.mkkabi.countersandyj.data.CalculationSimpleViewModel;
import com.mkkabi.countersandyj.data.model.CalculationSimple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalculationSimpleAdapterFixedHeader extends SampleTableAdapter {

    private static List<CalculationSimple> calculations;
    private final int width;
    private final int headerHeight, cellHeight;
    private List<String> topRow;
    private Resources resources;
    private Context context;
    private Activity activity;
    private CalculationSimpleViewModel mWordViewModel;

    public CalculationSimpleAdapterFixedHeader(Activity activity, Context context, List<CalculationSimple> calcs, CalculationSimpleViewModel mWordViewModel) {
        super(context);
        resources = context.getResources();
        this.context = context;
        calculations = calcs;
        this.activity = activity;
        this.mWordViewModel = mWordViewModel;
        width = resources.getDimensionPixelSize(R.dimen.table_width);
        headerHeight = resources.getDimensionPixelSize(R.dimen.table_daynight_header_height);
        cellHeight = resources.getDimensionPixelSize(R.dimen.table_daynight_cell_height);

        topRow = Arrays.asList(resources.getString(R.string.date),
                resources.getString(R.string.previous_data),
                resources.getString(R.string.current_data),
                resources.getString(R.string.consumed),
                resources.getString(R.string.paid));
    }

//    public static Object[] getObjectArray(CalculationSimple calculationSimple) throws NoSuchAlgorithmException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//        Method[] methods = calculationSimple.getClass().getMethods();
//
//        List<Method> getters = new ArrayList<>();
//        for (Method m : methods) {
//            if (isGetter(m)) {
//                getters.add(m);
//            }
//        }
//
//        Object[] objArr = new Object[getters.size()];
//        for (int i = 0; i < getters.size(); i++) {
//            Method m = getters.get(i);
//            objArr[i] = m.invoke(calculationSimple);
//        }
//
//        return objArr;
//    }
//
//    private static boolean isGetter(Method method) {
//        if (!method.getName().toLowerCase().startsWith("get")) return false;
//        if (method.getParameterTypes().length != 0) return false;
//        if (void.class.equals(method.getReturnType())) return false;
//        return true;
//    }


    @Override
    public int getRowCount() {
        if (calculations != null)
            return calculations.size();
        return 0;
    }

    @Override
    public int getColumnCount() {
        return topRow.size() - 1;
    }

    @Override
    public int getWidth(int column) {
        return width;
    }

    @Override
    public int getHeight(int row) {
        if (row == -1)
            return headerHeight;
        else return cellHeight;
    }

    @Override
    public String getCellString(int row, int column) {
        if (row == -1)
            return topRow.get(column + 1);

        if (calculations != null) {
            return parseRow(row, column);
        }
        return "Lorem (" + row + ", " + column + ")";
    }

    @Override
    public int getLayoutResource(int row, int column) {
        final int layoutResource;
        switch (getItemViewType(row, column)) {
            case 0:
                layoutResource = R.layout.item_table1_header;
                break;
            case 1:
                layoutResource = R.layout.item_table1;
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return layoutResource;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setCalculations(List<CalculationSimple> calculationss) {
        calculations = calculationss;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int row, int column, View converView, ViewGroup parent) {
        if (converView == null) {
            converView = getInflater().inflate(getLayoutResource(row, column), parent, false);
        }
        setText(converView, getCellString(row, column));
        if (row == -1) {
            converView.setClickable(true);
            converView.setBackgroundColor(resources.getColor(R.color.tableheader));

            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            converView.setBackgroundResource(outValue.resourceId);


            setTextColor(converView, resources.getColor(R.color.link));
            initClickListenersForTopRow(converView, column);
        }
        if (row >= 0) {
            converView.setOnLongClickListener((cell) -> {
                cell.setAlpha(0.5f);
                editCellDialog(activity, parseRow(row, column), row, column);
                return true;
            });
        }
        return converView;
    }

    private void initClickListenersForTopRow(View view, int column) {
        switch (column) {
            case -1:
                view.setOnClickListener(l -> {
                    Collections.sort(calculations, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                    notifyDataSetChanged();
                });
                break;
            case 0:
                view.setOnClickListener(l -> {
                    Collections.sort(calculations, (o1, o2) -> o1.getPrevious() - o2.getPrevious());
                    notifyDataSetChanged();
                });
                break;
            case 1:
                view.setOnClickListener(l -> {
                    Collections.sort(calculations, (o1, o2) -> o1.getCurrent() - o2.getCurrent());
                    notifyDataSetChanged();
                });
                break;
            case 2:
                view.setOnClickListener(l -> {
                    Collections.sort(calculations, (o1, o2) -> o1.getConsumed() - o2.getConsumed());
                    notifyDataSetChanged();
                });
                break;
            case 3:
                view.setOnClickListener(l -> {
                    Collections.sort(calculations, (o1, o2) -> Double.compare(o1.getPayment(), o2.getPayment()));
                    notifyDataSetChanged();
                });
                break;
        }
    }

    @Override
    public void updateRowInDB(int row) {
        CalculationSimple cs = calculations.get(row);
        mWordViewModel.updateRow(cs.getId(), cs.getDate(), cs.getPrevious(), cs.getCurrent(), cs.getConsumed(), cs.getPayment());
    }

    private String parseRow(int row, int col) {
        switch (col) {
            case -1:
                return calculations.get(row).getDate();
            case 0:
                return calculations.get(row).getPrevious() + "";
            case 1:
                return calculations.get(row).getCurrent() + "";
            case 2:
                return calculations.get(row).getConsumed() + "";
            case 3:
                return calculations.get(row).getPayment() + "";
            default:
                return "";
        }
    }

    private void updateValueInRow(int row, int col, String value) {
        switch (col) {
            case -1:
                calculations.get(row).setDate(value);
                break;
            case 0:
                calculations.get(row).setPrevious(Integer.parseInt(value));
                break;
            case 1:
                calculations.get(row).setCurrent(Integer.parseInt(value));
                break;
            case 2:
                calculations.get(row).setConsumed(Integer.parseInt(value));
                break;
            case 3:
                calculations.get(row).setPayment(Double.parseDouble(value));
                break;
            default:
        }
    }

    private void editCellDialog(Activity activity, String text, int row, int col) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Set other dialog properties
        EditText editText = new EditText(activity.getApplicationContext());
        editText.setText(text);
        editText.setTextColor(resources.getColor(R.color.primaryTextColor));
        editText.setInputType(4);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(50, 25, 50, 25);
        layout.addView(editText);
        builder.setView(layout);
        builder.setTitle(R.string.edit_cell);
        CalculationSimple cs = calculations.get(row);

        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button
            updateValueInRow(row, col, editText.getText().toString());
            notifyDataSetChanged();

            updateRowInDB(row);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
        });

        builder.setNeutralButton(R.string.delete_row, (dialog, which) -> confirmDeleteRow(cs.getId()));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmDeleteRow(int itemID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_row);
        builder.setMessage(R.string.are_you_sure);
        builder.setPositiveButton(R.string.ok, (id, which) -> mWordViewModel.deleteRecords(itemID));
        builder.setNeutralButton(R.string.cancel, (id, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
