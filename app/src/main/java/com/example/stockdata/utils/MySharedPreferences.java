package com.example.stockdata.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MySharedPreferences {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LIST = "myList";

    private SharedPreferences sharedPreferences;

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

        // Convert the input string to lowercase for case-insensitive comparison
        String lowercaseItem = item.toLowerCase();

        // Create an iterator for the list
        Iterator<String> iterator = list.iterator();

        // Iterate through the list
        while (iterator.hasNext()) {
            String listItem = iterator.next();
            // If the current item in the list, when converted to lowercase,
            // is equal to the lowercase input string, remove it
            if (listItem.toLowerCase().equals(lowercaseItem)) {

                iterator.remove();
            }
        }

        saveList(list);
    }


    public void removeAll() {
        saveList(new ArrayList<>());
    }

    private void saveList(List<String> list) {
        Set<String> stringSet = new HashSet<>(list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_LIST, stringSet);

        // Use commit() instead of apply() to save changes immediately
        boolean success = editor.commit();

        // Print out whether the save was successful
        System.out.println("Save successful: " + success);
    }

}

