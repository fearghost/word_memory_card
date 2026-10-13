package com.example.wordmemory.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordmemory.ui.screens.ParsingScreen
import com.example.wordmemory.ui.screens.HomeScreen
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
            HomeScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToNewBundle = { navController.navigate("new_bundle") },
                onNavigateToStudy = { bundleId -> navController.navigate("study/$bundleId") },
                onNavigateToEdit = { bundleId -> navController.navigate("review_edit/$bundleId") }
            )
        }
        composable("parsing") { 
            ParsingScreen(viewModel) 
        }
        composable("review_edit/{bundleId}") { backStackEntry ->
            val bundleId = backStackEntry.arguments?.getString("bundleId")?.toLong() ?: 0L
            ReviewEditScreen(
                bundleId = bundleId,
                viewModel = viewModel,
                onSave = { viewModel.saveWordsToBundle(bundleId, it, repository) },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("new_bundle") { 
            NewBundleScreen(
                repository = repository,
                onNavigationUp = { navController.popBackStack() }, 
                onBundleCreated = { navController.popBackStack() }
            ) 
        }
        composable("study/{bundleId}") { backStackEntry ->
            val bundleId = backStackEntry.arguments?.getString("bundleId")?.toLong() ?: 0L
            // In a real app, we'd fetch words from the repository here.
            // For now, we pass empty list or handle it in the ViewModel.
            StudyScreen(
                words = emptyList(), // Placeholder: should be loaded from DB
                ttsManager = ttsManager, 
                onComplete = { navController.popBackStack() }
            )
        }
    }
}
