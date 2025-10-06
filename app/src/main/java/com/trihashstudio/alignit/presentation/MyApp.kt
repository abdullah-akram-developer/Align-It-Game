package com.trihashstudio.alignit.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun MyApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("friend") { PlayVsFriend(navController) }
            composable("ai") { PlayVsAI(navController) }

        }
    }
}
