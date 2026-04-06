package com.example.kot1041_asm

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
import com.example.kot1041_asm.ui.screens.*
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

    // Luồng khởi động: Nếu đã login thì vào home, ngược lại vào welcome
    val startDestination = if (isLoggedIn) "home" else "welcome"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") { WelcomeRoute(navController) }
        composable("login") { LoginRoute(navController, isLoggedIn, updateLoginState) }
        composable("register") { RegisterRoute(navController) }

        composable("home") { HomeRoute(navController, updateLoginState) }

        composable("search") { SearchRoute(navController) }
        composable("cart") { CartRoute(navController) }
        composable("address") { AddressRoute(navController) }

        // --- 1. THÊM ROUTE LỊCH SỬ ĐƠN HÀNG ---
        composable("order_history") { OrderHistoryRoute(navController) }

        // --- 2. THÊM ROUTE THÀNH CÔNG ---
        composable("success") { SuccessRoute(navController) }

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
// KHAI BÁO CÁC ROUTE WRAPPERS
// =====================================================================

@Composable
fun WelcomeRoute(navController: NavController) {
    WelcomeScreen(
        onGetStartedClick = {
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true }
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
            navController.navigate("home") {
                popUpTo(0) { inclusive = true }
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
                            updateLoginState(false)
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

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
                        onAddressClick = { navController.navigate("address") },
                        // --- ĐÃ KẾT NỐI VỚI ROUTE ORDER_HISTORY ---
                        onOrderHistoryClick = { navController.navigate("order_history") },
                        onLogoutClick = {
                            updateLoginState(false)
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
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
            navController.navigate("success") {
                // Xoá màn hình cart/checkout khỏi ngăn xếp để ko back lại được
                popUpTo("home") { inclusive = false }
            }
        }
    )
}

@Composable
fun AddressRoute(navController: NavController) {
    AddressScreen(
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun OrderHistoryRoute(navController: NavController) {
    OrderHistoryScreen(
        onBackClick = { navController.popBackStack() },
        onDetailClick = { orderId ->
            // TODO: Điều hướng sang màn hình chi tiết đơn hàng nếu cần
        }
    )
}

@Composable
fun SuccessRoute(navController: NavController) {
    SuccessScreen(
        onTrackOrderClick = {
            // --- KHI ĐẶT HÀNG XONG, BẤM TRACK THÌ SANG LỊCH SỬ ---
            navController.navigate("order_history") {
                popUpTo("home") { inclusive = false }
            }
        },
        onBackHomeClick = {
            navController.navigate("home") {
                popUpTo("home") { inclusive = false }
            }
        }
    )
}