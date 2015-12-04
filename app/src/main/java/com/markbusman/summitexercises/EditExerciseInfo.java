package com.markbusman.summitexercises;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class EditExerciseInfo extends AppCompatActivity {

    public final static String EXTRA_WORKOUT_ID = "com.markbusman.summitexercises.WORKOUT_ID";
    public final static String EXTRA_EXERCISE_ID = "com.markbusman.summitexercises.EXERCISE_ID";
    public final static String EXTRA_EXERCISE_NAME = "com.markbusman.summitexercises.EXERCISE_NAME";
    public final static String EXTRA_EDIT_TYPE = "com.markbusman.summitexercises.EXERCISE_EDIT_TYPE";
    private final static int TIMERPOS = 4;

    private String workoutID;
    private String exerciseID;
    private String exerciseName;
    private String typeOfView = "VIEW";
    private SQLHandler mSQLHandler;

    private EditText descText;
    private EditText weightText;
    private EditText repsText;
    private EditText setsText;
    private EditText equipText;
    private EditText instructionsText;
    Switch timerSwitch;
    Spinner hourSpinner;
    Spinner minSpinner;
    Spinner secSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise_info);

        Intent intent = getIntent();
        workoutID = intent.getStringExtra(EXTRA_WORKOUT_ID);
        exerciseID = intent.getStringExtra(EXTRA_EXERCISE_ID);
        exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME);
        typeOfView = intent.getStringExtra(EXTRA_EDIT_TYPE);

        descText = (EditText) findViewById(R.id.editText0);
        weightText = (EditText) findViewById(R.id.editText1);
        repsText = (EditText) findViewById(R.id.editText2);
        setsText = (EditText) findViewById(R.id.editText3);
        equipText = (EditText) findViewById(R.id.editText4);
        instructionsText = (EditText) findViewById(R.id.editText5);

        String hour = "00";
        String min = "00";
        String sec = "00";
        int pos = 0;
        timerSwitch = (Switch) findViewById(R.id.useTimerSwitch);
        /*timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timerChecked(isChecked);
            }
        });*/
        hourSpinner = (Spinner) findViewById(R.id.spinnerHour);
        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(hourSpinner.getContext(),
                R.array.hour_numbers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        hourSpinner.setAdapter(hourAdapter);
        pos = hourAdapter.getPosition(hour);
        hourSpinner.setSelection(pos);
        /*hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected(0, (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        minSpinner = (Spinner) findViewById(R.id.spinnerMinute);
        ArrayAdapter<CharSequence> minAdapter = ArrayAdapter.createFromResource(minSpinner.getContext(),
                R.array.min_numbers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        minSpinner.setAdapter(minAdapter);
        pos = minAdapter.getPosition(min);
        minSpinner.setSelection(pos);
        /*minSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected(1, (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        secSpinner = (Spinner) findViewById(R.id.spinnerSeconds);
        ArrayAdapter<CharSequence> secAdapter = ArrayAdapter.createFromResource(secSpinner.getContext(),
                R.array.min_numbers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        secAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        secSpinner.setAdapter(secAdapter);
        pos = secAdapter.getPosition(sec);
        secSpinner.setSelection(pos);
        /*secSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected(2, (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        if (typeOfView.equals("VIEW")) {
            weightText.setEnabled(false);
            repsText.setEnabled(false);
            setsText.setEnabled(false);
            equipText.setEnabled(false);
            instructionsText.setEnabled(false);
            timerSwitch.setEnabled(false);
            hourSpinner.setEnabled(false);
            minSpinner.setEnabled(false);
            secSpinner.setEnabled(false);
            descText.setEnabled(false);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );

        }


        if (typeOfView.equals("VIEW")) {
            this.setTitle(exerciseName);
        } else if (typeOfView.equals("ADD"))  {
            this.setTitle("Add Exercise");
        } else {
            this.setTitle("Edit Exercise");
        }

        mSQLHandler = new SQLHandler(this);
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_exercise_info, menu);
        if (typeOfView.equals("VIEW")) return false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saveListItem) {
            saveExercise();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {

        timerSwitch.setChecked(false);

        if (!typeOfView.equals("ADD")) {

            String query = "SELECT * FROM Exercises WHERE id=\"" + exerciseID + "\"";
            Cursor cursor = mSQLHandler.selectQuery(query);
            if (cursor.moveToFirst()) {
                do {
                    Exercises exercise = new Exercises();
                    exercise.desc = cursor.getString(1);
                    exercise.equipment = cursor.getString(2);
                    exercise.instructions = cursor.getString(3);
                    //exercise.name = cursor.getString(4);
                    exercise.reps = cursor.getString(5);
                    exercise.sets = cursor.getString(6);
                    exercise.time = cursor.getString(8);
                    int useTimer = Integer.parseInt(cursor.getString(11));
                    if (useTimer != 0) {
                        exercise.useTimer = true;
                    } else {
                        exercise.useTimer = false;
                    }

                    exercise.weight = cursor.getString(12);
                    exercise.rowID = Integer.parseInt(cursor.getString(0));

                    descText.setText(exercise.desc);
                    weightText.setText((exercise.weight));
                    repsText.setText(exercise.reps);
                    setsText.setText(exercise.sets);
                    equipText.setText(exercise.equipment);
                    instructionsText.setText((exercise.instructions));

                    // create the time
                    if (exercise.useTimer) {
                        timerSwitch.setChecked(true);

                        String[] timeComponents = exercise.time.split(":");
                        int hour = Integer.parseInt(timeComponents[0]);
                        int min = Integer.parseInt(timeComponents[1]);
                        int sec = Integer.parseInt(timeComponents[2]);

                        hourSpinner.setSelection(hour);
                        minSpinner.setSelection(min);
                        secSpinner.setSelection(sec);
                    }


                } while (cursor.moveToNext());
            }
        }
    }

    private void saveExercise() {
        String query = "";
        String name = "";
        String timeStamp = new Date().getTime() + "";
        String time = hourSpinner.getSelectedItem() + " : " + minSpinner.getSelectedItem() + " : " + secSpinner.getSelectedItem();

        String useTimer = "0";
        if (timerSwitch.isChecked()) useTimer = "1";

        long order = DatabaseUtils.longForQuery(mSQLHandler.sqlDatabase, "SELECT COUNT(*) FROM Exercises WHERE programID = '" + workoutID + "'", null);
        String hashtag = "" + workoutID.hashCode() + name.hashCode() +  descText.getText().toString().hashCode()
                + equipText.getText().toString().hashCode() + instructionsText.getText().toString().hashCode() + repsText.getText().toString().hashCode() + setsText.getText().toString().hashCode()
                + weightText.getText().toString().hashCode() + time.hashCode() + timeStamp.hashCode();

        if (typeOfView.equals("ADD")) {
            query = "INSERT INTO Exercises ('desc', 'weight', 'reps', 'sets', 'time', 'useTimer', 'equipment', 'instructions', "
                    + "'order', 'timeStamp', 'lastModified', 'programID', 'hashTag', 'checked') VALUES ("
                    + "'" + descText.getText().toString() + "'" +  ","
                    + "'" + weightText.getText().toString() + "'" +  ","
                    + "'" + repsText.getText().toString() + "'" +  ","
                    + "'" + setsText.getText().toString() + "'" +  ","
                    + "'" + time + "'" +  ","
                    + "'" + useTimer + "'" +  ","
                    + "'" + equipText.getText().toString() + "'" +  ","
                    + "'" + instructionsText.getText().toString() + "'" +  ","
                    + "'" + order + "'" +  ","
                    + "'" + timeStamp + "'" +  ","
                    + "'" + timeStamp + "'" +  ","
                    + "'" + workoutID + "'" +  ","
                    + "'" + hashtag + "'" +  ","
                    + "'" + 0 + "'"
                    + ")";
        } else {
            query = "UPDATE Exercises SET 'desc'=\"" + descText.getText().toString() + "\", "
                    + "'weight'=\"" + weightText.getText().toString() + "\", "
                    + "'reps'=\"" + repsText.getText().toString() + "\", "
                    + "'sets'=\"" + setsText.getText().toString() + "\", "
                    + "'equipment'=\"" + equipText.getText().toString() + "\", "
                    + "'instructions'=\"" + instructionsText.getText().toString() + "\", "
                    + "'time'=\"" + time + "\", "
                    + "'useTimer'=\"" + useTimer + "\", "
                    + "'lastModified'=\"" + new Date().getTime() + "\", "
                    + "'hashTag'=\"" + hashtag + "\" WHERE id='" + exerciseID + "'";
        }
       if (!mSQLHandler.executeQuery(query)) {

           new AlertDialog.Builder(this)
                   .setTitle("Error")
                   .setMessage("Exercise could not be saved!")
                           //.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                           //    public void onClick(DialogInterface dialog, int which) {
                           //        // continue with delete
                           //    }
                           //})
                   .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           // do nothing
                       }
                   })
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .show();

       } else {
           Intent returnIntent = new Intent();
           setResult(RESULT_OK, returnIntent);
           finish();
       }
    }

    private void spinnerSelected(int control, String text) {
        /*Log.d("spinner" + control, text);
        String data = (String) mArrayList.get(TIMERPOS);
        String[] parts = data.split("_");
        String[] components = parts[1].split(":");

        String newData = parts[0] + "_";
        String time = "";
        for (int i = 0; i < 3; ++i) {
            if (i == control) {
                newData += text;
                time += text;
            } else {
                newData += components[i];
                time += components[i];
            }
            if (i < 2) {
                newData += ":";
                time += ":";
            }
        }
        exercise.time = time;
        mArrayList.set(TIMERPOS, newData);
        */
    }

    private void timerChecked(Boolean checked) {
        /*Log.d("timer checked", "" + checked);
        String data = (String) mArrayList.get(TIMERPOS);
        String[] parts = data.split("_");
        String newData = parts[1];
        if (checked) {
            exercise.useTimer = true;
            newData = "1_" + newData;
        } else {
            exercise.useTimer = false;
            newData = "0_" + newData;
        }
        mArrayList.set(TIMERPOS, newData);
        */
    }

    /*
    public class EditAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_TIME = 1;
        private static final int TYPE_MAX_COUNT = TYPE_TIME + 1;

        public EditAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifyDataSetChanged();
            mListView.setOnItemClickListener(null);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == TIMERPOS) {
                return TYPE_TIME;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        public int getCount() {
            return mArrayList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final EditTextViewHolder holder;
            String data = (String) mArrayList.get(position);
            String label = (String) mLabelArrayList.get(position);
            int type = getItemViewType(position);

            if (convertView == null) {
                holder = new EditTextViewHolder();
                if (type == TYPE_TIME) {
                    convertView = mInflater.inflate(R.layout.activity_edit_exercise_info_item_timer, null);
                    holder.timerSwitch = (Switch) convertView
                            .findViewById(R.id.switchTimer);
                    holder.hourSpinner = (Spinner) convertView
                            .findViewById(R.id.spinnerHour);
                    holder.minSpinner = (Spinner) convertView
                            .findViewById(R.id.spinnerMinute);
                    holder.secSpinner = (Spinner) convertView
                            .findViewById(R.id.spinnerSeconds);
                } else {
                    convertView = mInflater.inflate(R.layout.activity_edit_exercise_info_item, null);
                    holder.editField = (EditText) convertView
                            .findViewById(R.id.dataTextView);
                    holder.editField.clearFocus();

                }
                holder.labelField = (TextView) convertView
                        .findViewById(R.id.titleTextView);

                convertView.setTag(holder);
            } else {
                holder = (EditTextViewHolder) convertView.getTag();
            }

            holder.position = position;

            if (type == TYPE_TIME) {
                try {
                    // initialize switch
                    holder.timerSwitch.setChecked(false);
                    String[] parts = data.split("_");
                    holder.timerSwitch.setChecked(false);
                    Log.d("part data", data);
                    if (parts[0].equals("1") || parts[0].toLowerCase().equals("true")) {
                        holder.timerSwitch.setChecked(true);
                    }

                    holder.timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            timerChecked(isChecked);
                        }
                    });

                    String hour = "00";
                    String min = "00";
                    String sec = "00";
                    int pos = 0;

                    String[] timeComponents = parts[1].split(":");
                    hour = timeComponents[0];
                    min = timeComponents[1];
                    sec = timeComponents[2];

                    // initalize spinners
                    ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(convertView.getContext(),
                            R.array.hour_numbers_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    holder.hourSpinner.setAdapter(hourAdapter);
                    pos = hourAdapter.getPosition(hour);
                    holder.hourSpinner.setSelection(pos);
                    holder.hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerSelected(0, (String) parent.getItemAtPosition(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayAdapter<CharSequence> minAdapter = ArrayAdapter.createFromResource(convertView.getContext(),
                            R.array.min_numbers_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    holder.minSpinner.setAdapter(minAdapter);
                    pos = hourAdapter.getPosition(min);
                    holder.minSpinner.setSelection(pos);
                    holder.minSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerSelected(1, (String) parent.getItemAtPosition(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayAdapter<CharSequence> secAdapter = ArrayAdapter.createFromResource(convertView.getContext(),
                            R.array.min_numbers_array, android.R.layout.simple_spinner_item);
                    // Specify the layout to use when the list of choices appears
                    secAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    holder.secSpinner.setAdapter(secAdapter);
                    pos = hourAdapter.getPosition(sec);
                    holder.secSpinner.setSelection(pos);
                    holder.secSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerSelected(2, (String) parent.getItemAtPosition(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    if (typeOfView.equals("VIEW")) {
                        holder.timerSwitch.setEnabled(false);
                        holder.hourSpinner.setEnabled(false);
                        holder.minSpinner.setEnabled(false);
                        holder.secSpinner.setEnabled(false);
                    }


                } catch (Exception e) {
                    Log.d("timer entry error", e.getLocalizedMessage());
                }
            }
            else {
                try {
                    holder.editField.setText(data);
                    holder.editField.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    holder.editField.setId(position);
                    if (typeOfView.equals("VIEW")) {
                        holder.editField.setEnabled(false);
                    } else {
                        holder.editField.setBackground(ContextCompat.getDrawable(holder.editField.getContext(), R.drawable.textbox_edit));
                        holder.editField.setPadding(7, 1, 1, 5);
                        holder.editField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.requestFocus();
                                //holder.editField.setBackground(ContextCompat.getDrawable(holder.editField.getContext(), R.drawable.textbox_edit));
                            }
                        });

                        holder.editField.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                mArrayList.set(holder.position, s.toString());
                            }
                        });

                        //we need to update mStableArrayAdapter once we finish with editing
                        holder.editField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    final int pos = v.getId();
                                    final EditText EditField = (EditText) v;
                                    String text = EditField.getText().toString();
                                    mArrayList.set(pos, text);
                                    //holder.editField.setBackground(ContextCompat.getDrawable(holder.editField.getContext(), R.drawable.textbox));


                                }
                            }
                        });
                    }



                } catch (Exception e) {
                    Log.d("regular entry error", e.getLocalizedMessage());
                }
            }


            holder.labelField.setText(label);
            return convertView;
        }
    }

    class EditTextViewHolder {
        EditText editField;
        TextView labelField;
        Switch timerSwitch;
        Spinner hourSpinner;
        Spinner minSpinner;
        Spinner secSpinner;
        int position;
    }
    */
}
