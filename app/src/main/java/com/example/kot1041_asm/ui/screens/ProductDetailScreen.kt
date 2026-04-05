package com.example.kot1041_asm.ui.screens

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
import com.example.kot1041_asm.ui.theme.*

// Data class để chứa dữ liệu chi tiết sản phẩm
data class ProductDetailData(
    val id: String,
    val name: String,
    val price: Double,
    val rating: Double,
    val reviews: Int,
    val description: String,
    val images: List<String>
)

// Hàm giả lập việc fetch dữ liệu từ API dựa trên ID
fun getMockProductData(id: String): ProductDetailData {
    return when (id) {
        "1" -> ProductDetailData(
            id = "1", name = "Black Simple Lamp", price = 12.0, rating = 4.0, reviews = 20,
            description = "A beautiful black simple lamp perfectly designed for reading and focused work. Fits seamlessly in any modern workspace.",
            images = listOf("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop", "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?q=80&w=600&auto=format&fit=crop")
        )
        "2" -> ProductDetailData(
            id = "2", name = "Minimal Stand", price = 50.0, rating = 4.5, reviews = 50,
            description = "Minimal Stand is made of by natural wood. The design that is very simple and minimal. This is truly one of the best furnitures in any family for now. With 3 different colors, you can easily select the best match for your home.",
            images = listOf("https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop", "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?q=80&w=600&auto=format&fit=crop")
        )
        "3" -> ProductDetailData(
            id = "3", name = "Coffee Chair", price = 20.0, rating = 4.8, reviews = 120,
            description = "Relaxing coffee chair with ergonomic design. Enjoy your morning coffee in absolute comfort.",
            images = listOf("https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=600&auto=format&fit=crop", "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?q=80&w=600&auto=format&fit=crop")
        )
        "4" -> ProductDetailData(
            id = "4", name = "Simple Desk", price = 50.0, rating = 4.2, reviews = 35,
            description = "A simple, sturdy wooden desk ideal for your home office or study room. Offers plenty of legroom.",
            images = listOf("https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop")
        )
        else -> ProductDetailData(
            id = id, name = "Unknown Product", price = 0.0, rating = 0.0, reviews = 0,
            description = "No description available.",
            images = listOf("https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop")
        )
    }
}

@Composable
fun ProductDetail(
    productId: String,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val productData = remember(productId) { getMockProductData(productId) }

    // Màu sắc nút như thiết kế
    val colors = listOf(
        Color(0xFF909090), // Xám
        Color(0xFFB4916C), // Nâu nhạt
        Color(0xFFE4CBAD)  // Be
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopImageSection(
            modifier = Modifier.weight(1f),
            images = productData.images,
            colors = colors,
            selectedColorIndex = selectedColorIndex,
            onColorSelected = { selectedColorIndex = it },
            onBackClick = onBackClick // Dùng Callback thay vì finish()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = productData.name,
                style = TextStyle(
                    fontFamily = MerriweatherRegular,
                    fontSize = 24.sp,
                    color = Color(0xFF303030)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dòng Giá và Số lượng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ${if (productData.price % 1 == 0.0) productData.price.toInt() else productData.price}",
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

            // Dòng Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Rating",
                    tint = Color(0xFFF2C94C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = productData.rating.toString(),
                    style = TextStyle(
                        fontFamily = NunitoSansBold,
                        fontSize = 18.sp,
                        color = Color(0xFF303030)
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "(${productData.reviews} reviews)",
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF909090)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mô tả
            Text(
                text = productData.description,
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

        // Nút Add to Cart
        BottomActionRow(
            onAddToCart = {
                Toast.makeText(context, "Đã thêm $quantity ${productData.name} vào giỏ!", Toast.LENGTH_SHORT).show()
            }
        )
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
        // Ảnh sản phẩm với góc bo tròn phía dưới bên trái
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

            // Dấu chấm (Dashes) chuyển ảnh
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

        // Nút Quay Lại
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

        // Chọn màu sắc (Color Picker)
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
        // Nút Trừ
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

        // Nút Cộng
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
fun BottomActionRow(onAddToCart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Bookmark (Giữ để lưu)
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { /* Handle Bookmark */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Bookmark",
                tint = Color(0xFF303030),
                modifier = Modifier.size(24.dp)
            )
        }

        // Nút Add to Cart
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
        ProductDetail(productId = "2")
    }
}