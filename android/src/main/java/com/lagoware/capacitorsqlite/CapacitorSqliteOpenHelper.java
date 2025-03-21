package com.lagoware.capacitorsqlite;

import android.content.Context;
import android.util.Log;

import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Objects;

public class CapacitorSqliteOpenHelper extends SQLiteOpenHelper  {
    String TAG = "CapacitorSqliteOpenHelper";
    private final HashMap<Integer, String[]> upgrades;

    public CapacitorSqliteOpenHelper (Context context, String dbName, int version, HashMap<Integer, String[]> upgrades) {
        super(context, dbName, null, version);
        this.upgrades = upgrades;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int highestVersion = 0;

        for (Integer version : upgrades.keySet()) {
            highestVersion = Math.max(version, highestVersion);
        }

        runUpgrade(db, 0, highestVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        runUpgrade(db, oldVersion, newVersion);
    }

    private void runUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Running db upgrade from " + oldVersion + " to " + newVersion);

        for (int ii = oldVersion + 1; ii <= newVersion; ii++) {
            if (upgrades.containsKey(ii)) {
                for (String statement : Objects.requireNonNull(upgrades.get(ii))) {
                    db.execSQL(statement);
                }
            }
        }
    }
}
