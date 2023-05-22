package com.example.stockdata.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MySharedPreferences {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LIST = "myList";

    private final SharedPreferences sharedPreferences;

    public MySharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<String> getList() {
        Set<String> stringSet = sharedPreferences.getStringSet(KEY_LIST, new HashSet<>());
        return new ArrayList<>(stringSet);
    }

    public void addToList(String item) {
        List<String> list = getList();
        list.add(item);
        saveList(list);
    }

    public void removeFromList(String item) {
        List<String> list = getList();
        list.remove(item);
        saveList(list);
    }

    public void removeAll() {
        saveList(new ArrayList<>());
    }

    private void saveList(List<String> list) {
        Set<String> stringSet = new HashSet<>(list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_LIST, stringSet);
        editor.apply();
    }
}
