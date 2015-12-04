package com.markbusman.summitexercises;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by markbusman on 22/10/2015.
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public final static String EXTRA_WORKOUT_ID = "com.markbusman.summitexercises.WORKOUT_ID";
    public final static String EXTRA_WORKOUT_NAME = "com.markbusman.summitexercises.WORKOUT_NAME";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        //String programID = intent.getStringExtra(EXTRA_WORKOUT_ID);
        //String workoutName = intent.getStringExtra(EXTRA_WORKOUT_NAME);

        notificationManager.notify(id, notification);
        Toast.makeText(context, "Exercise Completed", Toast.LENGTH_LONG).show();

        //Intent exercises = new Intent(context, WorkoutExercises.class);
        //exercises.putExtra(EXTRA_WORKOUT_ID, programID);
        //exercises.putExtra(EXTRA_WORKOUT_NAME, workoutName);
        //context.startActivity(exercises);

    }
}
