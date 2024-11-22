package ru.netology.map.ui.theme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.map.R

@AndroidEntryPoint
class PreviewFragment : Fragment(R.layout.preview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.goButton).setOnClickListener {
            findNavController().navigate(R.id.action_previewFragment_to_mapFragment)
        }
    }
}