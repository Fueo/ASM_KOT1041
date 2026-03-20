package com.example.kot1041_asm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kot1041_asm.ui.theme.*

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

data class Category(val id: String, val name: String, val iconUrl: String)
data class Product(val id: String, val name: String, val price: Double, val imageUrl: String)

@Composable
fun Home(modifier: Modifier = Modifier) {
    var cartCount by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeContent(
                modifier = modifier.padding(paddingValues),
                cartCount = cartCount,
                onAddToCart = { cartCount++ }
            )
            1 -> PlaceholderScreen("Bookmark Screen", modifier.padding(paddingValues))
            2 -> PlaceholderScreen("Notifications Screen", modifier.padding(paddingValues))
            3 -> PlaceholderScreen("Profile Screen", modifier.padding(paddingValues))
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    cartCount: Int,
    onAddToCart: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection(cartCount = cartCount)
        Spacer(modifier = Modifier.height(24.dp))
        RenderCategories()
        Spacer(modifier = Modifier.height(24.dp))

        // Bọc Grid trong Box và dùng weight(1f) để Grid đẩy hết xuống không gian còn lại
        Box(modifier = Modifier.weight(1f)) {
            RenderProductGrid(onAddToCart = onAddToCart)
        }
    }
}

@Composable
fun HeaderSection(cartCount: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { context.startActivity(Intent(context, SearchScreen::class.java)) }) {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = TextSecondary)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Make home",
                style = Typography.displaySmall.copy(fontWeight = FontWeight.Normal),
                color = TextSecondary
            )
            Text(
                text = "BEAUTIFUL",
                style = Typography.displayMedium,
                color = Primary
            )
        }

        IconButton(onClick = { context.startActivity(Intent(context, CartScreen::class.java)) }) {
            // Box bao bọc Icon và Custom Badge
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    tint = TextSecondary,
                    modifier = Modifier.padding(4.dp) // Thêm padding để có chỗ cho badge
                )

                // Custom Badge Đỏ Bo Tròn
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape) // Bo tròn hoàn hảo
                            .background(Color(0xFFE53935)) // Đỏ tươi
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenderCategories(modifier: Modifier = Modifier) {
    val categories = listOf(
        Category("1", "Popular", "https://cdn-icons-png.flaticon.com/512/1828/1828884.png"),
        Category("2", "Chair", "https://cdn-icons-png.flaticon.com/512/2874/2874052.png"),
        Category("3", "Table", "https://cdn-icons-png.flaticon.com/512/2619/2619082.png"),
        Category("4", "Armchair", "https://cdn-icons-png.flaticon.com/512/7511/7511242.png"),
        Category("5", "Bed", "https://cdn-icons-png.flaticon.com/512/3133/3133645.png"),
        Category("6", "Lamp", "https://cdn-icons-png.flaticon.com/512/3133/3133645.png")
    )

    var selectedIndex by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = (screenWidth - 32.dp) / 5

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        itemsIndexed(categories) { index, category ->
            val isSelected = index == selectedIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(itemWidth)
                    .clickable { selectedIndex = index }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Primary else SecondaryButtonBG),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = category.iconUrl,
                        contentDescription = category.name,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(if (isSelected) Color.White else TextSecondary)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    style = Typography.titleMedium,
                    color = if (isSelected) Primary else TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun RenderProductGrid(onAddToCart: () -> Unit) {
    val context = LocalContext.current

    val products = listOf(
        Product("1", "Black Simple Lamp", 12.00, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop"),
        Product("2", "Minimal Stand", 25.00, "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"),
        Product("3", "Coffee Chair", 20.00, "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop"),
        Product("4", "Simple Desk", 50.00, "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp), // Tăng nhẹ khoảng cách dọc
        contentPadding = PaddingValues(bottom = 24.dp) // Thêm padding dưới để cuộn lên không bị vướng
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onAddToCart = onAddToCart,
                onProductClick = { productId ->
                    val intent = Intent(context, ProductDetailScreen::class.java).apply {
                        putExtra("PRODUCT_ID", productId)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onProductClick(product.id) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(12.dp))
                .background(SecondaryButtonBG)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Khối nút Add to Cart
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    // Đổi lại nền màu xám mờ giống thiết kế cũ
                    .background(Color(0xFF606060).copy(alpha = 0.5f))
                    .clickable {
                        onAddToCart()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(18.dp),
                    // Đổi lại icon thành màu trắng cho nổi bật trên nền xám
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.name,
            style = Typography.bodySmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$ ${String.format("%.2f", product.price)}",
            fontFamily = NunitoSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Primary
        )
    }
}

@Composable
fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(70.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Pair(Icons.Filled.Home, Icons.Outlined.Home),
            Pair(Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
            Pair(Icons.Filled.Notifications, Icons.Outlined.Notifications),
            Pair(Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEachIndexed { index, iconPair ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) iconPair.first else iconPair.second,
                        contentDescription = "Tab $index"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    unselectedIconColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = Typography.displaySmall, color = Primary)
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    KOT1041_ASMTheme {
        Home()
    }
}