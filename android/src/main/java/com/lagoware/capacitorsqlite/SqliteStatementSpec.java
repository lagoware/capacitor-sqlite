package com.lagoware.capacitorsqlite;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SqliteStatementSpec {

    static private final String TAG = "CapSqliteStatementSpec";
    static SqliteStatementSpec[] fromJsonArray(JSONArray jsonArr) throws JSONException {
        int len = jsonArr.length();

        Log.i(TAG, "Converting JSONArray of length " + len + " to SqliteStatementSpecs");
        ArrayList<SqliteStatementSpec> specs = new ArrayList<>();

        for (int ii = 0; ii < len; ii++) {
            Log.i(TAG, "Getting JSONObject for index " + ii);
            JSONObject obj = jsonArr.getJSONObject(ii);

            Log.i(TAG, "Setting element for index " + ii);
            specs.add(fromJsonObject(obj));
        }

        Log.i(TAG, "Finished converting JSONArray of length " + len + " to SqliteStatementSpecs");

        SqliteStatementSpec[] arr = new SqliteStatementSpec[specs.size()];

        return specs.toArray(arr);
    }

    static SqliteStatementSpec fromJsonObject(JSONObject obj) throws JSONException {
        JSONArray params = obj.optJSONArray("params");

        if (params == null) {
            return new SqliteStatementSpec(
                obj.getString("type"),
                obj.getString("statement"),
                new String[] {},
                obj.optBoolean("beginsTransaction"),
                obj.optBoolean("commitsTransaction"),
                obj.optBoolean("rollsBackTransaction")
            );
        } if (params.optJSONArray(0) != null) {
            return new SqliteStatementSpec(
                obj.getString("type"),
                obj.getString("statement"),
                Utils.jsonArrayTo2dStringArray(params),
                obj.optBoolean("beginsTransaction"),
                obj.optBoolean("commitsTransaction"),
                obj.optBoolean("rollsBackTransaction")
            );
        } else {
            return new SqliteStatementSpec(
                obj.getString("type"),
                obj.getString("statement"),
                Utils.jsonArrayToStringArray(params),
                obj.optBoolean("beginsTransaction"),
                obj.optBoolean("commitsTransaction"),
                obj.optBoolean("rollsBackTransaction")
            );
        }
    }

    String statement;
    String type;
    String[] params;

    Boolean beginsTransaction = false;

    Boolean commitsTransaction = false;

    Boolean rollsBackTransaction = false;

    String[][] paramSets;

    public SqliteStatementSpec(String type, String statement, String[][] paramSets) {
        this.statement = statement;
        this.type = type;
        this.paramSets = paramSets;
    }

    public SqliteStatementSpec(String type, String statement, String[] params) {
        this.statement = statement;
        this.type = type;
        this.params = params;
    }

    public SqliteStatementSpec(String type, String statement, String[][] paramSets, Boolean beginsTransaction, Boolean commitsTransaction, Boolean rollsBackTransaction) {
        this.statement = statement;
        this.type = type;
        this.paramSets = paramSets;
        this.beginsTransaction = beginsTransaction;
        this.commitsTransaction = commitsTransaction;
        this.rollsBackTransaction = rollsBackTransaction;
    }

    public SqliteStatementSpec(String type, String statement, String[] params, Boolean beginsTransaction, Boolean commitsTransaction, Boolean rollsBackTransaction) {
        this.statement = statement;
        this.type = type;
        this.params = params;
        this.beginsTransaction = beginsTransaction;
        this.commitsTransaction = commitsTransaction;
        this.rollsBackTransaction = rollsBackTransaction;
    }
}
