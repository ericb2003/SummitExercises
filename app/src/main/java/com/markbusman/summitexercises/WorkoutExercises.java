package com.markbusman.summitexercises;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class WorkoutExercises extends AppCompatActivity {

    protected DynamicListView mListView;
    protected ArrayList<Exercises> mDataList;
    protected StableArrayAdapter mStableArrayAdapter;
    private String workoutID;
    private String workoutName;
    private int selectedExercise = -1;

    private SQLHandler mSQLHandler;

    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    int soundStrikeID = 1;
    int soundEraseID = 2;

    public final static String EXTRA_WORKOUT_ID = "com.markbusman.summitexercises.WORKOUT_ID";
    public final static String EXTRA_WORKOUT_NAME = "com.markbusman.summitexercises.WORKOUT_NAME";
    public final static String EXTRA_EXERCISE_ID = "com.markbusman.summitexercises.EXERCISE_ID";
    public final static String EXTRA_EXERCISE_NAME = "com.markbusman.summitexercises.EXERCISE_NAME";
    public final static String EXTRA_EDIT_TYPE = "com.markbusman.summitexercises.EXERCISE_EDIT_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_exercises);

        //Log.d("create", "starting workoutexercises");

        Intent intent = getIntent();
        workoutID = intent.getStringExtra(EXTRA_WORKOUT_ID);
        workoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME);
        this.setTitle(workoutName);

        mSQLHandler = new SQLHandler(this);
        loadData();

        mListView = (DynamicListView) findViewById(R.id.ExercisesList);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setDataList(mDataList);
        mStableArrayAdapter = new StableArrayAdapter(this, R.layout.exercise_list_item, mDataList);
        mListView.setAdapter(mStableArrayAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedExercise = position;
                //Log.d("clicked cell ", "" + selectedExercise);
                //Log.d("cell value ", mDataList.get(position).desc);
                if (!view.isSelected()) {
                    view.setSelected(true);
                }
            }
        });
        mListView.setListRefreshedListener(new DynamicListView.ListRefreshedListener() {
            @Override
            public void listRefreshed() {
                reOrdered();

            }
        });


        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(soundStrikeID, soundPool.load(this, R.raw.strike, 1));
        soundPoolMap.put(soundEraseID, soundPool.load(this, R.raw.erase, 1));

        Button addButton = (Button) findViewById(R.id.button_addexercise);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercise();
            }
        });

        Button editButton = (Button) findViewById(R.id.button_editexercise);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editExercise();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.button_deleteexercise);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExercise();
            }
        });

        Button viewButton = (Button) findViewById(R.id.button_viewexercise);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewExercise();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loadData();
            mListView.setDataList(mDataList);
            mStableArrayAdapter = new StableArrayAdapter(this, R.layout.exercise_list_item, mDataList);
            mListView.setAdapter(mStableArrayAdapter);
            mStableArrayAdapter.notifyDataSetChanged();
            selectedExercise = -1;

        }

    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("resumed", "workoutexercises" + mListView.getAdapter().toString());


    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout_exercises, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
 /*       if (id == R.id.action_editListItem) {
            editExercise();
            return true;
        }

        if (id == R.id.action_addListItem) {
            addExercise();
            mListView.invalidateViews();
            return true;
        }

        if (id == R.id.action_deleteListItem) {
            deleteExercise();
            return true;
        }

        if (id == R.id.action_viewListItem) {
            viewExercise();
            return true;
        }*/

        if (id == R.id.action_markListItem) {
            markExercise();
            return true;
        }

        if (id == R.id.action_resetListItems) {
            resetAll();
            return true;
        }

        if (id == R.id.action_resetListTimers) {
            resetTimers();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        mDataList = new ArrayList<Exercises>();
        String query = "Select * FROM Exercises WHERE programID=\"" + workoutID + "\"";
        Cursor cursor = mSQLHandler.selectQuery(query);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Exercises exercise = new Exercises();
                exercise.rowID = Long.parseLong(cursor.getString(0));
                exercise.desc = cursor.getString(1);
                exercise.weight = cursor.getString(12);
                exercise.reps = cursor.getString(5);
                exercise.sets = cursor.getString(6);
                exercise.time = cursor.getString(8);
                exercise.order = Integer.parseInt(cursor.getString(7));
                int useTimer = Integer.parseInt(cursor.getString(11));
                if (useTimer != 0) {
                    exercise.useTimer = true;
                } else {
                    exercise.useTimer = false;
                }
                exercise.programID = cursor.getString(13);
                int checked = Integer.parseInt(cursor.getString(15));
                if (checked != 0) {
                    exercise.checked = true;
                } else {
                    exercise.checked = false;
                }
                //Log.d("exercise id", exercise.rowID + "");
                mDataList.add(exercise);

            } while (cursor.moveToNext());
        }
        Collections.sort(mDataList, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                int exercise1 = ((Exercises) lhs).order;
                int exercise2 = ((Exercises) rhs).order;
                return Integer.valueOf(exercise1).compareTo(Integer.valueOf(exercise2));
                //return 0;
            }
        });

    }

    private void reOrdered() {
        ArrayList<Exercises> exercises = mListView.getDataList();
        for (int i = 0; i < exercises.size(); ++i) {
            Exercises exercise = exercises.get(i);
            String query = "UPDATE Exercises SET 'order'=\"" + i + "\" WHERE id=\"" + exercise.rowID + "\"";
            if (mSQLHandler.executeQuery(query)) {
                exercise.order = i;
                //Log.d("reorder: (" + exercise.desc + "):", query);
            } else {
                //Log.d("error:", "sql problem - " + query);
            }
            mListView.updateDataItem(exercise, i);
        }
    }

    private void addExercise() {
        Intent intent = new Intent(this, EditExerciseInfo.class);
        intent.putExtra(EXTRA_WORKOUT_ID, workoutID);
        intent.putExtra(EXTRA_EDIT_TYPE, "ADD");
        intent.putExtra(EXTRA_EXERCISE_ID, "NULL");
        intent.putExtra(EXTRA_EXERCISE_NAME, "New Exercise");
        startActivityForResult(intent, 1);
    }

    private void deleteExercise() {
        if (selectedExercise == -1)  return;
        Exercises exercise = mDataList.get(selectedExercise);
        String query = "DELETE FROM Exercises WHERE id=\"" + exercise.rowID + "\"";
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Deleting Exercise Failed!")
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
            mListView.removeDataItem(selectedExercise);
            //mDataList.remove(selectedExercise);
            selectedExercise -= 1;
            mStableArrayAdapter.notifyDataSetChanged();

        }
    }

    private void editExercise() {
        if (selectedExercise == -1)  return;
        Exercises exercise = mListView.getDataList().get(selectedExercise);
        Intent intent = new Intent(this, EditExerciseInfo.class);
        intent.putExtra(EXTRA_WORKOUT_ID, exercise.programID);
        intent.putExtra(EXTRA_EXERCISE_ID, exercise.rowID + "");
        intent.putExtra(EXTRA_EXERCISE_NAME, exercise.desc);
        intent.putExtra(EXTRA_EDIT_TYPE, "EDIT");
        startActivityForResult(intent, 1);
    }

    private void viewExercise() {
        if (selectedExercise == -1)  return;

        // check if timer, if not load edit but block edit action
        Exercises exercise = mListView.getDataList().get(selectedExercise);
        //Log.d("exercise id", "" + exercise.rowID);
        if (exercise.useTimer) {
            Intent intent = new Intent(this, TimerViewer.class);
            intent.putExtra(EXTRA_WORKOUT_ID, exercise.programID);
            intent.putExtra(EXTRA_WORKOUT_NAME, workoutName);
            intent.putExtra(EXTRA_EXERCISE_ID, exercise.rowID + "");
            intent.putExtra(EXTRA_EXERCISE_NAME, exercise.desc);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this, EditExerciseInfo.class);
            intent.putExtra(EXTRA_WORKOUT_ID, exercise.programID);
            intent.putExtra(EXTRA_EXERCISE_ID, exercise.rowID + "");
            intent.putExtra(EXTRA_EXERCISE_NAME, exercise.desc);
            intent.putExtra(EXTRA_EDIT_TYPE, "VIEW");
            startActivityForResult(intent, 1);
        }
    }

    private void markExercise() {
        if (selectedExercise != -1) {
            Exercises exercise = mListView.getDataList().get(selectedExercise);
            String checked = "0";
            if (exercise.checked) {
                exercise.checked = false;
                checked = "0";
            } else {
                exercise.checked = true;
                checked = "1";
            }
            String query = "UPDATE Exercises SET checked=\"" + checked + "\" WHERE id=\"" + exercise.rowID + "\"";
            if (!mSQLHandler.executeQuery(query)) {
                exercise.checked = !exercise.checked;
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Marking Exercise Failed!")
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
                if (exercise.checked) {
                    playSound(soundStrikeID);
                } else  {
                    playSound(soundEraseID);
                }
            }


            mListView.updateDataItem(exercise, selectedExercise);
            mStableArrayAdapter.notifyDataSetChanged();
        }
    }

    private void resetAll() {

        String query = "UPDATE Exercises SET checked=\"" + 0 + "\" WHERE programID=\"" + workoutID + "\"";
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Updating Exercises Failed!")
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
            for (int i = 0; i < mDataList.size(); i++) {
                Exercises exercise = mDataList.get(i);
                if (exercise.checked) {
                    exercise.checked = false;
                    mListView.updateDataItem(exercise, i);
                }
            }
            mStableArrayAdapter.notifyDataSetChanged();
            playSound(soundEraseID);
        }
    }

    private void resetTimers() {

        // use put since record always exists
        String query = "UPDATE Exercises SET 'timeRemaining'=\"" + "\", 'timerStatus'=\"" +
                "\", setsCompleted=\"" + 0 + "\", endTime=\"" + "\" WHERE programID=\"" + workoutID + "\"";
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Could not reset timer")
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
        }

    }

    private void playSound(int ID) {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume/maxVolume;
        float rightVolume = curVolume/maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        soundPool.play(ID, leftVolume, rightVolume, priority, no_loop, normal_playback_rate);
    }
}
