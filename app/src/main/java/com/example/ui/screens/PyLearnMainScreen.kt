package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.PyLearnViewModel

@Composable
fun PyLearnMainScreen(
  viewModel: PyLearnViewModel
) {
  val currentTab by viewModel.currentTab.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = BackgroundDark,
    bottomBar = {
      NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        val tabs = listOf(
          Triple(AppTab.LEARN, Icons.Default.School, "Learn"),
          Triple(AppTab.CHALLENGES, Icons.Default.SportsScore, "Challenges"),
          Triple(AppTab.COMPILER, Icons.Default.Terminal, "Compiler"),
          Triple(AppTab.REFERENCE, Icons.Default.MenuBook, "Cheat Sheet"),
          Triple(AppTab.PROFILE, Icons.Default.Person, "Profile")
        )

        tabs.forEach { (tab, icon, label) ->
          val isSelected = currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { viewModel.setTab(tab) },
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) BackgroundDark else TextSecondaryDark
              )
            },
            label = {
              Text(
                text = label,
                fontSize = 11.sp,
                color = if (isSelected) PythonBlue else TextSecondaryDark
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = PythonBlue
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.LEARN -> LearnScreen(viewModel = viewModel)
        AppTab.CHALLENGES -> ChallengesScreen(viewModel = viewModel)
        AppTab.COMPILER -> CompilerScreen(viewModel = viewModel)
        AppTab.REFERENCE -> ReferenceScreen(viewModel = viewModel)
        AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
      }
    }
  }
}
