package com.cinebook.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cinebook.app.ui.CineBookTheme
import com.cinebook.app.viewmodel.AuthViewModel
import com.cinebook.app.viewmodel.CinemaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            CineBookTheme {
                val auth: AuthViewModel = viewModel()
                val cinema: CinemaViewModel = viewModel()
                AppNav(auth, cinema)
            }
        }
    }
}
