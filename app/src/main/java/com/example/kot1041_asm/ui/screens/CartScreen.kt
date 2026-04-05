package com.example.kot1041_asm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.*

// Data model cho giỏ hàng
data class CartItemModel(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String
)

@Composable
fun Cart(
    onBackClick: () -> Unit = {},
    onCheckoutClick: (Double) -> Unit = {}
) {
    val context = LocalContext.current

    val cartItems = remember {
        mutableStateListOf(
            CartItemModel("1", "Minimal Stand", 25.00, 1, "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=600&auto=format&fit=crop"),
            CartItemModel("2", "Coffee Table", 20.00, 1, "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?q=80&w=600&auto=format&fit=crop"),
            CartItemModel("3", "Minimal Desk", 50.00, 1, "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?q=80&w=600&auto=format&fit=crop")
        )
    }

    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Header
        CartHeader(onBackClick = onBackClick)

        // 2. Danh sách sản phẩm
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            }
        }

        // 3. Phần thanh toán (Promo Code & Total)
        CheckoutSection(
            totalPrice = totalPrice,
            onCheckoutClick = { onCheckoutClick(totalPrice) }
        )
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
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF303030)
            )
        }

        Text(
            text = "My cart",
            style = TextStyle(
                fontFamily = MerriweatherBold,
                fontSize = 18.sp,
                color = Color(0xFF303030)
            ),
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
                .background(Color(0xFFF5F5F5))
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
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF909090)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ ${String.format("%.2f", item.price)}",
                        style = TextStyle(
                            fontFamily = NunitoSansBold,
                            fontSize = 16.sp,
                            color = Color(0xFF303030)
                        )
                    )
                }

                // Nút Xóa
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Remove",
                    tint = Color(0xFF303030),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemove() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Số lượng
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        tint = Color(0xFF303030),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = item.quantity.toString().padStart(2, '0'),
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.05.em,
                        color = Color(0xFF303030)
                    )
                )

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
                        tint = Color(0xFF303030),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutSection(
    totalPrice: Double,
    onCheckoutClick: () -> Unit
) {
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
                        style = TextStyle(
                            fontFamily = NunitoSansRegular,
                            fontSize = 14.sp,
                            color = Color(0xFF909090)
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF303030),
                    unfocusedTextColor = Color(0xFF303030)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Nút áp dụng Promo Code
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF303030))
                    .clickable { /* Handle Promo code apply */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_white),
                    contentDescription = "Apply",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

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
                style = TextStyle(
                    fontFamily = NunitoSansSemiBold,
                    fontSize = 20.sp,
                    color = Color(0xFF909090)
                )
            )
            Text(
                text = "$ ${String.format("%.2f", totalPrice)}",
                style = TextStyle(
                    fontFamily = NunitoSansSemiBold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF303030)
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút Check out
        Button(
            onClick = onCheckoutClick, // Sử dụng Callback
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0xFF303030)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303030)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Check out",
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
fun CartScreenPreview() {
    KOT1041_ASMTheme {
        Cart()
    }
}