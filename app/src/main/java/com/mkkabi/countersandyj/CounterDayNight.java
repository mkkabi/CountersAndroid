package com.mkkabi.countersandyj;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.mkkabi.countersandyj.adapters.DayNightTableAdapter;
import com.mkkabi.countersandyj.adapters.OnSwipeTouchListener;
import com.mkkabi.countersandyj.controller.DataController;
import com.mkkabi.countersandyj.controller.Serializer;
import com.mkkabi.countersandyj.data.CalculationSimpleViewModel;
import com.mkkabi.countersandyj.data.CalculationViewModelBridge;
import com.mkkabi.countersandyj.data.model.CalculationDayNight;
import com.mkkabi.countersandyj.data.model.Counter;
import com.mkkabi.countersandyj.utils.DriveServiceHelper;
import com.mkkabi.countersandyj.utils.LoggerToaster;
import com.mkkabi.countersandyj.utils.PermissionUtil;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import static com.google.android.gms.common.api.CommonStatusCodes.SUCCESS;
import static com.mkkabi.countersandyj.controller.Const.COUNTER_LIST_POSITION;
import static com.mkkabi.countersandyj.controller.Const.DEBUG_TAG;
import static com.mkkabi.countersandyj.controller.Const.POSITION_NOT_SET;
import static com.mkkabi.countersandyj.utils.Constants.DUBUG;

