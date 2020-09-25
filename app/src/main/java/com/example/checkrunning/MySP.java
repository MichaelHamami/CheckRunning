package com.example.checkrunning;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class MySP {

    public interface KEYS {
        public static final String SP_NAME = "MY_SP";
        public static final String LIST_OF_TOP_GAMES = "LIST_OF_TOP_GAMES";
    }

    public interface VALUES {
        public static final String INITIAL_GAME_LIST = "";
        public static final int SIZE = 10;
        public static final int PLAYER_ONE = 1;
        public static final int PLAYER_TWO = 2;
    }

    private static MySP instance;

    private SharedPreferences prefs;

    public static MySP initHelper(Context context) {
        if (instance == null)
            instance = new MySP(context);
        return instance;
    }

    public static MySP getInstance() {
        return instance;
    }

    public MySP(Context context) {
        prefs = context.getSharedPreferences(KEYS.SP_NAME, MODE_PRIVATE);
    }

    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int def) {
        return prefs.getInt(key, def);
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putSetString(String key, Set<String> value){
        prefs.edit().putStringSet(key, value).apply();
    }
    public Set<String> getSetString(String key, Set<String> def){
        return prefs.getStringSet(key, def);
    }
}