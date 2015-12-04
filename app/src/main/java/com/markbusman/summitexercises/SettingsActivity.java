package com.markbusman.summitexercises;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    Switch printSwitch;
    Switch alarmCountdownSwitch;
    Spinner alarmSoundSpinner;
    String[] soundArray = {"airhorn", "applause", "doorbell", "helicopter", "mountainlion", "tornadosiren"};
    SharedPreferences sharedPref;
    /*
    <string-array name="sounds_array">
        <item>Air Horn</item>
        <item>Applause</item>
        <item>Door Bell</item>
        <item>Helicopter</item>
        <item>Mountain Lion</item>
        <item>Tornado Siren</item>
    </string-array>

    File file;
        file = new File(Environment.getExternalStorageDirectory(), "SummitExercisesData" + date.getTime() + ".xml");
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);
        //getPreferences(this.MODE_PRIVATE);

        String alarmFileName = "airhorn";
        Boolean countdownValue = true;
        Boolean printValue = true;
        try {
            countdownValue = sharedPref.getBoolean(getString(R.string.useCountdownKey), false);
            //getBoolean(R.string.useCountdownKey);
        } catch (ClassCastException e) {

        }
        try {
            printValue = sharedPref.getBoolean(getString(R.string.useLinesWhenPrintingKey), false);
        } catch (ClassCastException e) {
            //Log.d("error" , "no value for print" );
        }
        try {
            alarmFileName = sharedPref.getString(getString(R.string.alarmFileKey), "airhorn");
        } catch (ClassCastException e) {

        }

        Button clearData = (Button) findViewById(R.id.button_clearolddata);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                file = new File(Environment.getExternalStorageDirectory().getPath());//, "SummitExercisesData" + ".xml");
                String[] fileList = file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.contains(".xml") && filename.contains("SummitExercisesData")) {
                            return true;
                        }
                        return false;
                    }
                });
                for (String item: fileList) {
                    File fileToRemove = new File(Environment.getExternalStorageDirectory(), item);
                    Boolean deleted = fileToRemove.delete();
                    if (!deleted) {
                        //Log.d("File Delete", "could not delete file" + fileToRemove.getPath());
                    }
                }

            }
        });


        printSwitch = (Switch) findViewById(R.id.printingSwitch);
        printSwitch.setChecked(printValue);
        printSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePrintSetting(isChecked);
            }
        });

        alarmCountdownSwitch = (Switch) findViewById(R.id.endCountdownSwitch);
        alarmCountdownSwitch.setChecked(countdownValue);
        alarmCountdownSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCountdown(isChecked);
            }
        });

        alarmSoundSpinner = (Spinner) findViewById(R.id.alarmSoundSpinner);

        int pos = Arrays.asList(soundArray).indexOf(alarmFileName);
        if (pos != -1) {
            alarmSoundSpinner.setSelection(pos);
        }
        alarmSoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAudioSetting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updatePrintSetting(Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.useLinesWhenPrintingKey), value);
        editor.commit();
        //Log.d("update print settings", value + "");

    }

    private void updateCountdown(Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.useCountdownKey), value);
        editor.commit();
    }

    private void updateAudioSetting(int position) {
        //Log.d("sound: ", soundArray[position]);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.alarmFileKey), soundArray[position]);
        editor.commit();
    }

}
