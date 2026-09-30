package com.example.nasaapod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nasaapod.ui.ApodDetailScreen
import com.example.nasaapod.ui.ApodListScreen
import com.example.nasaapod.ui.ApodViewModel
import com.example.nasaapod.ui.theme.NASAApodTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ApodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASAApodTheme {
                val navController = rememberNavController()
                val selectedItem by viewModel.selectedItem.collectAsState()

                NavHost(navController = navController, startDestination = "list") {
                    composable(
                        "list",
                        enterTransition = { slideInHorizontally(initialOffsetX = { 0 }) },
                        exitTransition = { slideOutHorizontally(targetOffsetX = { 0 }) }) {
                        ApodListScreen(
                            viewModel = viewModel,
                            onItemClick = { item ->
                                viewModel.selectItem(item)
                                navController.navigate("detail")
                            }
                        )
                    }
                    composable(
                        "detail",
                        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                        exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
                    ) {
                        selectedItem?.let { item ->
                            ApodDetailScreen(
                                item = item,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
