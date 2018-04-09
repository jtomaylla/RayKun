package com.ecandle.raykun.helpers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by juantomaylla on 19/02/18.
 */

val prefs: Prefs by lazy {
    App.prefs!!
}

class App : Application() {
    companion object {
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}

class Prefs (context: Context) {
    val PREFS_FILENAME = "com.ecandle.raykun.prefs"
    val LOGGED_USER_ID = "logged_user_id"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var logged_user_id: String
        get() = prefs.getString(LOGGED_USER_ID, "0")
        set(value) = prefs.edit().putString(LOGGED_USER_ID, value).apply()
}