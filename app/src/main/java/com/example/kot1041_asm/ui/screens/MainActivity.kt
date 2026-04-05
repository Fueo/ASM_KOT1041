package com.example.kot1041_asm.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        setContent {
            KOT1041_ASMTheme {
                AppNavigation(sharedPreferences)
            }
        }
    }
}

@Composable
fun AppNavigation(sharedPreferences: SharedPreferences) {
    val navController = rememberNavController()

    var isLoggedIn by remember {
        mutableStateOf(sharedPreferences.getBoolean("is_logged_in", false))
    }

    val updateLoginState: (Boolean) -> Unit = { status ->
        isLoggedIn = status
        sharedPreferences.edit().putBoolean("is_logged_in", status).apply()
    }

    // 1. CHUẨN HOÁ LUỒNG KHỞI ĐỘNG:
    // Nếu đã đăng nhập thì vào thẳng home, chưa thì vào welcome/login
    val startDestination = if (isLoggedIn) "home" else "welcome"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") { WelcomeRoute(navController) }
        composable("login") { LoginRoute(navController, isLoggedIn, updateLoginState) }
        composable("register") { RegisterRoute(navController) }

        // Không cần truyền isLoggedIn vào HomeRoute nữa vì nếu vào được đây là đã log in
        composable("home") { HomeRoute(navController, updateLoginState) }

        composable("search") { SearchRoute(navController) }
        composable("cart") { CartRoute(navController) }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "1"
            ProductDetailRoute(navController, productId)
        }

        composable(
            route = "checkout/{orderTotal}",
            arguments = listOf(navArgument("orderTotal") { type = NavType.FloatType })
        ) { backStackEntry ->
            val orderTotal = backStackEntry.arguments?.getFloat("orderTotal")?.toDouble() ?: 0.0
            CheckoutRoute(navController, orderTotal)
        }
    }
}

// =====================================================================
// KHAI BÁO CÁC ROUTE
// =====================================================================

@Composable
fun WelcomeRoute(navController: NavController) {
    WelcomeScreen(
        onGetStartedClick = {
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true } // Xoá welcome khỏi backstack
            }
        }
    )
}

@Composable
fun LoginRoute(
    navController: NavController,
    isLoggedIn: Boolean,
    updateLoginState: (Boolean) -> Unit
) {
    Login(
        isLoggedIn = isLoggedIn,
        onSetLoggedIn = updateLoginState,
        onNavigateHome = {
            // 2. CHUẨN HOÁ BACKSTACK ĐĂNG NHẬP
            navController.navigate("home") {
                popUpTo(0) { inclusive = true } // Xoá toàn bộ backstack. Ở Home ấn back sẽ thoát app
            }
        },
        onClickRegister = {
            navController.navigate("register")
        }
    )
}

@Composable
fun RegisterRoute(navController: NavController) {
    SignUp(
        onClickLogin = {
            navController.popBackStack()
        }
    )
}

@Composable
fun HomeRoute(
    navController: NavController,
    updateLoginState: (Boolean) -> Unit
) {
    val bottomNavController = rememberNavController()

    MainScreen(
        bottomNavController = bottomNavController,
        content = { paddingValues ->
            NavHost(
                navController = bottomNavController,
                startDestination = "tab_home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("tab_home") {
                    HomeScreen(
                        onSearchClick = { navController.navigate("search") },
                        onCartClick = { navController.navigate("cart") },
                        onProductClick = { productId ->
                            navController.navigate("product_detail/$productId")
                        },
                        onLogoutClick = {
                            // 3. CHUẨN HOÁ BACKSTACK ĐĂNG XUẤT
                            updateLoginState(false)
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true } // Clear toàn bộ để không back lại được Home
                            }
                        }
                    )
                }

                // --- ĐÃ CẬP NHẬT CÁC HÀM CHUYỂN TRANG CHO BOOKMARK ---
                composable("tab_bookmark") {
                    BookmarkScreen(
                        onSearchClick = { navController.navigate("search") },
                        onCartClick = { navController.navigate("cart") },
                        onProductClick = { productId ->
                            navController.navigate("product_detail/$productId")
                        }
                    )
                }

                composable("tab_notification") {
                    NotificationScreen()
                }

                composable("tab_profile") {
                    ProfileScreen(
                        onSearchClick = { navController.navigate("search") },
                        onLogoutClick = {
                            updateLoginState(false)
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true } // Clear toàn bộ backstack
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun SearchRoute(navController: NavController) {
    Search(
        onBackClick = { navController.popBackStack() },
        onProductClick = { productId ->
            navController.navigate("product_detail/$productId")
        }
    )
}

@Composable
fun CartRoute(navController: NavController) {
    Cart(
        onBackClick = { navController.popBackStack() },
        onCheckoutClick = { totalPrice ->
            navController.navigate("checkout/${totalPrice.toFloat()}")
        }
    )
}

@Composable
fun ProductDetailRoute(navController: NavController, productId: String) {
    ProductDetail(
        productId = productId,
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun CheckoutRoute(navController: NavController, orderTotal: Double) {
    Checkout(
        orderTotal = orderTotal,
        onBackClick = { navController.popBackStack() },
        onSubmitOrder = {
            navController.navigate("home") {
                popUpTo("home") { inclusive = false }
            }
        }
    )
}