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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
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
                    color = Color.White
                ) {
                    Home()
                }
            }
        }
    }
}

// Đổi Category để nhận id của file Drawable thay vì url String
data class Category(val id: String, val name: String, val iconRes: Int)
data class Product(val id: String, val name: String, val price: Double, val imageUrl: String)

@Composable
fun Home(modifier: Modifier = Modifier) {
    var cartCount by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            RenderBottomTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color.White
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

        Spacer(modifier = Modifier.height(18.dp))
        RenderCategories()

        Spacer(modifier = Modifier.height(18.dp))

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
            Icon(
                painter = painterResource(id = R.drawable.ic_search), // File ic_search.xml
                contentDescription = "Search",
                tint = Color(0xFF909090),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Make home",
                style = TextStyle(
                    fontFamily = MerriweatherRegular,
                    fontSize = 18.sp,
                    color = Color(0xFF909090)
                )
            )
            Text(
                text = "BEAUTIFUL",
                style = TextStyle(
                    fontFamily = MerriweatherBold,
                    fontSize = 20.sp,
                    color = Color(0xFF303030)
                )
            )
        }

        IconButton(onClick = { context.startActivity(Intent(context, CartScreen::class.java)) }) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart), // File ic_cart.xml
                    contentDescription = "Cart",
                    tint = Color(0xFF909090),
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                )

                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 9.sp,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
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
        Category("1", "Popular", R.drawable.ic_star),
        Category("2", "Chair", R.drawable.ic_chair),
        Category("3", "Table", R.drawable.ic_table),
        Category("4", "Armchair", R.drawable.ic_sofa),
        Category("5", "Bed", R.drawable.ic_bed),
        Category("6", "Lamp", R.drawable.ic_lamp)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Tổng khoảng trống = 16dp (Start) + 16dp (End) + (4 khoảng cách * 16dp) = 96.dp
    val itemWidth = (screenWidth - 96.dp) / 5

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                        .background(if (isSelected) Color(0xFF303030) else Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.iconRes),
                        contentDescription = category.name,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) Color.White else Color(0xFF909090)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF303030) else Color(0xFF909090)
                    )
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
        // Thu nhỏ khoảng cách dọc giữa 2 hàng xuống còn 16.dp
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
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
                .background(Color(0xFFF5F5F5))
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF909090).copy(alpha = 0.6f))
                    .clickable { onAddToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bag),
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.name,
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                color = Color(0xFF606060)
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$ ${String.format("%.2f", product.price)}",
            style = TextStyle(
                fontFamily = NunitoSansBold,
                fontSize = 14.sp,
                color = Color(0xFF303030)
            )
        )
    }
}

@Composable
fun RenderBottomTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Pair(R.drawable.ic_home_filled, R.drawable.ic_home),
            Pair(R.drawable.ic_bookmark_filled, R.drawable.ic_bookmark),
            Pair(R.drawable.ic_notification_filled, R.drawable.ic_notification),
            Pair(R.drawable.ic_profile_filled, R.drawable.ic_profile)
        )

        items.forEachIndexed { index, iconPair ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (selectedTab == index) iconPair.first else iconPair.second),
                        contentDescription = "Tab $index",
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

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = MerriweatherBold,
                fontSize = 20.sp,
                color = Color(0xFF303030)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    KOT1041_ASMTheme {
        Home()
    }
}