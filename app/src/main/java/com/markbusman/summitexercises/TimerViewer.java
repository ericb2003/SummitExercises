package com.markbusman.summitexercises;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.GregorianCalendar;
import java.util.HashMap;

public class TimerViewer extends AppCompatActivity {

    RoseSurfaceView surfaceView;
    boolean timerRunning = false;
    MoreAccurateTimer countdownTimer;
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    int soundBeepID = 1;
    int soundBuzzID = 2;
    double totalSecs = 0;//30000 / 1000;
    int hours = 0;
    int mins = 0;
    int secs = 0;
    int totalSets = 0;
    int numOfSets = 0;
    int setsCompleted = 0;
    double remainingSecs = -1;
    double endTime = -1;
    String exerciseName;
    private String workoutID;
    private String exerciseID;
    private String alarmSound = "airhorn";
    private Boolean useCountdown = true;
    private SharedPreferences sharedPref;
    private SQLHandler mSQLHandler;
    private String workoutName;


    public final static String EXTRA_EXERCISE_NAME = "com.markbusman.summitexercises.EXERCISE_NAME";
    public final static String EXTRA_WORKOUT_ID = "com.markbusman.summitexercises.WORKOUT_ID";
    public final static String EXTRA_WORKOUT_NAME = "com.markbusman.summitexercises.WORKOUT_NAME";
    public final static String EXTRA_EXERCISE_ID = "com.markbusman.summitexercises.EXERCISE_ID";
    public final static String EXTRA_EDIT_TYPE = "com.markbusman.summitexercises.EXERCISE_EDIT_TYPE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_viewer);

        mSQLHandler = new SQLHandler(this);

