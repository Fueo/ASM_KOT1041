package com.example.kot1041_asm.ui.screens

import android.content.Context
import android.widget.Toast
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
import com.example.kot1041_asm.data.model.CartItem
import com.example.kot1041_asm.data.repository.AppRepository
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun Cart(
    onBackClick: () -> Unit = {},
    onCheckoutClick: (Double) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AppRepository() }

    // Lấy AccountID từ SharedPreferences
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val currentAccountId = sharedPref.getString("user_id", "") ?: ""

    // States
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Hàm load dữ liệu giỏ hàng
    fun loadCartData() {
        if (currentAccountId.isEmpty()) return
        coroutineScope.launch {
            isLoading = true
            val result = repository.getCart(currentAccountId)
            if (result.isSuccess) {
                cartItems = result.getOrNull() ?: emptyList()
            } else {
                Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    // 1. Fetch dữ liệu khi vừa vào màn hình
    LaunchedEffect(Unit) {
        loadCartData()
    }

    // Tính tổng tiền dựa trên giá sản phẩm và số lượng
    val totalPrice = cartItems.sumOf { (it.ProductID?.Price ?: 0.0) * it.Quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Header
        CartHeader(onBackClick = onBackClick)

        // 2. Danh sách sản phẩm hoặc Thông báo trống
        // Đã thêm fillMaxSize() vào Box để đảm bảo căn giữa theo cả chiều dọc và ngang
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            if (isLoading && cartItems.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF303030)
                )
            } else if (cartItems.isEmpty()) {
                Text(
                    text = "Your cart is empty",
                    style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 16.sp, color = Color(0xFF909090)),
                    modifier = Modifier.align(Alignment.Center) // Căn giữa hoàn hảo
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems, key = { it._id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = {
                                coroutineScope.launch {
                                    val newQty = item.Quantity + 1
                                    // Cập nhật UI ngay lập tức
                                    cartItems = cartItems.map { if (it._id == item._id) it.copy(Quantity = newQty) else it }
                                    // Gọi API update
                                    repository.updateCartQuantity(item._id, newQty)
                                }
                            },
                            onDecrease = {
                                if (item.Quantity > 1) {
                                    coroutineScope.launch {
                                        val newQty = item.Quantity - 1
                                        cartItems = cartItems.map { if (it._id == item._id) it.copy(Quantity = newQty) else it }
                                        repository.updateCartQuantity(item._id, newQty)
                                    }
                                }
                            },
                            onRemove = {
                                coroutineScope.launch {
                                    // Xoá khỏi UI ngay lập tức
                                    cartItems = cartItems.filter { it._id != item._id }
                                    // Gọi API xoá
                                    val res = repository.removeFromCart(item._id)
                                    if (res.isSuccess) {
                                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Nếu xoá lỗi trên server, load lại data cũ
                                        loadCartData()
                                        Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    }
                }
            }
        }

        // 3. Phần thanh toán (Promo Code & Total)
        CheckoutSection(
            totalPrice = totalPrice,
            onCheckoutClick = {
                if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                } else {
                    onCheckoutClick(totalPrice)
                }
            }
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

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    // Trích xuất dữ liệu từ object Product
    val productName = item.ProductID?.ProductName ?: "Unknown Product"
    val productPrice = item.ProductID?.Price ?: 0.0
    val imageUrl = item.ProductID?.productImage?.firstOrNull()?.url ?: ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = productName,
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF909090)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$ ${String.format("%.2f", productPrice)}",
                        style = TextStyle(
                            fontFamily = NunitoSansBold,
                            fontSize = 16.sp,
                            color = Color(0xFF303030)
                        )
                    )
                }

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    text = item.Quantity.toString().padStart(2, '0'),
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.05.em,
                        color = Color(0xFF303030)
                    )
                )

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

        Button(
            onClick = onCheckoutClick,
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