@TargetApi(22)  //Using runtime permission checks to target devices above Marshmallow
public class CounterDayNight extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    ///   GOOGLE DRIVE REST API Variables
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static String[] PERMISSIONS_FILES = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int GAPS_AVAILABLE;
    private final String TAG = "debug";
    int counterNumber = POSITION_NOT_SET;
    int backgroundImage;
    Context context;
    ImageView headerImage;
    Counter counter = DataController.activeCounter;
    private final String fileNameToExport = counter.getCounterType() + "_" + counter.getAccountNumber() + ".csv";
    View mLayout;
    ///   GOOGLE DRIVE REST API Variables End
    Locale locale;
    NumberFormat nf;
    private Activity activity;
    private Intent intent;
    private TextView previousDay, currentDay, previousNight, currentNight;
    private String startingDir;
    private CalculationSimpleViewModel mWordViewModel;
    private DriveServiceHelper mDriveServiceHelper;
    private GoogleApiAvailability mGoogleApiAvailability;
    private Runnable googleTask, filesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_counter_day_night);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity = this;
        context = CounterDayNight.this;

        mGoogleApiAvailability = GoogleApiAvailability.getInstance();

        locale = Locale.ENGLISH;
        nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        startingDir = "/mnt/sdcard/";

        CalculationViewModelBridge bridge = new CalculationViewModelBridge(counter, DataController.activeHouse);
        // Get a new or existing ViewModel from the ViewModelProvider.
        mWordViewModel = new ViewModelProvider(this).get(CalculationSimpleViewModel.class);
        intent = getIntent();
        counterNumber = intent.getIntExtra(COUNTER_LIST_POSITION, POSITION_NOT_SET);
        initViews();

        /*Table Fixed Headers Start*/
        TableFixHeaders tableFixHeaders = findViewById(R.id.table);

        DayNightTableAdapter myAdapter =
                new DayNightTableAdapter(activity, context, mWordViewModel.getCalculationsByCounterDayNight().getValue(), mWordViewModel);

        mWordViewModel.getCalculationsByCounterDayNight().observe(this, myAdapter::setCalculations);
        tableFixHeaders.setAdapter(myAdapter);
        /* Table Fixed Headers END */

        mLayout.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                LoggerToaster.printD(context, DEBUG_TAG, "swiped left");
                onSupportNavigateUp();
            }
        });
    }

    double getAllSpendings() {
        double result = 0;
        for (CalculationDayNight c : Objects.requireNonNull(mWordViewModel.getCalculationsByCounterDayNight().getValue())) {
            result = result + c.getPayment();
        }
        return result;
    }

    double getAllSavings() {
        double result = 0;
        for (CalculationDayNight c : Objects.requireNonNull(mWordViewModel.getCalculationsByCounterDayNight().getValue())) {
            result = result + c.getSavings();
        }
        return result;
    }

    public void calculate() {
        int prevDay, currDay, prevNight, currNight;
        double payment, paymentRegularTarif, savings;
        try {
            prevDay = Integer.parseInt(previousDay.getText().toString());
            prevNight = Integer.parseInt(previousNight.getText().toString());
            currDay = Integer.parseInt(currentDay.getText().toString());
            currNight = Integer.parseInt(currentNight.getText().toString());

            payment = nf.parse(nf.format(counter.getTraif().calculate(prevDay, prevNight, currDay, currNight))).doubleValue();
            System.out.println(payment);
            paymentRegularTarif = nf.parse(nf.format(counter.getTraif().calculate(prevDay + prevNight, currDay + currNight))).doubleValue();
            System.out.println(paymentRegularTarif);
            savings = nf.parse(nf.format(paymentRegularTarif - payment)).doubleValue();
            System.out.println(savings);

            counter.setPreviousData(currDay);
            counter.setPreviousNight(currNight);
            counter.setLastPayment(payment);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date datum = new Date();
            String date = format.format(datum);
            createAndInsertLine(date, prevDay, prevNight, currDay, currNight, payment, paymentRegularTarif, savings);

        } catch (NumberFormatException e) {
            Toast.makeText(context, getResources().getString(R.string.missing_field_value), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void createAndInsertLine(String date, int previousDay, int previousNight, int currentDay, int currentNight,
                                    double payment, double paymentWithRegualarTarif, double savings) {

        CalculationDayNight word2 = new CalculationDayNight(date, previousDay, previousNight, currentDay, currentNight,
                payment, paymentWithRegualarTarif, savings, counter.getCounterHouse().getID(), counter.getID());
        mWordViewModel.insertDayNightCalculation(word2);
    }

    public void initViews() {
        mLayout = findViewById(R.id.constraintLayout);
        previousNight = findViewById(R.id.previousNight);
        currentNight = findViewById(R.id.currentNight);
        previousDay = findViewById(R.id.previousDay);
        currentDay = findViewById(R.id.currentDay);
        headerImage = findViewById(R.id.headerImage);
        backgroundImage = DataController.activeCounter.getCounterType().getBackgroundImage();
        headerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), backgroundImage));
        previousDay.setText(counter.getPreviousData() <= 0 ? "0" : counter.getPreviousData() + "");
        previousNight.setText(counter.getPreviousNight() <= 0 ? "0" : counter.getPreviousNight() + "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_counter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_calculate) {
            calculate();
            return true;
        }
        if (id == R.id.action_export) {
            filesTask = () -> new ChooserDialog(CounterDayNight.this)
                    .withFilter(true, false)
                    .withStartFile(startingDir)
                    .withChosenListener((path, pathFile) -> {
                        Toast.makeText(context, getResources().getString(R.string.file_saved) + "" + path, Toast.LENGTH_SHORT).show();
                        LoggerToaster.printD(context, TAG, "CounterActivity, folder selected: " + path);
                        createAndSaveExportFile(Objects.requireNonNull(mWordViewModel.getCalculationsByCounterDayNight().getValue()),
                                path + "/" + fileNameToExport);
                    }).build().show();
            requestExternalStoragePermissions();
            return true;
        }
        if (id == R.id.action_import) {
            filesTask = () -> new ChooserDialog(CounterDayNight.this)
                    .withStartFile(startingDir)
                    .withChosenListener((path, pathFile) -> {
                        Toast.makeText(CounterDayNight.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        LoggerToaster.printD(context, TAG, "selected File " + path);
                        readFileToDB(path);
                    })
                    // to handle the back key pressed or clicked outside the dialog:
                    .withOnCancelListener(dialog -> {
                        Log.d("CANCEL", "CANCEL");
                        dialog.cancel(); // MUST have
                    }).build().show();

            requestExternalStoragePermissions();
            return true;
        }

        if (id == R.id.action_export_to_gdrive) {
            googleTask = () -> mCreateFile(fileNameToExport, constructExportFileContents(Objects.requireNonNull(mWordViewModel.getCalculationsByCounterDayNight().getValue())));
            requestSignIn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        LoggerToaster.printD(context, DUBUG, "onBackPressed function");
        saveCounterData();
        super.onBackPressed();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //replaces the default 'Back' button action
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            LoggerToaster.printD(context, DUBUG, "onKeyDown function");
//            saveCounterData();
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        //replaces the default 'Back' button action
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            LoggerToaster.printD(context, DUBUG, "onKeyUp function");
//            saveCounterData();
//        }
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        LoggerToaster.printD(context, DUBUG, "onSupportNavigateUp function");
        onBackPressed();
        return true;
    }


    private void saveCounterData() {
        try {
            counter.setOverallSpendings(Objects.requireNonNull(nf.parse(nf.format(getAllSpendings()))).doubleValue());
            counter.setOverallSavings(Objects.requireNonNull(nf.parse(nf.format(getAllSavings()))).doubleValue());
            LoggerToaster.printD(context, DUBUG, "saving counter data");
        } catch (ParseException e) {
            Log.d("Error", e.toString());
        }
        Serializer.saveApp(context);
    }

    /*PERMISSIONS START*/
    private void requestExternalStoragePermissions() {
        // BEGIN_INCLUDE(storage_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.d(TAG,
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            //@TODO need to rearrange this to string file
            Snackbar.make(mLayout, getResources().getString(R.string.storage_permissions_explanation_message),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view -> ActivityCompat
                            .requestPermissions(CounterDayNight.this, PERMISSIONS_FILES,
                                    REQUEST_EXTERNAL_STORAGE))
                    .show();
        } else {
            // sTORAGE permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_FILES, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(storage_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            LoggerToaster.printD(context, TAG, "Received response for storage permissions request.");

            // We have requested multiple permissions for storage, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display storage fragment.
                Snackbar.make(mLayout, "Permission available",
                        Snackbar.LENGTH_SHORT)
                        .show();
                filesTask.run();
            } else {
                LoggerToaster.printD(context, TAG, "storage permissions were NOT granted.");
                Snackbar.make(mLayout, "Permission not granted", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    /*PERMISSIONS END*/

    /* File Import-Export START*/
    public <T extends CalculationDayNight> void createAndSaveExportFile(List<T> list, String uri) {
        String result = constructExportFileContents(list);
        try (FileWriter fw = new FileWriter(new java.io.File(uri))) {
            fw.write(result);
        } catch (IOException ex) {
            LoggerToaster.printD(context, TAG, "error writing to file" + ex.toString());
        }
    }

    private <T extends CalculationDayNight> String constructExportFileContents(List<T> list) {
        StringBuilder buff = new StringBuilder();

        buff.append(getResources().getString(R.string.calculation_day_night_fileheader)).append("\r\n");
        for (CalculationDayNight calc : list) {
            buff.append(calc.getDate()).append(",").
                    append(calc.getPreviousDay()).append(",").
                    append(calc.getPreviousNight()).append(",").
                    append(calc.getCurrentDay()).append(",").
                    append(calc.getCurrentNight()).append(",").
                    append(calc.getConsumedDay()).append(",").
                    append(calc.getConsumedNight()).append(",").
                    append(calc.getPaymentWithRegualarTarif()).append(",").
                    append(calc.getSavings()).append(",").
                    append(calc.getPayment()).append("\r\n");
        }
        return buff.toString();
    }

    private void readFileToDB(String path) {
        java.io.File file = new java.io.File(path);
        int linesImported = 0;
        int currentLine = 0;
        ArrayList<Integer> errorLines = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            sc.nextLine();
            while (sc.hasNextLine()) {
                currentLine++;
                String line = sc.nextLine();
                String[] arr = line.split(",");
                try {
                    String date = arr[0];
                    int prevDay = Integer.parseInt(arr[1]);
                    int prevNight = Integer.parseInt(arr[2]);
                    int currDay = Integer.parseInt(arr[3]);
                    int currNight = Integer.parseInt(arr[4]);
                    double paymentWithRegualarTarif = Double.parseDouble(arr[7]);
                    double savings = Double.parseDouble(arr[8]);
                    double payment = Double.parseDouble(arr[9]);
                    createAndInsertLine(date, prevDay, prevNight, currDay, currNight,
                            payment, paymentWithRegualarTarif, savings);
                    linesImported++;
                } catch (NumberFormatException number) {
                    LoggerToaster.printD(context, TAG, "error parsing item from line: " + number.toString());
                    errorLines.add(currentLine + 1);
                } catch (ArrayIndexOutOfBoundsException arrError) {
                    LoggerToaster.printD(context, TAG, "Current line of the input file does not match structure " + arrError.toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(getResources().getString(R.string.imported_lines)).append(" ").append(linesImported);
        if (errorLines.size() > 0) {
            buffer.append("\n").append(getResources().getString(R.string.errors_importing_lines)).append(" ");
            for (int i : errorLines) {
                buffer.append(i).append(" ");
            }
        } else {
            buffer.append("\n").append(getResources().getString(R.string.no_errors_found));
        }
        String message = buffer.toString();
        DataController.showInfoDialog(activity, message);
    }

    /* File Import-Export END*/

    /*  GOOGLE DRIVE REST API START **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d(TAG, "inside onActivityResult DayNightCounter");
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                } else {
                    Dialog dialog = mGoogleApiAvailability.getErrorDialog(activity, REQUEST_CODE_SIGN_IN, 0);
                    dialog.show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void requestSignIn() {
        LoggerToaster.printD(context, TAG, "inside requestSignIn");
        GAPS_AVAILABLE = mGoogleApiAvailability.isGooglePlayServicesAvailable(context);
        if (GAPS_AVAILABLE == SUCCESS) {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                            .build();
            GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        } else {
            Dialog dialog = mGoogleApiAvailability.getErrorDialog(activity, GAPS_AVAILABLE, 0);
            dialog.show();
            dialog.setOnCancelListener(c -> Toast.makeText(this, getResources().getString(R.string.google_services_authorization_canceled), Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Utilities Accounting")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    googleTask.run();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Unable to sign in.", exception);
                    Toast.makeText(context, getResources().getString(R.string.unable_to_sign_in), Toast.LENGTH_SHORT).show();
                });
    }

    private void mCreateFile(String name, String content) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");
//            mDriveServiceHelper.deleteFolderFile(name+".csv").addOnSuccessListener(v->Toast.makeText(this, "Older File removed", Toast.LENGTH_SHORT).show()).
//                    addOnFailureListener(v->Toast.makeText(this, "Previous file was not removed", Toast.LENGTH_SHORT).show());
            mQuery(name);
            mDriveServiceHelper.createFile(name).addOnSuccessListener(
                    fileId -> mDriveServiceHelper.saveFile(fileId, name, content).addOnSuccessListener(v ->
                            Snackbar.make(mLayout, getResources().getString(R.string.file_uploaded) + " " + name, Snackbar.LENGTH_SHORT).show())).
                    addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    void mQuery(String name) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        for (File file : fileList.getFiles()) {
                            if (file.getName().equals(name))
                                mDriveServiceHelper.deleteFolderFile(file.getId()).addOnSuccessListener(v -> Log.d(TAG, "removed file " + file.getName())).
                                        addOnFailureListener(v -> Log.d(TAG, "File was not removed: " + file.getName()));
                        }
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }
    /*  GOOGLE DRIVE REST API END **/

}