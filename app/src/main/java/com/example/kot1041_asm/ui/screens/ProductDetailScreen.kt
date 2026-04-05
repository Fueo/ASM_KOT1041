package com.example.kot1041_asm.ui.screens

import AddToCartRequest
import BookmarkRequest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.model.Product
import com.example.kot1041_asm.data.repository.AppRepository
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProductDetail(
    productId: String,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AppRepository() }

    // Lấy AccountID từ SharedPreferences
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val currentAccountId = sharedPref.getString("user_id", "") ?: ""

    // States
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var quantity by remember { mutableStateOf(1) }
    var selectedColorIndex by remember { mutableStateOf(0) }

    // State cho Bookmark (Không cần bookmarkId nữa)
    var isBookmarked by remember { mutableStateOf(false) }

    // Màu sắc nút
    val colors = listOf(
        Color(0xFF909090),
        Color(0xFFB4916C),
        Color(0xFFE4CBAD)
    )

    // Lấy thông tin sản phẩm và kiểm tra trạng thái Bookmark
    LaunchedEffect(productId) {
        isLoading = true

        // 1. Fetch Chi tiết sản phẩm
        val result = repository.getProductDetail(productId)
        if (result.isSuccess) {
            product = result.getOrNull()
        } else {
            Toast.makeText(context, "Failed to load product details", Toast.LENGTH_SHORT).show()
        }

        // 2. Fetch danh sách Bookmark của User để xem sản phẩm này đã được lưu chưa
        if (currentAccountId.isNotEmpty()) {
            val bookmarkRes = repository.getBookmarks(currentAccountId)
            if (bookmarkRes.isSuccess) {
                val userBookmarks = bookmarkRes.getOrNull() ?: emptyList()
                // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
                val existingBookmark = userBookmarks.find { it.ProductID?._id == productId }
                isBookmarked = existingBookmark != null
            }
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF303030))
            }
        } else if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Product not found!",
                    style = TextStyle(fontFamily = NunitoSansBold, fontSize = 18.sp, color = Color(0xFF909090))
                )
            }
        } else {
            val currentProduct = product!!
            val images = currentProduct.productImage.map { it.url }.ifEmpty { listOf("") }

            TopImageSection(
                modifier = Modifier.weight(1f),
                images = images,
                colors = colors,
                selectedColorIndex = selectedColorIndex,
                onColorSelected = { selectedColorIndex = it },
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = currentProduct.ProductName,
                    style = TextStyle(
                        fontFamily = MerriweatherRegular,
                        fontSize = 24.sp,
                        color = Color(0xFF303030)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ ${if (currentProduct.Price % 1 == 0.0) currentProduct.Price.toInt() else currentProduct.Price}",
                        style = TextStyle(
                            fontFamily = NunitoSansBold,
                            fontSize = 30.sp,
                            color = Color(0xFF303030)
                        )
                    )

                    QuantitySelector(
                        quantity = quantity,
                        onIncrease = { quantity++ },
                        onDecrease = { if (quantity > 1) quantity-- }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color(0xFFF2C94C),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentProduct.rating.toString(),
                        style = TextStyle(
                            fontFamily = NunitoSansBold,
                            fontSize = 18.sp,
                            color = Color(0xFF303030)
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "(${currentProduct.numReviews} reviews)",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF909090)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentProduct.Description ?: "No description available.",
                    style = TextStyle(
                        fontFamily = NunitoSansLight,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF606060)
                    ),
                    textAlign = TextAlign.Justify
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            BottomActionRow(
                isBookmarked = isBookmarked,
                onBookmarkClick = {
                    if (currentAccountId.isEmpty()) {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        return@BottomActionRow
                    }

                    coroutineScope.launch {
                        // Gọi chung 1 API toggleBookmark
                        val request = BookmarkRequest(AccountID = currentAccountId, ProductID = currentProduct._id)
                        val res = repository.toggleBookmark(request)

                        if (res.isSuccess) {
                            // Đảo ngược trạng thái hiện tại trên UI
                            isBookmarked = !isBookmarked

                            val message = if (isBookmarked) "Added to favorites" else "Removed from favorites"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to update favorites", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onAddToCart = {
                    if (currentAccountId.isEmpty()) {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        return@BottomActionRow
                    }

                    coroutineScope.launch {
                        val request = AddToCartRequest(
                            AccountID = currentAccountId,
                            ProductID = currentProduct._id,
                            Quantity = quantity
                        )
                        val res = repository.addToCart(request)
                        if (res.isSuccess) {
                            Toast.makeText(context, "Added $quantity items to cart!", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMsg = res.exceptionOrNull()?.message ?: "Failed to add"
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

// ---------------- CÁC COMPONENT PHỤ TRỢ ----------------

@Composable
fun TopImageSection(
    modifier: Modifier = Modifier,
    images: List<String>,
    colors: List<Color>,
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(bottomStart = 50.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 40.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(images.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 30.dp else 16.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isSelected) Color(0xFF303030) else Color.White)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 24.dp, top = 50.dp)
                .size(40.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(10.dp))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF303030)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(50.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(50.dp))
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            colors.forEachIndexed { index, color ->
                val isSelected = index == selectedColorIndex
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) Color(0xFF909090) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_minus),
                contentDescription = "Decrease",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF303030)
            )
        }

        Text(
            text = quantity.toString().padStart(2, '0'),
            style = TextStyle(
                fontFamily = NunitoSansSemiBold,
                fontSize = 18.sp,
                color = Color(0xFF303030)
            )
        )

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Increase",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF303030)
            )
        }
    }
}

@Composable
fun BottomActionRow(
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Bookmark tự đổi màu dựa trên state
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isBookmarked) Color(0xFF303030) else Color(0xFFF0F0F0))
                .clickable { onBookmarkClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Bookmark",
                tint = if (isBookmarked) Color.White else Color(0xFF303030),
                modifier = Modifier.size(24.dp)
            )
        }

        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303030)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Add to cart",
                style = TextStyle(
                    fontFamily = NunitoSansSemiBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailPreview() {
    KOT1041_ASMTheme {
        ProductDetail(productId = "1")
    }
}