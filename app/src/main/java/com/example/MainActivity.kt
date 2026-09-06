package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.PyLearnMainScreen
import com.example.ui.theme.PyLearnTheme
import com.example.ui.viewmodel.PyLearnViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: PyLearnViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PyLearnTheme(darkTheme = true) {
        PyLearnMainScreen(viewModel = viewModel)
      }
    }
  }
}

