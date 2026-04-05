package com.example.kot1041_asm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.model.Product
import com.example.kot1041_asm.data.model.ProductImage
import com.example.kot1041_asm.ui.theme.*

@Composable
fun BookmarkScreen(
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var cartCount by remember { mutableStateOf(0) }

    // Mock Data tĩnh (Sau này bạn có thể thay bằng API repository.getBookmarks)
    val bookmarks = remember {
        mutableStateListOf(
            Product(_id = "1", ProductName = "Coffee Table", Price = 50.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "2", ProductName = "Coffee Chair", Price = 20.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "3", ProductName = "Minimal Stand", Price = 25.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "4", ProductName = "Minimal Desk", Price = 50.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop"))),
            Product(_id = "5", ProductName = "Minimal Lamp", Price = 12.00, CateID = null, productImage = listOf(ProductImage(url = "https://images.unsplash.com/photo-1519947486511-46149fa0a254?q=80&w=600&auto=format&fit=crop")))
        )
    }

    // Box ngoài cùng để chứa nội dung danh sách và Nút Add To Cart ghim ở đáy
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Header (Tìm kiếm - Favorites - Giỏ hàng)
            BookmarkHeader(
                cartCount = cartCount,
                onSearchClick = onSearchClick,
                onCartClick = onCartClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Danh sách sản phẩm Bookmark
            if (bookmarks.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No favorites yet",
                        style = TextStyle(fontFamily = NunitoSansRegular, color = Color(0xFF909090), fontSize = 16.sp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp) // Chừa khoảng trống cho nút bottom
                ) {
                    items(bookmarks) { product ->
                        BookmarkItem(
                            product = product,
                            onRemoveClick = {
                                bookmarks.remove(product) // Xóa khỏi danh sách tạm thời
                                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                            },
                            onAddToCartClick = {
                                cartCount++
                                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                            },
                            onItemClick = { onProductClick(product._id) }
                        )
                        // Đường viền ngăn cách giữa các item
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }

        // 3. Nút Add All To My Cart ghim ở dưới cùng màn hình
        if (bookmarks.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        cartCount += bookmarks.size
                        Toast.makeText(context, "All items added to cart!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0xFF303030).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303030)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Add all to my cart",
                        style = TextStyle(fontFamily = NunitoSansSemiBold, fontSize = 16.sp, color = Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkHeader(cartCount: Int, onSearchClick: () -> Unit, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search", tint = Color(0xFF909090), modifier = Modifier.size(24.dp))
        }

        Text(
            text = "Favorites",
            style = TextStyle(fontFamily = MerriweatherBold, fontSize = 18.sp, color = Color(0xFF303030))
        )

        IconButton(onClick = onCartClick) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(painter = painterResource(id = R.drawable.ic_cart), contentDescription = "Cart", tint = Color(0xFF909090), modifier = Modifier.padding(4.dp).size(24.dp))
                if (cartCount > 0) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFE53935)).align(Alignment.TopEnd), contentAlignment = Alignment.Center) {
                        Text(text = if (cartCount > 99) "99+" else cartCount.toString(), style = TextStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 9.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)))
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkItem(
    product: Product,
    onRemoveClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh sản phẩm
        val imageUrl = product.productImage.firstOrNull()?.url ?: ""
        AsyncImage(
            model = imageUrl,
            contentDescription = product.ProductName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        )

        Spacer(modifier = Modifier.width(20.dp))

        // Thông tin sản phẩm & Nút thao tác
        Column(
            modifier = Modifier.weight(1f).height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Hàng trên: Tên + Giá + Nút Xoá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = product.ProductName,
                        style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 14.sp, color = Color(0xFF909090))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ ${String.format("%.2f", product.Price)}",
                        style = TextStyle(fontFamily = NunitoSansBold, fontSize = 16.sp, color = Color(0xFF303030))
                    )
                }

                // Nút X (Dùng chung ic_close hoặc ic_close_circle nếu bạn có)
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close), // Thay bằng icon tròn x nếu bạn có
                        contentDescription = "Remove",
                        tint = Color(0xFF303030),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Hàng dưới: Nút Add to Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0)) // Nền xám nhạt theo thiết kế
                        .clickable { onAddToCartClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bag),
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF303030) // Màu icon đen đậm
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookmarkScreenPreview() {
    KOT1041_ASMTheme {
        BookmarkScreen()
    }
}