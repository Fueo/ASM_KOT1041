package com.example.kot1041_asm

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

class CheckoutScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nhận tổng tiền từ màn hình Cart truyền qua (nếu có), mặc định là 95.0
        val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 95.00)

        setContent {
            KOT1041_ASMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // Màu nền hơi xám nhẹ để nổi bật các Card trắng
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
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // Header
        CheckoutHeader(onBackClick = { (context as? Activity)?.finish() })

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // 1. Shipping Address Section
            SectionTitle(title = "Shipping Address", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(12.dp))
            ShippingAddressCard(
                name = "Bruno Fernandes",
                address = "25 rue Robert Latouche, Nice, 06200, Côte D'azur, France"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Payment Section
            SectionTitle(title = "Payment", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(12.dp))
            PaymentCard(
                cardNumber = "**** **** **** 3947",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Mastercard-logo.svg/1280px-Mastercard-logo.svg.png" // Mock Mastercard logo
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Delivery Method Section
            SectionTitle(title = "Delivery method", onEditClick = { /* Handle edit */ })
            Spacer(modifier = Modifier.height(12.dp))
            DeliveryMethodCard(
                methodName = "Fast (2-3days)",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/DHL_Express_logo.svg/2560px-DHL_Express_logo.svg.png" // Mock DHL logo
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Summary Card
            SummaryCard(orderPrice = orderTotal, deliveryPrice = deliveryFee, totalPrice = total)

            Spacer(modifier = Modifier.height(30.dp))

            // 5. Submit Button
            Button(
                onClick = {
                    Toast.makeText(context, "Order Submitted Successfully!", Toast.LENGTH_SHORT).show()
                    // Chuyển sang màn hình Success ở đây
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), spotColor = Primary),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "SUBMIT ORDER",
                    style = Typography.titleMedium.copy(color = Color.White, fontSize = 18.sp)
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
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Primary
            )
        }

        Text(
            text = "Check out",
            style = Typography.displaySmall,
            color = Primary,
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
            style = Typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = TextSecondary
        )
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "Edit $title",
            tint = TextSecondary,
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
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = name,
            style = Typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            color = Primary,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider(color = SecondaryButtonBG, thickness = 1.dp)
        Text(
            text = address,
            style = Typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 22.sp),
            color = TextSecondary,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun PaymentCard(cardNumber: String, logoUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Thẻ
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 40.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = "Card Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = cardNumber,
            style = Typography.bodySmall.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            color = Primary
        )
    }
}

@Composable
fun DeliveryMethodCard(methodName: String, logoUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Đơn vị vận chuyển
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 40.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = "Delivery Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = methodName,
            style = Typography.bodySmall.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            color = Primary
        )
    }
}

@Composable
fun SummaryCard(orderPrice: Double, deliveryPrice: Double, totalPrice: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryRow(label = "Order:", value = orderPrice, isBold = false)
        SummaryRow(label = "Delivery:", value = deliveryPrice, isBold = false)
        SummaryRow(label = "Total:", value = totalPrice, isBold = true)
    }
}

@Composable
fun SummaryRow(label: String, value: Double, isBold: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.bodySmall.copy(fontSize = 16.sp),
            color = TextSecondary
        )
        Text(
            text = "$ ${String.format("%.2f", value)}",
            style = Typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = Primary
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