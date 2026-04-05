package com.example.kot1041_asm.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kot1041_asm.R

@Composable
fun MainScreen(
    bottomNavController: NavHostController,
    content: @Composable (PaddingValues) -> Unit // Nhận nội dung (NavHost con) từ MainActivity
) {
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "tab_home"

    Scaffold(
        bottomBar = {
            RenderBottomTabs(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Render phần ruột được truyền từ MainActivity vào đây
        content(paddingValues)
    }
}

@Composable
fun RenderBottomTabs(currentRoute: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("tab_home", R.drawable.ic_home_filled, R.drawable.ic_home),
            Triple("tab_bookmark", R.drawable.ic_bookmark_filled, R.drawable.ic_bookmark),
            Triple("tab_notification",
                R.drawable.ic_notification_filled,
                R.drawable.ic_notification
            ),
            Triple("tab_profile", R.drawable.ic_profile_filled, R.drawable.ic_profile)
        )

        items.forEach { (route, selectedIcon, unselectedIcon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) selectedIcon else unselectedIcon),
                        contentDescription = route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF303030),
                    unselectedIconColor = Color(0xFF909090),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}