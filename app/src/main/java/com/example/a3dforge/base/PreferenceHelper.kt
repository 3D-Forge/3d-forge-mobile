package com.example.a3dforge.base

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveCredentials(userLogin: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USERLOGIN", userLogin)
        editor.putString("PASSWORD", password)
        editor.apply()
    }

    fun getSavedUserLogin(): String? {
        return sharedPreferences.getString("USERLOGIN", "")
    }

    fun getSavedPassword(): String? {
        return sharedPreferences.getString("PASSWORD", "")
    }

    fun saveRememberMeChecked(isChecked: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("REMEMBER_ME", isChecked)
        editor.apply()
    }

    fun isRememberMeChecked(): Boolean {
        return sharedPreferences.getBoolean("REMEMBER_ME", false)
    }

    fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("USERLOGIN")
        editor.remove("PASSWORD")
        editor.remove("REMEMBER_ME")
        editor.apply()
    }

    fun saveSelectedTheme(theme: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("SELECTED_THEME", theme)
        editor.apply()
    }

    fun getSelectedTheme(): Int {
        return sharedPreferences.getInt("SELECTED_THEME", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
