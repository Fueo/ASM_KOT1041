package com.example.kot1041_asm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                    color = Color(0xFFF8F8F8) // Nền xám nhạt để khối trắng nổi bật lên
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

    // States quản lý tìm kiếm và bộ lọc
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

    // Logic Lọc sản phẩm
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

    // Box là Container gốc, cho phép xếp chồng các component lên nhau
    Box(modifier = Modifier.fillMaxSize()) {

        // --- LAYER DƯỚI: NỘI DUNG CÓ THỂ CUỘN ĐƯỢC ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            // Căn lề cho toàn bộ Grid
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp) // Cập nhật lại khoảng cách dọc cho giống HomeScreen
        ) {

            // 1. Thanh Search
            item(span = { GridItemSpan(maxLineSpan) }) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(text = "Search product...", color = TextSecondary, fontSize = 16.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = TextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 8.dp)
                        .height(52.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.LightGray),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // 2. Bộ lọc (Filters)
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
                                .background(if (isSelected) Primary else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Primary else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else TextSecondary,
                                style = Typography.bodySmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            }

            // 3. Danh sách sản phẩm
            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products found", style = Typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        // Thêm sự kiện onAddToCart vào đây để hết báo lỗi
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

        // --- LAYER TRÊN CÙNG: NÚT BACK DÍNH (STICKY/FLOATING) ---
        Box(
            modifier = Modifier
                .padding(start = 12.dp, top = 40.dp)
                .size(24.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(10.dp))
                .clickable { (context as? Activity)?.finish() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
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