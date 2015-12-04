package com.markbusman.summitexercises;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.database.Cursor;


public class Workouts extends AppCompatActivity {

    protected ListView mListView;
    protected EditAdapter mEditAdapter;
    protected TextAdapter mTextAdapter;

    private SQLHandler mSQLHandler;
    private String sort = null;
    private String search = null;

    protected ArrayList mArrayList = new ArrayList();

    public final static String EXTRA_WORKOUT_ID = "com.markbusman.summitexercises.WORKOUT_ID";
    public final static String EXTRA_WORKOUT_NAME = "com.markbusman.summitexercises.WORKOUT_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        mSQLHandler = new SQLHandler(this);
        loadData();

        mListView = (ListView) findViewById(R.id.WorkoutsList);
        mListView.setItemsCanFocus(true);
        mEditAdapter = new EditAdapter();
        mTextAdapter = new TextAdapter();

        mListView.setAdapter(mTextAdapter);

        Button addButton = (Button) findViewById(R.id.button_addworkout);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkout();
                mListView.invalidateViews();
            }
        });

        Button importButton = (Button) findViewById(R.id.button_importworkout);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importLists();
            }
        });

        Button editButton = (Button) findViewById(R.id.button_editworkout);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (mListView.getAdapter() == mTextAdapter) {
                    b.setText(getString(R.string.action_done));
                    mListView.setAdapter(mEditAdapter);
                } else {
                    b.setText(getString(R.string.action_editListItem));
                    mListView.setAdapter(mTextAdapter);
                }
            }
        });

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        //make sure it's an action and type we can handle
        /*if(receivedAction.equals(Intent.ACTION_SEND)){
            if(receivedType.startsWith("text/")){
                String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null) {
                    // handle the text
                    performImport(receivedText);
                }
            }
        }
        else*/ if(receivedAction.equals(Intent.ACTION_MAIN)){
            //app has been launched directly, not from share list
        } else {
            Uri uri = receivedIntent.getData();
            //Log.d("data received", uri.toString());
            performImport(uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workouts, menu);
        return true;
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
/*
        if (id == R.id.action_editListItem) {
            if (mListView.getAdapter() == mTextAdapter) {
                item.setTitle(getString(R.string.action_done));
                mListView.setAdapter(mEditAdapter);
            } else {
                item.setTitle(getString(R.string.action_editListItem));
                mListView.setAdapter(mTextAdapter);
            }

            return true;
        }

        if (id == R.id.action_addListItem) {
            addWorkout();
            mListView.invalidateViews();
            return true;
        }

        if (id == R.id.action_importList) {
            importLists();
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //Log.d("file received", uri.toString());
                performImport(uri);
            }
        }
    }

    private void loadData() {
        mArrayList = new ArrayList();
        String query = "Select * FROM Program";
        Cursor cursor = mSQLHandler.selectQuery(query);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                try {
                    Program program = new Program();
                    program.name = cursor.getString(1);
                    program.order = Integer.parseInt(cursor.getString(2));
                    program.timestamp = new Date(Long.parseLong(cursor.getString(3)));
                    program.programID = cursor.getString(4);
                    program.lastModified = new Date(Long.parseLong(cursor.getString(5)));
                    program.hashTag = cursor.getString(6);
                    program.rowID = Integer.parseInt(cursor.getString(0));

                    // Adding member to list
                    mArrayList.add(program);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
    }

    private void importLists() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        startActivityForResult(intent, 42);
    }

    private void addWorkout() {

        long rowID = mSQLHandler.nextId("Program");
        String query;
        int count =  0;

        if (rowID == -1) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Adding Workout Failed!")
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
            return;
        }

        Program program = new Program();
        program.name = "New Workout";
        program.order = 0;
        program.timestamp = new Date();
        program.programID = "" + program.timestamp.getTime();
        program.lastModified = program.timestamp;
        program.hashTag = String.valueOf(program.name.hashCode()) + String.valueOf(program.timestamp.hashCode());
        program.rowID = rowID;

        // add item to database
       query = "INSERT INTO Program ('name', 'order', 'timeStamp', 'programID', 'lastModified', 'hashTag') VALUES (\"" +
                program.name + "\", \"" + program.order + "\", " + program.timestamp.getTime() + ", \"" + program.programID + "\", " +
                program.lastModified.getTime() + ", \"" + program.hashTag + "\");";
        if (mSQLHandler.executeQuery(query)) {
            // Adding program to list
            mArrayList.add(program);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Workout added but not saved!")
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
        mListView.invalidate();
    }

    private void updateWorkout(Program program) {
        //Program program = (Program) mArrayList.get(position);

        String query = "UPDATE Program SET 'name'=\"" + program.name + "\", 'hashTag'=\"" + program.hashTag +
                "\", lastModified=" + program.lastModified.getTime() + " WHERE id=" + program.rowID;
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Updating Workout Failed!")
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
        //mEditAdapter.notifyDataSetChanged();
    }

    public void deleteWorkout(int position) {
        Program program = (Program) mArrayList.get(position);
        String query;

        query = "DELETE FROM Exercises WHERE programID=\"" + program.programID + "\"";
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Deleting Exerices from selected Workout Failed!")
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

        query = "DELETE FROM Program WHERE programID=\"" + program.programID + "\"";
        if (!mSQLHandler.executeQuery(query)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Deleting Workout Failed!")
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
        loadData();
        mEditAdapter.notifyDataSetChanged();
    }

    public void shareWorkout(int position) {
        Program program = (Program) mArrayList.get(position);
        ShareData share = new ShareData(this, program.programID);

        String text = share.convertToText();
        String htmltext = share.convertToHTML();
        Date date = new Date();
        File file = share.createXMLFile(date);
        //Log.d("share", file.getPath());

        //File filePath = getFileStreamPath(path);//getFileStreamPath("shareimage.jpg");  //optional //internal storage
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, htmltext);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));  //optional//use this when you want to send an image
        shareIntent.setType("text/*");
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share via..."));
    }

    public void loadExercises(int position) {
        Program program = (Program) mArrayList.get(position);

        Intent intent = new Intent(this, WorkoutExercises.class);
        intent.putExtra(EXTRA_WORKOUT_ID, program.programID);
        intent.putExtra(EXTRA_WORKOUT_NAME, program.name);
        startActivity(intent);

    }


    private void performImport(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            XMLParser xmlParser = new XMLParser(is, this);

            //Log.d("text received as uri:", xml);
        } catch (Exception e) {
            e.printStackTrace();
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        loadData();
        mListView.invalidate();
        mTextAdapter.notifyDataSetChanged();
        mEditAdapter.notifyDataSetChanged();
    }

    /*private void performImport(String xml) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        XMLParser xmlParser = new XMLParser(is, this);
        loadData();
        mListView.invalidate();
    }*/

    public class EditAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public EditAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifyDataSetChanged();
            mListView.setOnItemClickListener(null);
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
            EditTextViewHolder holder;
            if (convertView == null) {
                //convertView.setClickable(false);
                //convertView.setFocusable(false);
                holder = new EditTextViewHolder();
                convertView = mInflater.inflate(R.layout.editable_list_item, null);
                holder.caption = (EditText) convertView
                        .findViewById(R.id.ListItem);
                holder.caption.clearFocus();

                holder.delete = (Button) convertView.findViewById(R.id.deleteButton);
                holder.share = (Button) convertView.findViewById(R.id.shareButton);

                holder.delete.setClickable(true);
                holder.delete.setFocusable(true);
                holder.delete.setId(position);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.requestFocus();
                        deleteWorkout(v.getId());
                    }
                });

                holder.share.setClickable(true);
                holder.share.setFocusable(true);
                holder.share.setId(position);
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareWorkout(v.getId());
                        v.requestFocus();
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (EditTextViewHolder) convertView.getTag();
            }

            //Fill EditText with the value you have in data source
            Program program = (Program) mArrayList.get(position);
            holder.caption.setText(program.name);
            holder.caption.setId(position);

            //we need to update mStableArrayAdapter once we finish with editing


            holder.caption.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        try {
                            Program program = (Program) mArrayList.get(position);
                            program.name = Caption.getText().toString();
                            program.lastModified = new Date();
                            program.hashTag = String.valueOf(program.name.hashCode()) + String.valueOf(program.timestamp.hashCode());
                            updateWorkout(program);
                        } catch (Exception e) {
                            System.out.print("OOPS");
                        }
                    }
                }
            });

            return convertView;
        }
    }


    public class TextAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public TextAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifyDataSetChanged();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    loadExercises(position);
                }
            });
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
            TextViewHolder holder;
            if (convertView == null) {
                holder = new TextViewHolder();
                convertView = mInflater.inflate(R.layout.workout_list_item, null);
                holder.caption = (TextView) convertView
                        .findViewById(R.id.workouts_textview);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            //Fill EditText with the value you have in data source
            Program program = (Program) mArrayList.get(position);
            holder.caption.setText(program.name);
            holder.caption.setId(position);

            //we need to update mStableArrayAdapter once we finish with editing
            holder.caption.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final int position = v.getId();
                        final TextView Caption = (TextView) v;
                        Program program = (Program) mArrayList.get(position);
                        program.name = Caption.getText().toString();
                        //mArrayList.get(position) = Caption.getText().toString();
                    }
                }
            });

            return convertView;
        }
    }

    class EditTextViewHolder {
        EditText caption;
        Button delete;
        Button share;
    }

    class TextViewHolder {
        TextView caption;
    }

}
