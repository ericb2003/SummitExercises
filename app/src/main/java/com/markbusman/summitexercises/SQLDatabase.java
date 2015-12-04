package com.markbusman.summitexercises;

/**
 * Created by markbusman on 9/19/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Summit_Workouts";


    public static final String DATABASE_TABLE1 = "Program";
    public static final String PROGRAM_COLUMN1 = "name";
    public static final String PROGRAM_COLUMN2 = "order";
    public static final String PROGRAM_COLUMN3 = "timeStamp";
    public static final String PROGRAM_COLUMN4 = "programID";
    public static final String PROGRAM_COLUMN5 = "lastModified";
    public static final String PROGRAM_COLUMN6 = "hashTag";

    public static final String DATABASE_TABLE2 = "Exercises";
    public static final String EXERCISES_COLUMN1 = "desc";
    public static final String EXERCISES_COLUMN2 = "equipment";
    public static final String EXERCISES_COLUMN3 = "instructions";
    public static final String EXERCISES_COLUMN4 = "name";
    public static final String EXERCISES_COLUMN5 = "reps";
    public static final String EXERCISES_COLUMN6 = "sets";
    public static final String EXERCISES_COLUMN7 = "order";
    public static final String EXERCISES_COLUMN8 = "time";
    public static final String EXERCISES_COLUMN9 = "timeStamp";
    public static final String EXERCISES_COLUMN10 = "lastModified";
    public static final String EXERCISES_COLUMN11 = "useTimer";
    public static final String EXERCISES_COLUMN12 = "weight";
    public static final String EXERCISES_COLUMN13 = "programID";
    public static final String EXERCISES_COLUMN14 = "hashTag";
    public static final String EXERCISES_COLUMN15 = "checked";
    public static final String EXERCISES_COLUMN16 = "timeRemaining";
    public static final String EXERCISES_COLUMN17 = "timerStatus";
    public static final String EXERCISES_COLUMN18 = "setsCompleted";
    public static final String EXERCISES_COLUMN19 = "endTime";


    private static final String SCRIPT_CREATE_DATABASE_PROGRAM = "create table "
            + DATABASE_TABLE1 + " ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT, '" + PROGRAM_COLUMN1
            + "' text, '" + PROGRAM_COLUMN2
            + "' text, '" + PROGRAM_COLUMN3
            + "' text, '" + PROGRAM_COLUMN4
            + "' text, '" + PROGRAM_COLUMN5
            + "' text, '" + PROGRAM_COLUMN6
            + "' text);";
    private static final String SCRIPT_CREATE_DATABASE_EXERCISES = "create table " + DATABASE_TABLE2 + " ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT, '" + EXERCISES_COLUMN1
            + "' text, '" + EXERCISES_COLUMN2
            + "' text, '" + EXERCISES_COLUMN3
            + "' text, '" + EXERCISES_COLUMN4
            + "' text, '" + EXERCISES_COLUMN5
            + "' text, '" + EXERCISES_COLUMN6
            + "' text, '" + EXERCISES_COLUMN7
            + "' text, '" + EXERCISES_COLUMN8
            + "' text, '" + EXERCISES_COLUMN9
            + "' text, '" + EXERCISES_COLUMN10
            + "' text, '" + EXERCISES_COLUMN11
            + "' text, '" + EXERCISES_COLUMN12
            + "' text, '" + EXERCISES_COLUMN13
            + "' text, '" + EXERCISES_COLUMN14
            + "' text, '" + EXERCISES_COLUMN15
            + "' text, '" + EXERCISES_COLUMN16
            + "' text, '" + EXERCISES_COLUMN17
            + "' text, '" + EXERCISES_COLUMN18
            + "' text, '" + EXERCISES_COLUMN19
            + "' text);";



    private Context context;

    public SQLDatabase(Context context, String name, CursorFactory factory,
                       int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public SQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_DATABASE_PROGRAM);
        db.execSQL(SCRIPT_CREATE_DATABASE_EXERCISES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);

        onCreate(db);
    }

}