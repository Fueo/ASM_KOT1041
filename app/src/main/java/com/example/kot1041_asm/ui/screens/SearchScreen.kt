package com.example.kot1041_asm.ui.screens

import android.widget.Toast
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
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.*

// Import các model chuẩn từ AppModel
import com.example.kot1041_asm.data.model.Product
import com.example.kot1041_asm.data.model.ProductImage

@Composable
fun Search(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Chair", "Table", "Lamp", "Bed")

    // Cập nhật lại cách khởi tạo object Product theo đúng AppModel
    val allProducts = remember {
        listOf(
            Product(_id = "1", ProductName = "Black Simple Lamp", Price = 12.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "2", ProductName = "Minimal Stand", Price = 25.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "3", ProductName = "Coffee Chair", Price = 20.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "4", ProductName = "Simple Desk", Price = 50.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "5", ProductName = "Yellow Armchair", Price = 45.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1519947486511-46149fa0a254?q=80&w=600&auto=format&fit=crop")))
        )
    }

    // Cập nhật lại logic filter: dùng ProductName thay vì name
    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = product.ProductName.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "Table" -> product.ProductName.contains("Desk", true) || product.ProductName.contains("Stand", true) || product.ProductName.contains("Table", true)
            "Chair" -> product.ProductName.contains("Chair", true) || product.ProductName.contains("Armchair", true)
            else -> product.ProductName.contains(selectedFilter, ignoreCase = true)
        }
        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

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
                    .clickable { onBackClick() },
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
                    .weight(1f)
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
                            // Cập nhật lại dùng product.ProductName
                            Toast.makeText(context, "Đã thêm ${product.ProductName} vào giỏ!", Toast.LENGTH_SHORT).show()
                        },
                        onProductClick = { productId ->
                            onProductClick(productId)
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