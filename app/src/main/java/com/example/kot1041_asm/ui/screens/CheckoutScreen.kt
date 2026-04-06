package com.example.kot1041_asm.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.model.Address
import com.example.kot1041_asm.data.model.AddressRequest
import com.example.kot1041_asm.data.model.CartItem
import com.example.kot1041_asm.data.model.CartItemRequest
import com.example.kot1041_asm.data.model.PlaceOrderRequest
import com.example.kot1041_asm.data.repository.AppRepository
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun Checkout(
    orderTotal: Double, // Tham số này có thể giữ lại cho NavGraph không bị lỗi, nhưng ta sẽ tính lại từ API
    onBackClick: () -> Unit = {},
    onSubmitOrder: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AppRepository() }
    val scrollState = rememberScrollState()

    // Lấy AccountID từ SharedPreferences
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val currentAccountId = sharedPref.getString("user_id", "") ?: ""

    // States dữ liệu
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }

    // Fetch Giỏ hàng và Địa chỉ
    LaunchedEffect(currentAccountId) {
        if (currentAccountId.isNotEmpty()) {
            isLoading = true

            // 1. Lấy giỏ hàng
            val cartResult = repository.getCart(currentAccountId)
            if (cartResult.isSuccess) {
                cartItems = cartResult.getOrNull() ?: emptyList()
            }

            // 2. Lấy địa chỉ
            val addressResult = repository.getAddresses(currentAccountId)
            if (addressResult.isSuccess) {
                val list = addressResult.getOrNull() ?: emptyList()
                addresses = list
                // Tự động chọn địa chỉ isDefault = true, nếu không có thì lấy cái đầu tiên
                selectedAddress = list.find { it.isDefault } ?: list.firstOrNull()
            }

            isLoading = false
        }
    }

    // Tính toán lại tiền
    val calculatedTotal = cartItems.sumOf { (it.ProductID?.Price ?: 0.0) * it.Quantity }
    val deliveryFee = 5.00
    val finalTotal = calculatedTotal + deliveryFee

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        CheckoutHeader(onBackClick = onBackClick)

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF303030))
            }
        } else {
            // Nội dung cuộn
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Shipping Address
                SectionTitle(title = "Shipping Address", onEditClick = { showAddressDialog = true })
                Spacer(modifier = Modifier.height(10.dp))
                if (selectedAddress != null) {
                    ShippingAddressCard(
                        name = selectedAddress!!.RecipientName,
                        address = selectedAddress!!.AddressDetail
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.LightGray)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .clickable { showAddressDialog = true }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Tap to select or add shipping address",
                            style = TextStyle(fontFamily = NunitoSansSemiBold, color = Color(0xFF303030))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Payment (Giữ nguyên UI tĩnh)
                SectionTitle(title = "Payment", onEditClick = { })
                Spacer(modifier = Modifier.height(10.dp))
                PaymentCard(
                    cardNumber = "**** **** **** 3947",
                    logoRes = R.drawable.card
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Delivery (Giữ nguyên UI tĩnh)
                SectionTitle(title = "Delivery method", onEditClick = { })
                Spacer(modifier = Modifier.height(10.dp))
                DeliveryMethodCard(
                    methodName = "Fast (2-3days)",
                    logoRes = R.drawable.dhl
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 4. Summary
                SummaryCard(orderPrice = calculatedTotal, deliveryPrice = deliveryFee, totalPrice = finalTotal)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Submit Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        if (cartItems.isEmpty()) {
                            Toast.makeText(context, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (selectedAddress == null) {
                            Toast.makeText(context, "Please select an address!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        coroutineScope.launch {
                            isSubmitting = true
                            // Chuyển CartItem thành CartItemRequest
                            val orderItems = cartItems.mapNotNull {
                                val productId = it.ProductID?._id
                                if (productId != null) CartItemRequest(
                                    ProductID = productId,
                                    Quantity = it.Quantity
                                ) else null
                            }

                            val request = PlaceOrderRequest(
                                accountId = currentAccountId,
                                RecipientName = selectedAddress!!.RecipientName,
                                AddressDetail = selectedAddress!!.AddressDetail,
                                Note = "Standard Delivery",
                                items = orderItems
                            )

                            val res = repository.placeOrder(request)
                            if (res.isSuccess) {
                                Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                                onSubmitOrder() // Trở về trang Home hoặc màn Success
                            } else {
                                Toast.makeText(context, "Failed to place order: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                            isSubmitting = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0xFF303030)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF303030),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
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
    }

    // DIALOG QUẢN LÝ / CHỌN ĐỊA CHỈ
    if (showAddressDialog) {
        AddressSelectionDialog(
            addresses = addresses,
            currentSelected = selectedAddress,
            onDismiss = { showAddressDialog = false },
            onAddressSelected = {
                selectedAddress = it
                showAddressDialog = false
            },
            onAddNewAddress = { name, detail ->
                coroutineScope.launch {
                    val req = AddressRequest(
                        accountId = currentAccountId,
                        RecipientName = name,
                        AddressDetail = detail,
                        isDefault = false
                    )
                    val res = repository.createAddress(req)
                    if (res.isSuccess) {
                        val newAddr = res.getOrNull()
                        if (newAddr != null) {
                            addresses = addresses + newAddr
                            selectedAddress = newAddr
                        }
                        showAddressDialog = false
                        Toast.makeText(context, "Address added!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save address", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun AddressSelectionDialog(
    addresses: List<Address>,
    currentSelected: Address?,
    onDismiss: () -> Unit,
    onAddressSelected: (Address) -> Unit,
    onAddNewAddress: (String, String) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var newDetail by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Address",
                    style = TextStyle(fontFamily = MerriweatherBold, fontSize = 18.sp, color = Color(0xFF303030))
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Danh sách địa chỉ có sẵn
                if (addresses.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(addresses) { address ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAddressSelected(address) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (address._id == currentSelected?._id),
                                    onClick = { onAddressSelected(address) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF303030))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = address.RecipientName, style = TextStyle(fontFamily = NunitoSansBold, fontSize = 14.sp))
                                    Text(text = address.AddressDetail, style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 12.sp, color = Color.Gray))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Form Thêm mới
                Text(
                    text = "Or add new address",
                    style = TextStyle(fontFamily = NunitoSansSemiBold, fontSize = 16.sp, color = Color(0xFF303030))
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Recipient Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newDetail,
                    onValueChange = { newDetail = it },
                    label = { Text("Address Detail") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Nút bấm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newDetail.isNotBlank()) {
                                onAddNewAddress(newName, newDetail)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303030))
                    ) {
                        Text("Add & Select")
                    }
                }
            }
        }
    }
}

// ==========================================
// CÁC COMPONENTS UI (Giữ nguyên như cũ)
// ==========================================
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