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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.*

// Data model for an order item
data class OrderItem(
    val id: String,
    val orderNo: String,
    val date: String,
    val quantity: Int,
    val totalAmount: Double,
    val status: String // "Delivered", "Processing", "Canceled"
)

// Mock data for preview
val mockOrders = listOf(
    OrderItem("1", "238562312", "20/03/2020", 3, 150.0, "Delivered"),
    OrderItem("2", "238562312", "20/03/2020", 3, 150.0, "Delivered"),
    OrderItem("3", "238562312", "20/03/2020", 3, 150.0, "Delivered")
)

@Composable
fun OrderHistoryScreen(
    orders: List<OrderItem> = mockOrders,
    onBackClick: () -> Unit = {},
    onDetailClick: (String) -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Delivered, 1: Processing, 2: Canceled
    val tabs = listOf("Delivered", "Processing", "Canceled")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OrderHeader(onBackClick = onBackClick)

        OrderTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Only display orders for the selected tab for simplicity in this preview
        val displayedOrders = if (selectedTab == 0) {
            orders.filter { it.status == "Delivered" }
        } else {
            emptyList()
        }

        if (displayedOrders.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No orders found for this status",
                    style = TextStyle(fontFamily = NunitoSansRegular, color = Color(0xFF909090))
                )
            }
        } else {
            OrderList(
                orders = displayedOrders,
                onDetailClick = onDetailClick
            )
        }
    }
}

@Composable
fun OrderHeader(onBackClick: () -> Unit) {
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
            text = "My order",
            style = TextStyle(
                fontFamily = MerriweatherBold,
                fontSize = 18.sp,
                color = Color(0xFF303030)
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(48.dp)) // Balanced space
    }
}

@Composable
fun OrderTabs(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = if (isSelected) NunitoSansBold else NunitoSansRegular,
                        fontSize = 18.sp,
                        color = if (isSelected) Color(0xFF303030) else Color(0xFF909090)
                    )
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp) // Adjusted to title length
                            .height(4.dp)
                            .background(Color(0xFF303030), RoundedCornerShape(2.dp))
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp)) // Spacer to maintain Row height
                }
            }
        }
    }
}

@Composable
fun OrderList(
    orders: List<OrderItem>,
    onDetailClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            OrderItemCard(order = order, onDetailClick = onDetailClick)
        }
    }
}

@Composable
fun OrderItemCard(
    order: OrderItem,
    onDetailClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
            .background(Color.White, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Order No and Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order No${order.orderNo}",
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF303030)
                    )
                )
                Text(
                    text = order.date,
                    style = TextStyle(
                        fontFamily = NunitoSansRegular,
                        fontSize = 16.sp,
                        color = Color(0xFF909090)
                    )
                )
            }

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            // Row 2: Quantity and Total Amount
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Quantity: ",
                        style = TextStyle(
                            fontFamily = NunitoSansRegular,
                            fontSize = 16.sp,
                            color = Color(0xFF909090)
                        )
                    )
                    Text(
                        text = order.quantity.toString().padStart(2, '0'),
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF303030)
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total Amount: ",
                        style = TextStyle(
                            fontFamily = NunitoSansRegular,
                            fontSize = 16.sp,
                            color = Color(0xFF909090)
                        )
                    )
                    Text(
                        text = "$${String.format("%.0f", order.totalAmount)}",
                        style = TextStyle(
                            fontFamily = NunitoSansBold,
                            fontSize = 16.sp,
                            color = Color(0xFF303030)
                        )
                    )
                }
            }

            // Row 3: Button and Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onDetailClick(order.id) },
                    modifier = Modifier
                        .width(100.dp)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF303030),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Detail",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    )
                }

                if (order.status == "Delivered") {
                    Text(
                        text = "Delivered",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF27AE60) // Delivered status color (from design)
                        )
                    )
                } else if (order.status == "Processing") {
                    Text(
                        text = "Processing",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFFFF9800) // Example Processing color
                        )
                    )
                } else if (order.status == "Canceled") {
                    Text(
                        text = "Canceled",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFFE53935) // Example Canceled color
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderHistoryScreenPreview() {
    KOT1041_ASMTheme {
        OrderHistoryScreen()
    }
}