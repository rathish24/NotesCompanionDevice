package com.dell.notescompaniondevice.utils

import android.content.Context
import android.content.SharedPreferences


object PreferenceUtils {
    private const val PREFS = "PREFS"
    private var prefs: SharedPreferences? = null
    private fun getSharedPreferences(context: Context): SharedPreferences? {
        if (prefs == null) {
            prefs = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )
        }
        return prefs
    }

    fun writeToPrefs(
        context: Context,
        sPref: String?,
        sValue: String?
    ) {
        val prefs = getSharedPreferences(context)
        val editor = prefs!!.edit()
        editor.putString(sPref, sValue)
        editor.apply()
    }

    fun writeBooleanPref(
        context: Context,
        pref: String?,
        value: Boolean?
    ) {
        val prefs = getSharedPreferences(context)
        val editor = prefs!!.edit()
        editor.putBoolean(pref, value!!)
        editor.apply()
    }

    fun getPrefs(context: Context, sPref: String?): String? {
        return getPrefs(context, sPref, "")
    }

    fun getPrefs(
        context: Context,
        sPref: String?,
        defaultValue: String?
    ): String? {
        val prefs = getSharedPreferences(context)
        return prefs!!.getString(sPref, defaultValue)
    }

    fun getBooleanPref(
        context: Context,
        sPref: String?,
        defaultValue: Boolean?
    ): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs!!.getBoolean(sPref, defaultValue!!)
    }

    fun removePrefs(context: Context, sPref: String?) {
        val prefs = getSharedPreferences(context)
        prefs!!.edit().remove(sPref).apply()
    }
}
