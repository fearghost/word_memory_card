package com.example.wordmemory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Text("Home Screen") }
        composable("bundle_list") { Text("Bundle List Screen") }
        composable("study_mode") { Text("Study Mode Screen") }
        composable("add_new") { Text("Add New Bundle Screen") }
    }
}
