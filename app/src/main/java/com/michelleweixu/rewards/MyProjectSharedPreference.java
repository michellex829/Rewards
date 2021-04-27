package com.michelleweixu.rewards;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyProjectSharedPreference {

    private static final String TAG = "MyProjectSharedPreference";
    private SharedPreferences prefs;

    MyProjectSharedPreference(Activity activity) {
        super();
        prefs = activity.getSharedPreferences("MY_PREFS_KEY", Context.MODE_PRIVATE);
    }

    void save(String key, String text) {
        Editor editor = prefs.edit();
        editor.putString(key, text);
        editor.apply(); // commit T/F
    }


    String getValue(String key) {
        String text = prefs.getString(key, "");
        return text;
    }


    void clearAll() {
        Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // commit T/F
    }

    void removeValue(String key) {
        Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply(); // commit T/F
    }
}
