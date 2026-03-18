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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            RenderCategories()
            Spacer(modifier = Modifier.height(24.dp))
            RenderProductGrid()
        }
    }
}

@Composable
fun HeaderSection() {
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
            Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Cart", tint = TextSecondary)
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
        Category("5", "Bed", "https://cdn-icons-png.flaticon.com/512/3133/3133645.png")
    )

    var selectedIndex by remember { mutableStateOf(0) }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            val isSelected = index == selectedIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selectedIndex = index }
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
fun RenderProductGrid() {
    val context = LocalContext.current // Lấy context để start Activity

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                // Truyền hàm xử lý click vào card
                onProductClick = { productId ->
                    // Tạo Intent và truyền ID sang ProductDetailScreen
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
fun ProductCard(product: Product, onProductClick: (String) -> Unit) {
    // Thêm Modifier.clickable để bắt sự kiện click lên toàn bộ Card
    Column(modifier = Modifier.fillMaxWidth().clickable { onProductClick(product.id) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SecondaryButtonBG)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.8f))
                    .clickable { /* Xử lý thêm vào giỏ ngay tại đây nếu cần */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.name,
            style = Typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = "$ ${String.format("%.2f", product.price)}",
            style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Primary
        )
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle Navigation */ }) {
            Icon(Icons.Filled.Home, contentDescription = "Home", tint = Primary)
        }
        IconButton(onClick = { /* Handle Navigation */ }) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Bookmark", tint = TextSecondary)
        }
        IconButton(onClick = { /* Handle Navigation */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextSecondary)
        }
        IconButton(onClick = { /* Handle Navigation */ }) {
            Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = TextSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    KOT1041_ASMTheme {
        Home()
    }
}