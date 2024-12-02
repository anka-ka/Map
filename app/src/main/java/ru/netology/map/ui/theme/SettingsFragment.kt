package ru.netology.map.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.map.R
import ru.netology.map.ViewModel.SettingsViewModel


class SettingsFragment : Fragment(R.layout.settings) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var languageGroup: RadioGroup
    private lateinit var themeGroup: RadioGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backMenu)
        saveButton = view.findViewById(R.id.save_settings)
        languageGroup = view.findViewById(R.id.radio_group_languages)
        themeGroup = view.findViewById(R.id.radio_group_themes)


        viewModel.language.observe(viewLifecycleOwner) { language ->
            when (language) {
                "en" -> languageGroup.check(R.id.radio_english)
                "ru" -> languageGroup.check(R.id.radio_russian)
            }
        }

        viewModel.theme.observe(viewLifecycleOwner) { theme ->
            when (theme) {
                "dark" -> themeGroup.check(R.id.radio_dark)
                "light" -> themeGroup.check(R.id.radio_light)
            }
        }

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        saveButton.setOnClickListener {
            val selectedLanguage = when (languageGroup.checkedRadioButtonId) {
                R.id.radio_english -> "en"
                R.id.radio_russian -> "ru"
                else -> "en"
            }

            val selectedTheme = when (themeGroup.checkedRadioButtonId) {
                R.id.radio_dark -> "dark"
                R.id.radio_light -> "light"
                else -> "light"
            }

            viewModel.saveSettings(selectedLanguage, selectedTheme)
        }
    }
}