package ru.netology.map.repository


import android.content.SharedPreferences
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val preferences: SharedPreferences
) {

    fun getLanguage(): String {
        return preferences.getString("language", "en") ?: "en"
    }

    fun getTheme(): String {
        return preferences.getString("theme", "light") ?: "light"
    }

    fun saveLanguage(language: String) {
        preferences.edit().putString("language", language).apply()
    }

    fun saveTheme(theme: String) {
        preferences.edit().putString("theme", theme).apply()
    }

    fun getAllSettings(): Pair<String, String> {
        val language = preferences.getString("language", "en") ?: "en"
        val theme = preferences.getString("theme", "light") ?: "light"
        return Pair(language, theme)
    }
}