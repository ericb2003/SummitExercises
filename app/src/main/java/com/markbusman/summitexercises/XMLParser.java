package com.markbusman.summitexercises;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by markbusman on 29/10/2015.
 */
public class XMLParser  {
    private XmlPullParserFactory xmlFactoryObject;
    InputStream stream;
    public volatile boolean parsingComplete = true;

    private String programTimeStamp = "";
    private String programLastModified = "";
    private String programHashTag = "";
    private String programName = "";
    private String programID = "";
    private Exercises exercise;
    private SQLHandler mSQLHandler;
    private Context context;

    public XMLParser(InputStream is, Context context) throws IOException {
        this.context = context;
        mSQLHandler = new SQLHandler(context);

        //String xml = convertStreamToString(is);
        //Log.d("stream", xml);
        //if (isValidFile(is)) {
            try {
                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();
                myparser.setInput(is, null);
                parseXMLAndStoreIt(myparser);

            } catch (XmlPullParserException e) {

                new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("Importing File Failed\nThere is something wrong with the file")
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


                e.printStackTrace();
            }
        //} else {
        /*    new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Importing File Failed\nFile is not compatible...")
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
        } */
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private Boolean isValidFile(InputStream is) {
        String xmlText = convertStreamToString(is);
        if (xmlText.contains("<!--com.markbusman.My-Workout-Program-->")) {
            return true;
        }
        return false;
    }


    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("exercise")){
                            exercise = new Exercises();
                        }
                        if(name.equals("programName")) {
                            programTimeStamp = myParser.getAttributeValue(null, "timeStamp");
                            programLastModified = myParser.getAttributeValue(null, "lastModified");
                            programHashTag = myParser.getAttributeValue(null, "hashTag");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("programName")){

                            programName = text;
                            programID = new Date().getTime() + "";
                            processWorkout();
                        }

                        else if(name.equals("program")){

                        }

                        else if(name.equals("exercise")){
                            processExercise();
                        }

                        else if(name.equals("desc")){
                            exercise.desc = text.trim();
                        }

                        else if(name.equals("equipment")){
                            exercise.equipment = text.trim();
                        }

                        else if(name.equals("instructions")){
                            exercise.instructions = text.trim();
                        }

                        else if(name.equals("name")){
                            exercise.name = text.trim();
                        }

                        else if(name.equals("reps")){
                            exercise.reps = text.trim();
                        }

                        else if(name.equals("sets")){
                            exercise.sets = text.trim();
                        }

                        else if(name.equals("time")){
                            exercise.time = text.trim();
                            Log.d("time parsed", text.trim());
                        }

                        else if(name.equals("weight")){
                            exercise.weight = text.trim();
                        }

                        else if(name.equals("order")){
                            exercise.order = Integer.parseInt(text.trim());
                        }

                        else if(name.equals("useTimer")){
                            exercise.useTimer = false;
                            if (text.trim().equals("1")) {
                                exercise.useTimer = true;
                            }
                        }

                        else if(name.equals("timeStamp")){
                            //Log.d("date stamp", "rrr" +  Double.parseDouble(text) + "rrr");
                            exercise.timeStamp = new Date((long) Double.parseDouble(text.trim()));
                        }

                        else if(name.equals("lastModified")){
                            exercise.lastModified = new Date((long) Double.parseDouble(text.trim()));
                        }

                        else if(name.equals("hashTag")){
                            exercise.hashTag = text.trim();
                        }

                        else{
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processWorkout() {
        // create new workout

        long timestamp = new Date((long) Double.parseDouble(programTimeStamp)).getTime();
        long lastmod = new Date((long) Double.parseDouble(programTimeStamp)).getTime();

        String query = "INSERT INTO Program ('name', 'order', 'timeStamp', 'programID', 'lastModified', 'hashTag') VALUES (\"" +
                programName + "\", \"" + 0 + "\", " + timestamp + ", \"" + programID + "\", " +
               lastmod+ ", \"" + programHashTag + "\");";
        //Log.d("process Workout:" , query);
        if (mSQLHandler.executeQuery(query)) {
            // Adding program to list

        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Workout could not be saved!")
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

    private void processExercise() {
        String useTimer = "0";
        if (exercise.useTimer) useTimer = "1";
        String query = "INSERT INTO Exercises ('desc', 'name', 'weight', 'reps', 'sets', 'time', 'useTimer', 'equipment', 'instructions', "
                + "'order', 'timeStamp', 'lastModified', 'programID', 'hashTag', 'checked') VALUES ("
                + "'" + exercise.desc + "'" +  ","
                + "'" + exercise.name + "'" + ","
                + "'" + exercise.weight + "'" +  ","
                + "'" + exercise.reps + "'" +  ","
                + "'" + exercise.sets + "'" +  ","
                + "'" + exercise.time + "'" +  ","
                + "'" + useTimer + "'" +  ","
                + "'" + exercise.equipment + "'" +  ","
                + "'" + exercise.instructions + "'" +  ","
                + "'" + exercise.order + "'" +  ","
                + "'" + exercise.timeStamp.getTime() + "'" +  ","
                + "'" + exercise.lastModified.getTime() + "'" +  ","
                + "'" + programID + "'" +  ","
                + "'" + exercise.hashTag + "'" +  ","
                + "'" + 0 + "'"
                + ")";

        if (mSQLHandler.executeQuery(query)) {
            // Adding program to list
        } else {
            new AlertDialog.Builder(context)
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
        }
    }

}
