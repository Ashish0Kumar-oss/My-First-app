package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.Routes
import com.example.features.converter.ConverterScreen
import com.example.features.favorites.FavoritesScreen
import com.example.features.history.RecentScreen
import com.example.features.home.HomeScreen
import com.example.features.settings.SettingsScreen
import com.example.features.viewer.ResultScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    val bottomNavItems = listOf(
        Triple(Routes.HOME, "Home", Icons.Default.Home),
        Triple(Routes.RECENT, "Recent", Icons.Default.History),
        Triple(Routes.FAVORITES, "Favorites", Icons.Default.Favorite),
        Triple(Routes.SETTINGS, "Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on top level destinations
            val showBottomBar = bottomNavItems.any { it.first == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    bottomNavItems.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { 
                                Text(
                                    label.uppercase(), 
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 0.5.sp
                                ) 
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToConverter = { navController.navigate(Routes.CONVERTER) },
                    onNavigateToRecent = { navController.navigate(Routes.RECENT) },
                    onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                    onPdfClick = { pdf ->
                        val encodedPath = Uri.encode(pdf.filePath)
                        navController.navigate("result/$encodedPath")
                    }
                )
            }
            
            composable(Routes.RECENT) {
                RecentScreen(
                    viewModel = viewModel,
                    onPdfClick = { pdf ->
                        val encodedPath = Uri.encode(pdf.filePath)
                        navController.navigate("result/$encodedPath")
                    }
                )
            }
            
            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onPdfClick = { pdf ->
                        val encodedPath = Uri.encode(pdf.filePath)
                        navController.navigate("result/$encodedPath")
                    }
                )
            }
            
            composable(Routes.SETTINGS) {
                SettingsScreen(viewModel = viewModel)
            }
            
            composable(Routes.CONVERTER) {
                ConverterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToResult = { filePath ->
                        navController.navigate("result/$filePath") {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            
            composable(Routes.RESULT) { backStackEntry ->
                val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
                ResultScreen(
                    filePath = Uri.decode(filePath),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