        Intent intent = getIntent();
        workoutID = intent.getStringExtra(EXTRA_WORKOUT_ID);
        workoutName = intent.getStringExtra( EXTRA_WORKOUT_NAME);
        exerciseID = intent.getStringExtra(EXTRA_EXERCISE_ID);
        exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME);
        this.setTitle(exerciseName);

        final ImageButton startButton = (ImageButton) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startPressed();
            }
        });

        final ImageButton resetButton = (ImageButton) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetPressed();
            }
        });

        final Button infoButton = (Button) findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPressed();
            }
        });

        final Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsPressed();
            }
        });

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);

        try {
            useCountdown = sharedPref.getBoolean(getString(R.string.useCountdownKey), false);
        } catch (ClassCastException e) {
            //Log.d("cast error", "use countdown");
        }

        try {
            alarmSound = sharedPref.getString(getString(R.string.alarmFileKey), "airhorn");
        } catch (ClassCastException e) {

        }

        loadData();

        /*final TextView total = (TextView) findViewById(R.id.initialTimeText);
        total.setText(getTimeString(totalSecs));

        final TextView setsText = (TextView) findViewById(R.id.editText3);
        if (numOfSets > 0) {
            setsText.setText(setsCompleted + " of " + numOfSets);
        } else {
            setsText.setText("No Sets");
        }

        final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
        countdownText.setText(getTimeString(0));*/

        restartTimer();

        soundPool = new SoundPool(4, AudioManager.STREAM_ALARM, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(soundBeepID, soundPool.load(this, R.raw.beep, 1));

        if (alarmSound.equals("airhorn")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.airhorn, 1));
        } else if (alarmSound.equals("applause")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.applause, 1));
        } else if (alarmSound.equals("doorbell")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.doorbell, 1));
        } else if (alarmSound.equals("helicopter")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.helicopter, 1));
        } else if (alarmSound.equals("mountainlion")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.mountainlion, 1));
        } else if (alarmSound.equals("tornadosiren")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.tornadosiren, 1));
        }

        surfaceView = (RoseSurfaceView) findViewById(R.id.surfaceView);
        //surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        //surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_viewer, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // save state of timer
            countdownTimer.cancel();
            saveState();
            //Log.d("timer", "stop");
        } catch (Exception e) {

        }
        // save state of timer
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);

        try {
            useCountdown = sharedPref.getBoolean(getString(R.string.useCountdownKey), false);
        } catch (ClassCastException e) {
            //Log.d("cast error", "use countdown");
        }

        try {
            alarmSound = sharedPref.getString(getString(R.string.alarmFileKey), "airhorn");
        } catch (ClassCastException e) {

        }

        // request sounds
        try {
            useCountdown = sharedPref.getBoolean(getString(R.string.useCountdownKey), false);
        } catch (ClassCastException e) {
            //Log.d("cast error", "use countdown");
        }

        try {
            alarmSound = sharedPref.getString(getString(R.string.alarmFileKey), "airhorn");
        } catch (ClassCastException e) {

        }

        soundPool = new SoundPool(4, AudioManager.STREAM_ALARM, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(soundBeepID, soundPool.load(this, R.raw.beep, 1));

        if (alarmSound.equals("airhorn")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.airhorn, 1));
        } else if (alarmSound.equals("applause")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.applause, 1));
        } else if (alarmSound.equals("doorbell")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.doorbell, 1));
        } else if (alarmSound.equals("helicopter")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.helicopter, 1));
        } else if (alarmSound.equals("mountainlion")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.mountainlion, 1));
        } else if (alarmSound.equals("tornadosiren")) {
            soundPoolMap.put(soundBuzzID, soundPool.load(this, R.raw.tornadosiren, 1));
        }

        loadState();

        // restart timer if it was running
       restartTimer();
    }

    private void loadData() {
        // set totalSecs and numOfSets if appropriate
        String query = "SELECT * FROM Exercises WHERE id=\"" + exerciseID + "\"";

        Cursor cursor = mSQLHandler.selectQuery(query);
        Exercises exercise = new Exercises();
        if (cursor.moveToFirst()) {
            do {
                try {
                    String str = cursor.getString(16);
                    if (str != null) {
                        remainingSecs = Double.parseDouble(str);
                    }
                } catch (NumberFormatException e) {
                    remainingSecs = -1;
                }
                try {
                    String str = cursor.getString(19);
                    if (str != null) {
                        endTime = Double.parseDouble(str);
                    }
                } catch (NumberFormatException e) {
                    endTime = -1;
                }
                try {
                    timerRunning = Boolean.parseBoolean(cursor.getString(17));
                } catch (NumberFormatException e) {
                    timerRunning = false;
                }
                try {
                    setsCompleted = Integer.parseInt(cursor.getString(18));
                } catch (NumberFormatException e) {
                    setsCompleted = 0;
                }
                //Log.d("remaining time", remainingSecs + "");

                if (remainingSecs == 0 && !timerRunning) {
                    remainingSecs = -1;
                }

                exercise.sets = cursor.getString(6);
                exercise.time = cursor.getString(8);
                if (!exercise.sets.equals("")) {
                    numOfSets = Integer.parseInt(exercise.sets);
                } else {
                    numOfSets = -1;
                }
                String[] parts = exercise.time.split(":");
                try {
                    for (int i = 0; i < 3; ++i) {
                        int time = Integer.parseInt(parts[i].trim());
                        if (i == 0) {
                            hours = time;
                        } else if (i == 1) {
                            mins = time;
                        } else if (i == 2) {
                            secs = time;
                        }
                    }
                } catch (Exception e) {
                    //Log.d("err parsing numbers", hours + ":" + mins + ":" + secs);
                }
            } while (cursor.moveToNext());
        }

        // compute total secs
        totalSecs = hours * 60 * 60 + mins * 60 + secs;

        //Log.d("total secs", totalSecs + "");

    }

    private void saveState() {
        // use put since record always exists
        String query = "UPDATE Exercises SET 'timeRemaining'=\"" + remainingSecs + "\", 'timerStatus'=\"" + timerRunning +
                "\", setsCompleted=\"" + setsCompleted + "\", endTime=\"" + endTime + "\" WHERE id=" + exerciseID;
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Timer data could not be saved!")
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

    private void loadState() {
        String query = "SELECT * FROM Exercises WHERE id=\"" + exerciseID + "\"";

        Cursor cursor = mSQLHandler.selectQuery(query);
        Exercises exercise = new Exercises();
        if (cursor.moveToFirst()) {
            do {
                try {
                    String str = cursor.getString(16);
                    if (str != null) {
                        remainingSecs = Double.parseDouble(str);
                    }
                } catch (NumberFormatException e) {
                    remainingSecs = -1;
                }
                try {
                    String str = cursor.getString(19);
                    if (str != null) {
                        endTime = Double.parseDouble(str);
                    }
                } catch (NumberFormatException e) {
                    endTime = -1;
                }
                try {
                    timerRunning = Boolean.parseBoolean(cursor.getString(17));
                } catch (NumberFormatException e) {
                    timerRunning = false;
                }
                try {
                    setsCompleted = Integer.parseInt(cursor.getString(18));
                } catch (NumberFormatException e) {
                    setsCompleted = 0;
                }

                exercise.sets = cursor.getString(6);
                exercise.time = cursor.getString(8);
                //Log.d("total time", exercise.time);
                if (!exercise.sets.equals("")) {
                    numOfSets = Integer.parseInt(exercise.sets);
                } else {
                    numOfSets = -1;
                }
                String[] parts = exercise.time.split(":");
                try {
                    for (int i = 0; i < 3; ++i) {
                        int time = Integer.parseInt(parts[i]);
                        if (i == 0) {
                            hours = time;
                        } else if (i == 1) {
                            mins = time;
                        } else if (i == 2) {
                            secs = time;
                        }
                    }
                } catch (Exception e) {

                }
            } while (cursor.moveToNext());
        }

        //Log.d("time", remainingSecs + "");
        //Log.d("timer running", timerRunning + "");

        // compute total secs
        totalSecs = hours * 60 * 60 + mins * 60 + secs;
    }

    private void restartTimer() {
        final ImageButton startButton = (ImageButton) findViewById(R.id.startButton);
        if (timerRunning == true && endTime > -1) {
            try {
                countdownTimer.cancel();
            } catch (Exception e) {

            }
            startButton.setImageResource(R.mipmap.pause);

            // compute elasped time using endTime
            double startTime = endTime - new GregorianCalendar().getTimeInMillis();
            //Log.d("resume", "" + startTime / 1000);

            if (startTime > 0) {
                final TextView total = (TextView) findViewById(R.id.initialTimeText);
                total.setText(getTimeString(totalSecs));

                final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
                countdownText.setText(getTimeString((startTime - 1000) / 1000));

                final TextView setsText = (TextView) findViewById(R.id.editText3);
                setsText.setText("x " + setsCompleted);


                cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
                scheduleNotification(getNotification("Timer for " + exerciseName + " completed."), startTime);

                countdownTimer = new MoreAccurateTimer((long) startTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        TextView countdownText = (TextView) findViewById(R.id.coutDownText);

                        double deg = 360 / totalSecs;
                        remainingSecs = millisUntilFinished / 1000;
                        deg = (totalSecs - remainingSecs) * deg;

                        countdownText.setText(getTimeString(remainingSecs));
                        surfaceView.degreeToDraw = deg;
                        surfaceView.invalidate();

                        // if less than 4 secs left play sound
                        if (remainingSecs < 4 && remainingSecs > 0 && useCountdown) {
                            playSound(soundBeepID);
                        }

                        if (remainingSecs == 1) {
                            cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
                        }

                        if (remainingSecs == 0) {
                            //cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
                            playSound(soundBuzzID);
                            remainingSecs = -1;

                            countdownText = (TextView) findViewById(R.id.coutDownText);
                            countdownText.setText("00 : 00 : 00");

                            surfaceView.degreeToDraw = 360;
                            surfaceView.invalidate();

                            startButton.setImageResource(R.mipmap.play);
                        }

                        //Log.d("secs left", "" + remainingSecs);
                    }


                    public void onFinish() {
                        // play sound, blink text
                        //playSound(soundBuzzID);

                        //final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
                        //countdownText.setText("00 : 00 : 00");

                        //surfaceView.degreeToDraw = 360;
                        //surfaceView.invalidate();

                        timerRunning = false;

                        //startButton.setImageResource(R.mipmap.play);
                    }
                }.start();
            } else {
                timerRunning = false;
                remainingSecs = -1;
                startButton.setImageResource(R.mipmap.play);
                final TextView total = (TextView) findViewById(R.id.initialTimeText);
                total.setText(getTimeString(totalSecs));

                final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
                countdownText.setText(getTimeString(0));

                final TextView setsText = (TextView) findViewById(R.id.editText3);
                setsText.setText("x " + setsCompleted);

            }
        } else {
            timerRunning = false;
            startButton.setImageResource(R.mipmap.play);
            double startTime = 0;
            if (remainingSecs != -1) {
                startTime = remainingSecs * 1000 + 1000;
            }

            final TextView total = (TextView) findViewById(R.id.initialTimeText);
            total.setText(getTimeString(totalSecs));

            final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
            if (startTime > 0) {
                countdownText.setText(getTimeString((startTime - 1000) / 1000));
            } else {
                countdownText.setText(getTimeString(0));
            }
            final TextView setsText = (TextView) findViewById(R.id.editText3);
            setsText.setText("x " + setsCompleted);
        }

    }


    private void startPressed() {
        final ImageButton startButton = (ImageButton) findViewById(R.id.startButton);
        if (timerRunning == false) {
            startButton.setImageResource(R.mipmap.pause);
            timerRunning = true;

            double startTime = totalSecs * 1000 + 1000;
            if (remainingSecs != -1) {
                startTime = remainingSecs * 1000 + 1000;
            } else {
                setsCompleted++;
            }

            endTime = new GregorianCalendar().getTimeInMillis() + startTime;

            final TextView total = (TextView) findViewById(R.id.initialTimeText);
            total.setText(getTimeString(totalSecs));

            final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
            countdownText.setText(getTimeString((startTime - 1000) / 1000));

            final TextView setsText = (TextView) findViewById(R.id.editText3);
            setsText.setText("x " + setsCompleted);

            scheduleNotification(getNotification("Timer for " + exerciseName + " completed."), startTime);

            countdownTimer = new MoreAccurateTimer((long) startTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextView countdownText = (TextView) findViewById(R.id.coutDownText);

                    double deg = 360 / totalSecs;
                    remainingSecs = millisUntilFinished / 1000;
                    deg = (totalSecs - remainingSecs) * deg;

                    countdownText.setText(getTimeString(remainingSecs));
                    surfaceView.degreeToDraw = deg;
                    surfaceView.invalidate();

                    // if less than 4 secs left play sound
                    if (remainingSecs < 4 &&  remainingSecs > 0 && useCountdown) {
                        playSound(soundBeepID);
                    }

                    if (remainingSecs == 1) {
                        cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
                    }

                    if (remainingSecs == 0) {
                        //cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
                        playSound(soundBuzzID);
                        remainingSecs = -1;

                        countdownText = (TextView) findViewById(R.id.coutDownText);
                        countdownText.setText("00 : 00 : 00");

                        surfaceView.degreeToDraw = 360;
                        surfaceView.invalidate();

                        startButton.setImageResource(R.mipmap.play);
                    }

                    //Log.d("secs left", "" + remainingSecs);
                }



                public void onFinish() {
                    // play sound, blink text
                    //playSound(soundBuzzID);

                    //final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
                    //countdownText.setText("00 : 00 : 00");

                    //surfaceView.degreeToDraw = 360;
                    //surfaceView.invalidate();

                    timerRunning = false;

                    //startButton.setImageResource(R.mipmap.play);
                }
            }.start();

        } else {
            startButton.setImageResource(R.mipmap.play);
            timerRunning = false;
            countdownTimer.cancel();
            cancelNotification(getNotification("Timer for " + exerciseName + " completed."));

        }

    }

    private void resetPressed() {
        cancelNotification(getNotification("Timer for " + exerciseName + " completed."));
        remainingSecs = -1;
        final ImageButton startButton = (ImageButton) findViewById(R.id.startButton);
        timerRunning = false;
        startButton.setImageResource(R.mipmap.play);

        try {
            countdownTimer.cancel();
        } catch (Exception e) {
            //Log.d("timer", "cancelled");
        }

        final TextView countdownText = (TextView) findViewById(R.id.coutDownText);
        countdownText.setText(getTimeString(0));
        surfaceView.degreeToDraw = 0;
        surfaceView.invalidate();
        setsCompleted = 0;
        final TextView setsText = (TextView) findViewById(R.id.editText3);
        setsText.setText("x " + setsCompleted);

        saveState();
    }

    private void infoPressed() {
        Intent intent = new Intent(this, EditExerciseInfo.class);
        intent.putExtra(EXTRA_WORKOUT_ID, workoutID);
        intent.putExtra(EXTRA_EXERCISE_ID, exerciseID);
        intent.putExtra(EXTRA_EXERCISE_NAME, exerciseName);
        intent.putExtra(EXTRA_EDIT_TYPE, "VIEW");
        startActivityForResult(intent, 1);
    }

    private void settingsPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 2);
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

    private String getTimeString(double totalSeconds) {
        int hours = (int) Math.floor(totalSeconds / 60 / 60);
        double rem = (int) (totalSeconds - (hours * 60 * 60));
        int minutes = (int) Math.floor(rem / 60);
        int seconds = (int) totalSeconds % 60;

        //Log.d("hours", "" + hours);
        //Log.d("mins", "" + minutes);
        //Log.d("secs", "" + seconds);

        String time = "";
        if (hours < 10) {
            time = "0" + hours;
        } else {
            time = "" + hours;
        }
        time = time + " : ";
        if (minutes < 10) {
            time = time + "0" + minutes;
        } else {
            time = time + minutes;
        }
        time = time + " : ";
        if (seconds < 10) {
            time = time + "0" + seconds;
        } else {
            time = time + seconds;
        }
        return time;
    }

    private void scheduleNotification(Notification notification, double delay) {

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        int id = Integer.parseInt(exerciseID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra(EXTRA_WORKOUT_ID, workoutID);
        notificationIntent.putExtra(EXTRA_WORKOUT_NAME, workoutName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = (long) (new GregorianCalendar().getTimeInMillis()+delay);//SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) futureInMillis, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP,futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Time's Up!!!");
        builder.setContentText(content);


        builder.setSmallIcon(R.mipmap.ic_notificon);
        Uri sound = null;
        if (alarmSound.equals("airhorn")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.airhorn);
        } else if (alarmSound.equals("applause")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.applause);
        } else if (alarmSound.equals("doorbell")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.doorbell);
        } else if (alarmSound.equals("helicopter")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.helicopter);
        } else if (alarmSound.equals("mountainlion")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mountainlion);
        } else if (alarmSound.equals("tornadosiren")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tornadosiren);
        }

        if (sound != null) {
            builder.setSound(sound);
        }
        return builder.build();
    }

    private void cancelNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        int id = Integer.parseInt(exerciseID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.v("TAG", "cancelling notification");
    }
}