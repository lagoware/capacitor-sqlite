package com.lagoware.capacitorsqlite;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.requery.android.database.sqlite.SQLiteDatabase;

public class CapacitorSqliteDbManager {

    static HashMap<Integer,String[]> jsonObjectToUpgradesMap(JSONObject object) throws JSONException {
        HashMap<Integer,String[]> map = new HashMap<>();

        for (Iterator<String> it = object.keys(); it.hasNext(); ) {
            String key = it.next();

            map.put(Integer.parseInt(key), Utils.jsonArrayToStringArray(object.getJSONArray(key)));
        }

        return map;
    }

    private final static HashMap<String, CapacitorSqliteOpenHelper> openHelpers = new HashMap<>();
    static String TAG = "Sqlite";
    public static synchronized CapacitorSqliteOpenHelper openHelper(Context context, String dbName, Integer version, HashMap<Integer, String[]> upgrades) {
        Log.i(TAG, "Open helper " + dbName + " " + version);
        if (openHelpers.containsKey(dbName)) {
            CapacitorSqliteOpenHelper helper = openHelpers.get(dbName);

            if (helper != null) {
                SQLiteDatabase db = helper.getWritableDatabase();

                if (db.isOpen() && db.getVersion() == version) {
                    return helper;
                }
            }
        }
        Log.i(TAG, "Creating new open helper " + dbName + " " + version);
        openHelpers.put(dbName, new CapacitorSqliteOpenHelper(context, dbName, version, upgrades));
        return openHelpers.get(dbName);
    }

    public static synchronized CapacitorSqliteOpenHelper getHelper(String dbName) throws Exception {
        if (!openHelpers.containsKey(dbName)) {
            throw new Exception("SQLite DB " + dbName + " is not open.");
        }
        return openHelpers.get(dbName);
    }
}
