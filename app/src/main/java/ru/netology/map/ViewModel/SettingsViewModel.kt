package ru.netology.map.ViewModel


import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.map.repository.SettingsRepository
import javax.inject.Inject
import java.util.Locale

@HiltViewModel
class SettingsViewModel@Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> get() = _language

    private val _theme = MutableLiveData<String>()
    val theme: LiveData<String> get() = _theme

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            _language.postValue(repository.getLanguage())
            _theme.postValue(repository.getTheme())
        }
    }

    fun saveSettings(language: String, theme: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveLanguage(language)
            repository.saveTheme(theme)


            withContext(Dispatchers.Main) {
                applyTheme(theme)
                applyLanguage(language)

                _language.value = language
                _theme.value = theme
            }
        }
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun applyLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Resources.getSystem().configuration
        config.setLocale(locale)
        Resources.getSystem().updateConfiguration(config, Resources.getSystem().displayMetrics)
    }
}
