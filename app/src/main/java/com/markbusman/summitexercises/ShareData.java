package com.markbusman.summitexercises;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by markbusman on 13/10/2015.
 */
public class ShareData {

    private String programID = "";
    private SQLHandler mSQLHandler;
    private Context context;
    private ArrayList<Exercises> exercises;

    private String programTimeStamp = "";
    private String programLastModified = "";
    private String programHashTag = "";
    private String programName = "";


    public ShareData(Context context, String programID) {
        this.context = context;
        this.programID = programID;

        mSQLHandler = new SQLHandler(context);
        this.exercises = new ArrayList<Exercises>();

        //Log.d("programID", programID);
        fetchData();
    }

    private void fetchData() {

        String query = "Select * FROM Program";
        Cursor cursor = mSQLHandler.selectQuery(query);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Program program = new Program();
                programName = cursor.getString(1);
                programTimeStamp = cursor.getString(3);
                programLastModified = cursor.getString(5);
                programHashTag = cursor.getString(6);

            } while (cursor.moveToNext());
        }


        query = "SELECT * FROM Exercises WHERE programID=\"" + programID + "\"";
        cursor = mSQLHandler.selectQuery(query);
        if (cursor.moveToFirst()) {
            do {
                Exercises exercise = new Exercises();
                exercise.desc = cursor.getString(1);
                exercise.equipment = cursor.getString(2);
                exercise.instructions = cursor.getString(3);
                exercise.name = "";//cursor.getString(4);
                exercise.reps = cursor.getString(5);
                exercise.sets = cursor.getString(6);
                exercise.order = Integer.parseInt(cursor.getString(7));
                exercise.time = cursor.getString(8);
                exercise.timeStamp = new Date(Long.parseLong(cursor.getString(9)));
                exercise.lastModified = new Date(Long.parseLong(cursor.getString(10)));
                int useTimer = Integer.parseInt(cursor.getString(11));
                if (useTimer != 0) {
                    exercise.useTimer = true;
                } else {
                    exercise.useTimer = false;
                }

                exercise.weight = cursor.getString(12);
                exercise.programID = cursor.getString(13);
                exercise.hashTag = cursor.getString(14);
                //Log.d("exercise", exercise.toString());
                exercises.add(exercise);
            } while (cursor.moveToNext());
        }

        Collections.sort(exercises, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                int exercise1 = ((Exercises) lhs).order;
                int exercise2 = ((Exercises) rhs).order;
                return Integer.valueOf(exercise1).compareTo(Integer.valueOf(exercise2));
                //return 0;
            }
        });

    }

    private String escapeXMLString(String str) {
        String newString = str;
        try {
            newString = newString.replace("&", "&amp;");
        } catch (NullPointerException e) {

        }

        try {
            newString = newString.replace("\"", "&quot;");
        } catch (NullPointerException e) {

        }

        try {
            newString = newString.replace("'", "&#39;");
        } catch (NullPointerException e) {

        }

        try {
            newString = newString.replace(">", "&gt;");
        } catch (NullPointerException e) {

        }

        try {

            newString = newString.replace("<", "&lt;");
        } catch (NullPointerException e) {

        }

        return newString;
    }

    public File createXMLFile(Date date) {
        //File filePath = context.getCacheDir();// + "/data" + date.getTime() + ".xml";
        File file;
        file = new File(Environment.getExternalStorageDirectory(), "SummitExercisesData" + date.getTime() + ".xml");
        String data = convertToXML();

        try {
            FileOutputStream fos = new FileOutputStream(file); //context.openFileOutput("data" + date.getTime() + ".xml", context.MODE_PRIVATE);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
            bw.write(data);
            bw.flush();
            bw.close();
            //fos.write(data.getBytes());
            //FileOutputStream fos = new FileOutputStream(file);
            //fos.flush();
            //fos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


    public String convertToXML() {
        String xmlHeader = "<!--com.markbusman.My-Workout-Program-->";
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        data += "\n" + xmlHeader;

        // collect info about program, name, timestamp, programid
        // be sure to escape the strings

        data += "\n<program>";
        data += "\n\t<programName timeStamp=\"" + programTimeStamp + "\" lastModified=\"" + programLastModified +  "\" hashTag=\"" + programHashTag + "\">";

        if (programName.equals("")) {
            data += "Unkown Program";
        } else {
            data += escapeXMLString(programName);

        }
        data += "</programName>";


        for (Exercises item : exercises) {

            int useTimer = 0;
            if (item.useTimer) useTimer = 1;

            data += "\n\t<exercise>";
            data += "\n\t\t<desc>" + escapeXMLString(item.desc) + "</desc>";
            data += "\n\t\t<equipment>" + escapeXMLString(item.equipment) + "</equipment>";
            data += "\n\t\t<instructions>" + escapeXMLString(item.instructions) + "</instructions>";
            data += "\n\t\t<name>" + escapeXMLString(item.name) + "</name>";
            data += "\n\t\t<reps>" + escapeXMLString(item.reps) + "</reps>";
            data += "\n\t\t<sets>" + escapeXMLString(item.sets) + "</sets>";
            data += "\n\t\t<time>" + escapeXMLString(item.time) + "</time>";
            data += "\n\t\t<weight>" + escapeXMLString(item.weight) + "</weight>";
            data += "\n\t\t<order>" + item.order + "</order>";
            data += "\n\t\t<useTimer>" + useTimer + "</useTimer>";
            data += "\n\t\t<timeStamp>" + item.timeStamp.getTime() + "</timeStamp>";
            data += "\n\t\t<lastModified>" + item.lastModified.getTime() + "</lastModified>";
            data += "\n\t\t<hashTag>" + item.hashTag + "</hashTag>";
            data += "\n\t</exercise>";
        }
        data += "\n</program>";

        return data;
    }

    public String convertToHTML() {
        String data = "";

        int border = 0;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
        try {
            Boolean result = sharedPref.getBoolean(context.getString(R.string.useLinesWhenPrintingKey), false);
            if (result) border = 1;
        } catch (ClassCastException e) {

        }


        if (!programName.equals("")) {
            data += "<!DOCTYPE html>\n<html>\n<head>\n\t<meta charset=\"UTF-8\">\n\t<title>" + programName + "</title>\n</head>\n\n";
        } else {
            data += "<!DOCTYPE html>\n<html>\n<head>\n\t<meta charset=\"UTF-8\">\n\t<title>Unknown Program</title>\n</head>\n\n";
        }

        data += "<body><center>\n";
        if (!programName.equals("")) {
            data += "<h2>Exercise Program: " + programName + "</h2>\n";
        } else {
            data += "<h2>Exercise Program: Unknown Program</h2>\n";
        }
        data += "<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" width=\"700\">\n";

        int counter = 0;

        for (Exercises item : exercises) {
            counter++;
            String htmlStr = item.desc;
            if (item.reps.length() > 0 || item.sets.length() > 0 || item.weight.length() > 0 || item.time.length() > 0) {
                htmlStr += ":";
            }

            if (item.weight.length() > 0) {
                htmlStr += " " + item.weight;
            }

            if (item.reps.length() > 0) {
                htmlStr += " rep: " + item.reps;
            }

            if (item.sets.length() > 0) {
                htmlStr += " x " + item.sets + " sets";
            }

            if (item.time.length() > 0) {
                htmlStr += "\t</td>\n</tr>\n<tr>\n\t<td style=\"border: 0px; border-bottom: " + border + "px solid black;\"></td>\n\t<td style=\"border: 0px; border-bottom: " + border + "px solid black\">";
                htmlStr += "Time: " + item.time;
            }

            if (item.equipment.length() > 0) {
                htmlStr += "\t</td>\n</tr>\n<tr>\n\t<td style=\"border: 0px; border-bottom: " + border + "px solid black;\"></td>\n\t<td style=\"border: 0px; border-bottom: " + border + "px solid black\">";
                htmlStr += "Equipment: " + item.equipment;
            }

            if (item.instructions.length() > 0) {
                htmlStr += "\t</td>\n</tr>\n<tr>\n\t<td style=\"border: 0px; border-bottom: " + border +"px solid black;\"></td>\n\t<td style=\"border: 0px; border-bottom: " + border + "px solid black\">";
                htmlStr += "Instructions: " + item.instructions;
            }

            data += "<tr>\n";
            data += "\t<td style=\"border: 0px; border-bottom: " + border + "px solid black;\">\n\t" + counter + ".\n\t</td>\n<td style=\"border: 0px; border-bottom: " + border + "px solid black;\">";
            data += "\t\t";
            data += htmlStr;
            data += "\t</td>";
            data += "</tr>\n";

            data += "<tr><td style=\"border: 0px; border-bottom: " + border + "px solid black;\">&nbsp;</td><td style=\"border: 0px; border-bottom: " + border + "px solid black;\">&nbsp;</td></tr>\n";
        }

        data += "</table>\n</center></body>\n</html>";


        return data;
    }

    public String convertToText() {
        String data = "";

        if (!programName.equals("")) {
            data += "Exercise Program: " + programName + "\n";
        } else {
            data += "Exercise Program: Unknown Program\n";
        }

        int counter = 0;

        for (Exercises item: exercises) {
            counter++;
            String str = "\n\n" + counter + ".\t" + item.desc;
            if (item.reps.length() > 0 || item.sets.length() > 0 || item.weight.length() > 0 || item.time.length() > 0) {
                str += ":";
            }

            if (item.weight.length() > 0) {
                str += " " + item.weight;
            }

            if (item.reps.length() > 0) {
                str += " rep: " + item.reps;
            }

            if (item.sets.length() > 0) {
                str += " x " + item.sets + " sets";
            }

            if (item.time.length() > 0) {
                str += "\n\tTime: " + item.time;
            }

            if (item.equipment.length() > 0) {
                str += "\n\tEquipment: " + item.equipment;
            }

            if (item.instructions.length() > 0) {
                str += "\n\tInstructions: " + item.instructions;
            }

            data += str;
        }

        return data;
    }
}
