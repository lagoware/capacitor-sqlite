package com.lagoware.capacitorsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CapacitorSqliteOpenHelper extends SQLiteOpenHelper  {
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
        for (int ii = oldVersion + 1; ii <= newVersion; ii++) {
            if (upgrades.containsKey(ii)) {
                for (String statement : Objects.requireNonNull(upgrades.get(ii))) {
                    db.execSQL(statement);
                }
            }
        }
    }
}
