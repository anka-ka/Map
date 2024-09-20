package ru.netology.map.ui.theme

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.R
class PreviewFragment : Fragment(R.layout.preview) {

    private lateinit var previewView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.preview)


        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        view.findViewById<View>(R.id.goButton).setOnClickListener {
            navController.navigate(R.id.action_previewFragment_to_mapFragment)
        }
    }
}