package com.lagoware.capacitorsqlite;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Utils {

    static String[][] jsonArrayTo2dStringArray(JSONArray arr) throws JSONException {
        int len = arr.length();

        ArrayList<String[]> strings = new ArrayList<>();

        for (int ii = 0; ii < len; ii++) {
            strings.add(jsonArrayToStringArray(arr.getJSONArray(ii)));
        }

        String[][] stringsArr = new String[strings.size()][];

        return strings.toArray(stringsArr);
    }

    static String[] jsonArrayToStringArray(JSONArray arr) throws JSONException {
        int len = arr.length();

        ArrayList<String> strings = new ArrayList<>();

        for (int ii = 0; ii < len; ii++) {
            strings.add(arr.getString(ii));
        }

        String[] stringArr = new String[strings.size()];

        return strings.toArray(stringArr);
    }
}
