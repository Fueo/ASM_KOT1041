package com.example.kot1041_asm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kot1041_asm.ui.theme.*

// Data model cho giỏ hàng
data class CartItemModel(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String
)

class CartScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Cart()
                }
            }
        }
    }
}

@Composable
fun Cart() {
    val context = LocalContext.current

    // Dữ liệu mock (có thể thay bằng dữ liệu lấy từ DB/ViewModel sau này)
    val cartItems = remember {
        mutableStateListOf(
            CartItemModel("1", "Minimal Stand", 25.00, 1, "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"),
            CartItemModel("2", "Coffee Table", 20.00, 1, "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?q=80&w=600&auto=format&fit=crop"),
            CartItemModel("3", "Minimal Desk", 50.00, 1, "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop")
        )
    }

    // Tính tổng tiền động (Tự động cập nhật khi list thay đổi)
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Header
        CartHeader(onBackClick = { (context as? Activity)?.finish() })

        // 2. Danh sách sản phẩm (Dùng weight để chiếm hết phần trống ở giữa)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(cartItems, key = { it.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = {
                        val index = cartItems.indexOf(item)
                        if (index != -1) {
                            cartItems[index] = item.copy(quantity = item.quantity + 1)
                        }
                    },
                    onDecrease = {
                        val index = cartItems.indexOf(item)
                        if (index != -1 && item.quantity > 1) {
                            cartItems[index] = item.copy(quantity = item.quantity - 1)
                        }
                    },
                    onRemove = {
                        cartItems.remove(item)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SecondaryButtonBG, thickness = 1.dp)
            }
        }

        // 3. Phần thanh toán (Promo Code & Total)
        CheckoutSection(totalPrice = totalPrice)
    }
}

@Composable
fun CartHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Primary
            )
        }

        Text(
            text = "My cart",
            style = Typography.displaySmall,
            color = Primary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Spacer để cân bằng IconButton bên trái, giúp title nằm ở giữa
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun CartItemRow(
    item: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh sản phẩm
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SecondaryButtonBG)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Thông tin
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = item.name,
                        style = Typography.bodySmall.copy(fontSize = 14.sp),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ ${String.format("%.2f", item.price)}",
                        style = Typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                }

                // Nút Xóa
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemove() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Số lượng (Nhỏ hơn ở Product Detail)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecondaryButtonBG)
                        .clickable { onIncrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 18.sp, color = Primary, fontWeight = FontWeight.Medium)
                }

                Text(
                    text = item.quantity.toString().padStart(2, '0'),
                    style = Typography.titleMedium.copy(fontSize = 16.sp),
                    color = Primary
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecondaryButtonBG)
                        .clickable { onDecrease() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", fontSize = 18.sp, color = Primary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun CheckoutSection(totalPrice: Double) {
    val context = LocalContext.current
    var promoCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Ô nhập Promo code
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = promoCode,
                onValueChange = { promoCode = it },
                placeholder = {
                    Text(
                        text = "Enter your promo code",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Nút áp dụng Promo Code
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary)
                    .clickable { /* Handle Promo code apply */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Apply",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dòng Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total:",
                style = Typography.displaySmall.copy(fontSize = 20.sp),
                color = TextSecondary
            )
            Text(
                text = "$ ${String.format("%.2f", totalPrice)}",
                style = Typography.displayMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút Check out
        Button(
            onClick = { context.startActivity(Intent(context, CheckoutScreen::class.java))},
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = Primary),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Check out",
                style = Typography.titleMedium.copy(color = Color.White, fontSize = 18.sp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    KOT1041_ASMTheme {
        Cart()
    }
}