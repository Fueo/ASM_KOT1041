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

// Import các model và repo chuẩn
import com.example.kot1041_asm.data.model.Product
import com.example.kot1041_asm.data.model.Category
import com.example.kot1041_asm.data.repository.AppRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Composable
fun Search(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { AppRepository() }

    // States cho UI và API
    var searchQuery by remember { mutableStateOf("") }
    var selectedCateId by remember { mutableStateOf("") } // "" nghĩa là "All"

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // 1. Fetch Categories khi vừa vào màn hình
    LaunchedEffect(Unit) {
        val catResult = repository.getAllCategories()
        if (catResult.isSuccess) {
            categories = catResult.getOrNull() ?: emptyList()
        } else {
            Toast.makeText(context, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Fetch Products mỗi khi searchQuery hoặc selectedCateId thay đổi
    LaunchedEffect(searchQuery, selectedCateId) {
        // Debounce: Đợi người dùng ngừng gõ 500ms rồi mới gọi API để tránh spam server
        delay(500)

        isLoading = true
        try {
            val cateParam = if (selectedCateId.isBlank()) null else selectedCateId
            val kwParam = if (searchQuery.isBlank()) null else searchQuery

            // Gọi API thông qua AppRepository
            val result = repository.getProducts(
                cateId = cateParam,
                keyword = kwParam,
                page = 1,
                limit = 20 // Số lượng sản phẩm hiển thị tối đa
            )

            if (result.isSuccess) {
                products = result.getOrNull() ?: emptyList()
            } else {
                products = emptyList()
            }
        } catch (e: CancellationException) {
            throw e // Bắt buộc ném lại lỗi huỷ coroutine để Compose xử lý
        } catch (e: Exception) {
            products = emptyList()
            Toast.makeText(context, "Lỗi tìm kiếm sản phẩm", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
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
            // 1. Bộ lọc (Filters) dựa trên API Categories
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    // Mục "All"
                    item {
                        val isAllSelected = selectedCateId.isEmpty()
                        FilterChipUI(
                            text = "All",
                            isSelected = isAllSelected,
                            onClick = { selectedCateId = "" }
                        )
                    }

                    // Mục "Popular" hoặc các mục khác nếu BE có thẻ cứng Popular
                    // (Bạn có thể bỏ qua nếu không cần tab Popular trong Search)

                    // Render danh sách category từ API
                    items(categories) { category ->
                        // Bỏ qua tab "Popular" nếu nó đã nằm trong list (tùy logic BE của bạn)
                        if (category.CateName.contains("Popular", true)) return@items

                        val isSelected = selectedCateId == category._id
                        FilterChipUI(
                            text = category.CateName,
                            isSelected = isSelected,
                            onClick = { selectedCateId = category._id }
                        )
                    }
                }
            }

            // 2. Danh sách sản phẩm
            if (isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF303030))
                    }
                }
            } else if (products.isEmpty()) {
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
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            Toast.makeText(context, "Added ${product.ProductName} to cart!", Toast.LENGTH_SHORT).show()
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

// Tách Box Filter ra thành Composable nhỏ cho gọn code
@Composable
fun FilterChipUI(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF303030) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = NunitoSansSemiBold,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color(0xFF909090)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchPreview() {
    KOT1041_ASMTheme {
        Search()
    }
}