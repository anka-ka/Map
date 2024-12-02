package ru.netology.map.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.map.repository.SettingsRepository
import javax.inject.Inject

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
        }
    }
}