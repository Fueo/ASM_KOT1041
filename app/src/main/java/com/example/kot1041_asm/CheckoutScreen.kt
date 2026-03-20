package com.example.kot1041_asm

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.kot1041_asm.ui.theme.*

class CheckoutScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nhận tổng tiền từ màn hình Cart truyền qua (nếu có), mặc định là 95.0
        val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 95.00)

        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // Nền xám nhạt để các Card màu trắng nổi bật
                ) {
                    Checkout(orderTotal = orderTotal)
                }
            }
        }
    }
}

@Composable
fun Checkout(orderTotal: Double) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val deliveryFee = 5.00
    val total = orderTotal + deliveryFee

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Đổi nền xám nhạt
    ) {
        // Header (Cố định ở trên)
        CheckoutHeader(onBackClick = { (context as? Activity)?.finish() })

        // Nội dung có thể cuộn (Sử dụng weight để đẩy nút Submit xuống đáy)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Chiếm toàn bộ không gian còn lại
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Shipping Address Section
            SectionTitle(title = "Shipping Address", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(10.dp))
            ShippingAddressCard(
                name = "Bruno Fernandes",
                address = "25 rue Robert Latouche, Nice, 06200, Côte D'azur, France"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Payment Section
            SectionTitle(title = "Payment", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(10.dp))
            PaymentCard(
                cardNumber = "**** **** **** 3947",
                logoRes = R.drawable.card // Sử dụng hình ảnh drawable tải về
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Delivery Method Section
            SectionTitle(title = "Delivery method", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(10.dp))
            DeliveryMethodCard(
                methodName = "Fast (2-3days)",
                logoRes = R.drawable.dhl // Sử dụng hình ảnh drawable tải về
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 4. Summary Card
            SummaryCard(orderPrice = orderTotal, deliveryPrice = deliveryFee, totalPrice = total)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Submit Button (Luôn hiển thị ở dưới cùng màn hình)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Order Submitted Successfully!", Toast.LENGTH_SHORT).show()
                    // Chuyển sang màn hình Success ở đây
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0xFF303030)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF303030),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SUBMIT ORDER",
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun CheckoutHeader(onBackClick: () -> Unit) {
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
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF303030)
            )
        }

        Text(
            text = "Check out",
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
fun SectionTitle(title: String, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = NunitoSansSemiBold,
                fontSize = 18.sp,
                color = Color(0xFF909090)
            )
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = "Edit $title",
            tint = Color(0xFF909090),
            modifier = Modifier
                .size(20.dp)
                .clickable { onEditClick() }
        )
    }
}

@Composable
fun ShippingAddressCard(name: String, address: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = name,
            style = TextStyle(
                fontFamily = NunitoSansBold,
                fontSize = 18.sp,
                color = Color(0xFF303030)
            ),
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
        Text(
            text = address,
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color(0xFF909090)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun PaymentCard(cardNumber: String, logoRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Thẻ - Đổ bóng trước, fill nền trắng sau để bóng không lẹm vào trong
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 40.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Card Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = cardNumber,
            style = TextStyle(
                fontFamily = NunitoSansSemiBold,
                fontSize = 14.sp,
                color = Color(0xFF303030)
            )
        )
    }
}

@Composable
fun DeliveryMethodCard(methodName: String, logoRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Đơn vị vận chuyển
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 40.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Delivery Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = methodName,
            style = TextStyle(
                fontFamily = NunitoSansSemiBold,
                fontSize = 14.sp,
                color = Color(0xFF303030)
            )
        )
    }
}

@Composable
fun SummaryCard(orderPrice: Double, deliveryPrice: Double, totalPrice: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SummaryRow(label = "Order:", value = orderPrice, isTotal = false)
        SummaryRow(label = "Delivery:", value = deliveryPrice, isTotal = false)
        SummaryRow(label = "Total:", value = totalPrice, isTotal = true)
    }
}

@Composable
fun SummaryRow(label: String, value: Double, isTotal: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 16.sp,
                color = Color(0xFF909090)
            )
        )
        Text(
            text = "$ ${String.format("%.2f", value)}",
            style = TextStyle(
                fontFamily = if (isTotal) NunitoSansBold else NunitoSansSemiBold,
                fontSize = 16.sp,
                color = Color(0xFF303030)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    KOT1041_ASMTheme {
        Checkout(orderTotal = 95.00)
    }
}