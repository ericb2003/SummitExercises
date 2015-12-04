/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markbusman.summitexercises;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<Exercises> {

    final int INVALID_ID = -1;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    int mRowLayout;
    List<Exercises> mExercises;

    public StableArrayAdapter(Context context, int textViewResourceId, List<Exercises> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            Exercises exercise = objects.get(i);
            String uniqueID = exercise.rowID + "";//exercise.programID + exercise.timeStamp.getTime() + "";
            mIdMap.put(uniqueID, i);

            mExercises = objects;
            mRowLayout = textViewResourceId;


        }
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }

        try {
            Exercises exercise = getItem(position);
            String uniqueID = exercise.rowID + "";//exercise.programID + exercise.timeStamp.getTime() + "";
            return mIdMap.get(uniqueID);
        } catch (Exception e) {
            return INVALID_ID;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(mRowLayout, null);

        TextView mTextView = (TextView) convertView.findViewById(R.id.exerciseTextView);
        Exercises exercise = mExercises.get(position);
        String text = exercise.desc + ": ";

        if (exercise.weight.trim().length() > 0) {
            text += exercise.weight;
        }

        if (exercise.reps.trim().length() > 0 && exercise.weight.trim().length() > 0) {
            text += " x " + exercise.reps;
        } else if (exercise.reps.trim().length() > 0) {
            text += exercise.reps;
        }

        if (exercise.sets.trim().length() > 0 && (exercise.reps.trim().length() > 0 || exercise.weight.trim().length() > 0)) {
            text += " x " + exercise.sets;
        } else {
            text += exercise.sets;
        }

        if (exercise.useTimer) {
            text += "\n" + exercise.time;
        }
        mTextView.setText(text);

        ImageView mImageView = (ImageView) convertView.findViewById(R.id.exerciseCheckmark);
        if (mExercises.get(position).checked) {
            mImageView.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.INVISIBLE);


        }



        return convertView;

        //TextView tvAddress = (TextView)convertView.findViewById(R.id.item_adressView);
        //TextView tvZipCode = (TextView)convertView.findViewById(R.id.item_zipcodePlaceView);
        //TextView tvCompany = (TextView)convertView.findViewById(R.id.item_companyView);
        //TextView tvDriveType = (TextView)convertView.findViewById(R.id.item_driveTypeView);

        //tvAddress.setText(mStrings.get(position)[0]);
        //tvZipCode.setText(mStrings.get(position)[1]);
        //tvCompany.setText(mStrings.get(position)[2]);
        //tvDriveType.setText(mStrings.get(position)[3]);

        //return convertView;
    }

}
