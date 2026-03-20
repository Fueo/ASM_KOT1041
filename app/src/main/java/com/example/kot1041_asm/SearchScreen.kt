package com.example.kot1041_asm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.ui.theme.*

class SearchScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Search()
                }
            }
        }
    }
}

@Composable
fun Search() {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Chair", "Table", "Lamp", "Bed")

    val allProducts = remember {
        listOf(
            Product("1", "Black Simple Lamp", 12.00, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop"),
            Product("2", "Minimal Stand", 25.00, "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"),
            Product("3", "Coffee Chair", 20.00, "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop"),
            Product("4", "Simple Desk", 50.00, "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop"),
            Product("5", "Yellow Armchair", 45.00, "https://images.unsplash.com/photo-1519947486511-46149fa0a254?q=80&w=600&auto=format&fit=crop")
        )
    }

    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "Table" -> product.name.contains("Desk", true) || product.name.contains("Stand", true) || product.name.contains("Table", true)
            "Chair" -> product.name.contains("Chair", true) || product.name.contains("Armchair", true)
            else -> product.name.contains(selectedFilter, ignoreCase = true)
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- HEADER TĨNH: Nút Back + Thanh Search ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Back
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp), spotColor = Color.LightGray)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable { (context as? Activity)?.finish() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color(0xFF303030),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Thanh Search
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search product...",
                        style = TextStyle(
                            fontFamily = NunitoSansRegular,
                            fontSize = 16.sp,
                            color = Color(0xFF909090)
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color(0xFF303030),
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Clear",
                                tint = Color(0xFF303030),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f) // Chiếm toàn bộ không gian còn lại
                    .height(56.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.LightGray),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF303030),
                    focusedTextColor = Color(0xFF303030),
                    unfocusedTextColor = Color(0xFF303030)
                ),
                textStyle = TextStyle(
                    fontFamily = NunitoSansRegular,
                    fontSize = 16.sp,
                    color = Color(0xFF303030)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // --- NỘI DUNG CUỘN: Filters & Products ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Bộ lọc (Filters)
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF303030) else Color(0xFFF5F5F5))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                style = TextStyle(
                                    fontFamily = NunitoSansSemiBold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else Color(0xFF909090)
                                )
                            )
                        }
                    }
                }
            }

            // 2. Danh sách sản phẩm
            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products found",
                            style = TextStyle(
                                fontFamily = NunitoSansSemiBold,
                                fontSize = 16.sp,
                                color = Color(0xFF909090)
                            )
                        )
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            Toast.makeText(context, "Đã thêm ${product.name} vào giỏ!", Toast.LENGTH_SHORT).show()
                        },
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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchPreview() {
    KOT1041_ASMTheme {
        Search()
    }
}