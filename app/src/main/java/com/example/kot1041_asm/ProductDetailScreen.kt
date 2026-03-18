package com.example.kot1041_asm

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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

class ProductDetailScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nhận ID từ Intent được truyền qua từ HomeScreen
        // Nếu null (không có) thì gán mặc định là "1"
        val productId = intent.getStringExtra("PRODUCT_ID") ?: "1"

        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    ProductDetail(productId = productId)
                }
            }
        }
    }
}

// Hàm giả lập việc fetch dữ liệu từ API dựa trên ID
fun getMockProductData(id: String): ProductDetailData {
    return when (id) {
        "1" -> ProductDetailData(
            id = "1", name = "Black Simple Lamp", price = 12.0, rating = 4.0, reviews = 20,
            description = "A beautiful black simple lamp perfectly designed for reading and focused work. Fits seamlessly in any modern workspace.",
            images = listOf("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=600&auto=format&fit=crop", "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?q=80&w=600&auto=format&fit=crop")
        )
        "2" -> ProductDetailData(
            id = "2", name = "Minimal Stand", price = 25.0, rating = 4.5, reviews = 50,
            description = "Minimal Stand is made of by natural wood. The design that is very simple and minimal. This is truly one of the best furnitures in any family for now.",
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
fun ProductDetail(productId: String) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }
    var selectedColorIndex by remember { mutableStateOf(0) }

    // Fetch dữ liệu dựa trên productId
    val productData = remember(productId) { getMockProductData(productId) }

    val colors = listOf(
        Color(0xFF909090),
        Color(0xFFB4916C),
        Color(0xFFE4CBAD)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopImageSection(
            images = productData.images,
            colors = colors,
            selectedColorIndex = selectedColorIndex,
            onColorSelected = { selectedColorIndex = it },
            onBackClick = { (context as? Activity)?.finish() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = productData.name,
                style = Typography.displayMedium,
                color = Primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ ${if (productData.price % 1 == 0.0) productData.price.toInt() else productData.price} ",
                    style = Typography.titleMedium.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary
                )

                QuantitySelector(
                    quantity = quantity,
                    onIncrease = { quantity++ },
                    onDecrease = { if (quantity > 1) quantity-- }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFF2C94C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = productData.rating.toString(),
                    style = Typography.titleMedium.copy(fontSize = 18.sp),
                    color = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "(${productData.reviews} reviews)",
                    style = Typography.labelSmall.copy(fontSize = 14.sp),
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = productData.description,
                style = Typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Justify
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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
    images: List<String>,
    colors: List<Color>,
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(bottomStart = 50.dp))
                .background(SecondaryButtonBG)
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
                            .width(if (isSelected) 24.dp else 12.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) Primary else Color.White.copy(alpha = 0.5f))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 24.dp, top = 40.dp)
                .size(40.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(16.dp),
                tint = Primary
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                .background(Color.White, RoundedCornerShape(50.dp))
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            colors.forEachIndexed { index, color ->
                val isSelected = index == selectedColorIndex
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onColorSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
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
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SecondaryButtonBG)
                .clickable { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Text("-", fontSize = 20.sp, color = Primary, fontWeight = FontWeight.Medium)
        }

        Text(
            text = quantity.toString().padStart(2, '0'),
            style = Typography.titleMedium.copy(fontSize = 18.sp),
            color = Primary
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SecondaryButtonBG)
                .clickable { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 20.sp, color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BottomActionRow(onAddToCart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SecondaryButtonBG)
                .clickable { /* Handle Bookmark */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Favorite,
                contentDescription = "Bookmark",
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = Primary),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Add to cart",
                style = Typography.titleMedium.copy(color = Color.White, fontSize = 18.sp)
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