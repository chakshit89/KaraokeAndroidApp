import androidx.compose.foundation.layout.navigationBarsPadding
import com.karoke.myoyasing.presentation.navigation.routes.AppRoutes


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.karoke.myoyasing.presentation.navigation.routes.BottomNavigationBar


@Composable
fun MainBottomNavScreen(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val selectedTab = when (currentRoute) {
        AppRoutes.HOME -> 0
        AppRoutes.FEED -> 1
        AppRoutes.PROFILE -> 2
        else -> 0
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color(0xFF111111),
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onHomeClick = {
                    navController.navigate(AppRoutes.HOME) {
                        launchSingleTop = true
                    }
                },
                onFeedClick = {
                    navController.navigate(AppRoutes.FEED) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            content()
        }
    }
}



