package com.myapp.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("CaffyPrefs", Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    private val KEY_IS_FIRST_TIME = "isFirstTime"

    var isFirstTime: Boolean
        get() = prefs.getBoolean(KEY_IS_FIRST_TIME, true) // Default is true
        set(value) {
            editor.putBoolean(KEY_IS_FIRST_TIME, value)
            editor.apply()
        }
}