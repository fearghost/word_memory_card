package com.example.wordmemory.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordmemory.ui.screens.ParsingScreen
import com.example.wordmemory.ui.screens.ReviewEditScreen
import com.example.wordmemory.ui.screens.NewBundleScreen
import com.example.wordmemory.ui.screens.StudyScreen
import com.example.wordmemory.ui.viewmodel.WordViewModel
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.tts.TtsManager

@Composable
fun AppNavigation(
    viewModel: WordViewModel,
    repository: WordRepository,
    ttsManager: TtsManager
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { 
            Text("Home Dashboard") 
        }
        composable("parsing") { 
            ParsingScreen(viewModel) 
        }
        composable("review_edit") { 
            ReviewEditScreen(viewModel, onNavigationUp = { navController.popBackStack() }, onCancel = { navController.popBackStack() }) 
        }
        composable("new_bundle") { 
            NewBundleScreen(repository, onNavigationUp = { navController.popBackStack() }, onBundleCreated = { navController.popBackStack() }) 
        }
        composable("study") {
            // In a real app, we'd pass the actual words from the ViewModel
            StudyScreen(words = emptyList(), ttsManager = ttsManager, onComplete = { navController.popBackStack() })
        }
    }
}
