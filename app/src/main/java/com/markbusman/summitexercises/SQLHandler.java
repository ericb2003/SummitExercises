package com.markbusman.summitexercises;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by markbusman on 9/19/15.
 */
public class SQLHandler {

    public static final String DATABASE_NAME = "Summit_Workouts";
    public static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase sqlDatabase;
    SQLDatabase dbHelper;

    public SQLHandler(Context context) {

        dbHelper = new SQLDatabase(context, DATABASE_NAME, null,
                DATABASE_VERSION);
        sqlDatabase = dbHelper.getWritableDatabase();
    }

    public boolean executeQuery(String query) {
        try {

            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }

            sqlDatabase = dbHelper.getWritableDatabase();
            sqlDatabase.execSQL(query);
            return true;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR " + e);
            return false;
        }

    }

    public Cursor selectQuery(String query) {
        Cursor c1 = null;
        try {

            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();

            }
            sqlDatabase = dbHelper.getWritableDatabase();
            c1 = sqlDatabase.rawQuery(query, null);

        } catch (Exception e) {

            System.out.println("DATABASE ERROR " + e);

        }
        return c1;

    }

    public void closeDatabase() {
        if (sqlDatabase.isOpen()) {
            sqlDatabase.close();
        }
    }

    public long nextId(String tableName) {
        long rowId = -1;

        sqlDatabase = dbHelper.getWritableDatabase();


        sqlDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", "");


            // insert a valid row into your table
            rowId = sqlDatabase.insert(tableName, null, values);

            // NOTE: we don't call  db.setTransactionSuccessful()
            // so as to rollback and cancel the last changes

        } finally {
            sqlDatabase.endTransaction();
        }

        return rowId;
    }


    /*
    public List<RecordData> getAllMembers(String search, String sort) {
        List<RecordData> memberList = new ArrayList<RecordData>();
        // Select All Query
        String selectQuery = "SELECT members.ID, members.\"Customer Name\", members.\"Specialization\", members.\"image\", " +
                "clinics.Institution, clinics.City, clinics.Province FROM clinics " +
                "INNER JOIN members ON clinics.\"Customer ID\" = members.\"Customer ID\" ";

        if (search != null) {
            selectQuery += "WHERE members.\"customer Name\" LIKE '%" + search + "%' OR " +
                    "members.\"Specialization\" LIKE '%" + search + "%' OR " +
                    "members.\"Current Position\" LIKE '%" + search + "%' OR " +
                    "members.\"Training\" LIKE '%" + search + "%' OR " +
                    "members.\"PRC Number\" LIKE '%" + search + "%' OR " +
                    "members.\"PMA Number\" LIKE '%" + search + "%' OR " +
                    "clinics.\"Address\" LIKE '%" + search + "%' OR " +
                    "clinics.\"Schedule\" LIKE '%" + search + "%' OR " +
                    "clinics.\"Institution\" LIKE '%" + search + "%' OR " +
                    "clinics.\"City\" LIKE '%" + search + "%' OR " +
                    "clinics.\"Province\" LIKE '%" + search + "%' OR " +
                    "clinics.\"Phone Number\" LIKE '%" + search + "%' ";

            SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
            if (settings.contains("loggedin")) {
                boolean loginstat = settings.getBoolean("loggedin", false);
                if (loginstat) {
                    selectQuery += "OR members.\"Mobile Number\" LIKE '%" + search + "%' OR " +
                            "members.\"Email\" LIKE '%" + search + "%' OR " +
                            "members.\"Fax\" LIKE '%" + search + "%' OR " +
                            "members.\"Home Address\" LIKE '%" + search + "%' OR " +
                            "members.\"Home Phone No.\" LIKE '%" + search + "%' ";
                }
            }
        }

        if (sort != null) {
            selectQuery += "ORDER BY \"" + sort + "\"";
        } else {
            selectQuery += 	"GROUP BY \"Customer Name\" " +
                    "ORDER BY \"Customer Name\"";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RecordData member = new RecordData();
                member.setID(Integer.parseInt(cursor.getString(0)));
                member.setCustomerName(cursor.getString(1));
                member.setSpecialization(cursor.getString(2));
                member.setImage(cursor.getString(3));
                member.setInstitution(cursor.getString(4));
                member.setCity(cursor.getString(5));
                member.setProvince(cursor.getString(6));
                // Adding member to list
                memberList.add(member);
            } while (cursor.moveToNext());
        }

        // return member list
        return memberList;
    }
    */

}