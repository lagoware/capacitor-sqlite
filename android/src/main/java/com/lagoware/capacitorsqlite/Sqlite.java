package com.lagoware.capacitorsqlite;

import android.database.Cursor;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Sqlite {

    private final String TAG = "CapacitorSqlite";

    public JSONArray runStatements(String dbName, SqliteStatementSpec[] statementSpecs) throws Exception {
        SQLiteDatabase db = CapacitorSqliteDbManager.getHelper(dbName).getWritableDatabase();

        JSONArray results = new JSONArray();

        Log.i(TAG, "Running " + statementSpecs.length + " statements");

        for (int ii = 0; ii < statementSpecs.length; ii++) {
            Log.i(TAG, "Running statement " + ii);
            SqliteStatementSpec statementSpec = statementSpecs[ii];

            if (statementSpec.beginsTransaction) {
                db.beginTransaction();
            }
            try {
                switch (statementSpec.type) {
                    case "query":
                        if (statementSpec.paramSets != null) {
                            results.put(ii, runQueryStatement(db, statementSpec.statement, statementSpec.paramSets));
                        } else {
                            results.put(ii, runQueryStatement(db, statementSpec.statement, statementSpec.params));
                        }
                        break;
                    case "command":
                        if (statementSpec.paramSets != null) {
                            runCommandStatement(db, statementSpec.statement, statementSpec.paramSets);
                        } else if (statementSpec.params != null) {
                            runCommandStatement(db, statementSpec.statement, statementSpec.params);
                        } else {
                            runCommandStatement(db, statementSpec.statement);
                        }
                        results.put(ii, null);
                        break;
                }
            } catch (Exception error) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                throw error;
            }

            if (statementSpec.commitsTransaction) {
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        }

        Log.i(TAG, "Finished running " + statementSpecs.length + " statements");
        return results;
    }

    private String sanitizeQueryString(String sqlQuery) {
        String trimmedString = sqlQuery.trim();
        if (trimmedString.charAt(trimmedString.length() - 1) == ';') {
            return trimmedString.substring(0, trimmedString.length() - 1);
        }
        return trimmedString;
    }

    private JSONArray runQueryStatement(SQLiteDatabase db, String statement, String[][] paramSets) throws JSONException {
        JSONArray results = new JSONArray();

        for (int ii = 0; ii < paramSets.length; ii++) {
            results.put(ii, runQueryStatement(db, statement, paramSets[ii]));
        }

        return results;
    }

    private JSONArray runQueryStatement(SQLiteDatabase db, String statement, String[] params) throws JSONException {
        Cursor cursor = params == null ? db.rawQuery(sanitizeQueryString(statement), new String[] {}) : db.rawQuery(sanitizeQueryString(statement), params);

        String[] names = cursor.getColumnNames();
        int cols = cursor.getColumnCount();
        JSONArray arr = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject obj = new JSONObject();
            for (int col = 0; col < cols; col++) {
                String name = names[col];
                switch (cursor.getType(col)) {
                    case Cursor.FIELD_TYPE_NULL:
                        obj.put(name, null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        obj.put(name, cursor.getInt(col));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        obj.put(name, cursor.getFloat(col));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        obj.put(name, cursor.getString(col));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        obj.put(name, cursor.getBlob(col));
                        break;
                }
            }
            arr.put(obj);
        }
        cursor.close();
        return arr;
    }

    private void runCommandStatement(SQLiteDatabase db, String statement) {
        SQLiteStatement st = db.compileStatement(statement);
        st.execute();
    }

    private void runCommandStatement(SQLiteDatabase db, String statement, String[][] paramSets) {
        SQLiteStatement st = db.compileStatement(statement);

        for (String[] params : paramSets) {
            st.bindAllArgsAsStrings(params);
            st.execute();
            st.clearBindings();
        }
    }

    private void runCommandStatement(SQLiteDatabase db, String statement, String[] params) {
        SQLiteStatement st = db.compileStatement(statement);

        st.bindAllArgsAsStrings(params);

        st.execute();
    }
}
