package com.lagoware.capacitorsqlite;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

@CapacitorPlugin(name = "Sqlite")
public class SqlitePlugin extends Plugin {

    private final String TAG = "SqlitePlugin";

    private final Sqlite impl = new Sqlite();

    @PluginMethod
    public void runStatements(PluginCall call) throws Exception {
        String dbName = call.getString("dbName");
        JSONArray statementSpecs = call.getArray("statementSpecs");

        JSObject ret = new JSObject();

        try {
            ret.put("results", impl.runStatements(dbName, SqliteStatementSpec.fromJsonArray(statementSpecs)));
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void openDb(PluginCall call) throws Exception {
        String dbName = call.getString("dbName");
        Integer version = call.getInt("version");
        JSONObject upgrades = call.getObject("upgrades");

        Context context = this.bridge.getContext();

        try {
            CapacitorSqliteDbManager.openHelper(context, dbName, version, CapacitorSqliteDbManager.jsonObjectToUpgradesMap(upgrades));
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }
}
