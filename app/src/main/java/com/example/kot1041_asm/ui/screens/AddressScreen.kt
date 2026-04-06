package com.example.kot1041_asm.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.model.Address
import com.example.kot1041_asm.data.model.AddressRequest
import com.example.kot1041_asm.data.repository.AppRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AppRepository() }

    // --- 1. LẤY ACCOUNT ID TỪ SHAREDPREFERENCES ---
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val accountId = sharedPref.getString("user_id", "") ?: ""

    // --- STATES ---
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Dialog states
    var showDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Address?>(null) } // Nếu null = Thêm mới, có data = Chỉnh sửa
    var recipientName by remember { mutableStateOf("") }
    var addressDetail by remember { mutableStateOf("") }

    // --- HÀM FETCH DỮ LIỆU ---
    fun fetchAddresses() {
        if (accountId.isEmpty()) return
        coroutineScope.launch {
            isLoading = true
            val result = repository.getAddresses(accountId)
            if (result.isSuccess) {
                addresses = result.getOrNull() ?: emptyList()
            } else {
                Toast.makeText(context, "Failed to load addresses", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    // Lấy dữ liệu lần đầu
    LaunchedEffect(Unit) {
        fetchAddresses()
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Shipping address",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF303030))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // FIX: Đã chỉnh size 24.dp và tint đồng bộ Checkout
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF303030)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Mở dialog ở chế độ Thêm mới
                    editingAddress = null
                    recipientName = ""
                    addressDetail = ""
                    showDialog = true
                },
                containerColor = Color.White,
                contentColor = Color(0xFF303030),
                shape = CircleShape,
                modifier = Modifier.shadow(4.dp, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Address")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading && addresses.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF303030))
            } else if (addresses.isEmpty()) {
                Text(
                    text = "No addresses found. Please add a new one.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(addresses) { address ->
                        AddressItem(
                            address = address,
                            onSetDefault = {
                                if (!address.isDefault) {
                                    coroutineScope.launch {
                                        val request = AddressRequest(
                                            accountId = accountId,
                                            RecipientName = address.RecipientName,
                                            AddressDetail = address.AddressDetail,
                                            isDefault = true
                                        )
                                        repository.updateAddress(address._id, request)
                                        fetchAddresses() // Tải lại list sau khi set default
                                    }
                                }
                            },
                            onEditClick = {
                                // Mở dialog ở chế độ Chỉnh sửa
                                editingAddress = address
                                recipientName = address.RecipientName
                                addressDetail = address.AddressDetail
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        // --- DIALOG THÊM / SỬA ĐỊA CHỈ ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = if (editingAddress == null) "Add New Address" else "Edit Address") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = { Text("Recipient Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = addressDetail,
                            onValueChange = { addressDetail = it },
                            label = { Text("Address Detail") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (recipientName.isBlank() || addressDetail.isBlank()) {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                val request = AddressRequest(
                                    accountId = accountId,
                                    RecipientName = recipientName,
                                    AddressDetail = addressDetail,
                                    isDefault = editingAddress?.isDefault ?: false // Giữ nguyên trạng thái default hiện tại
                                )

                                val result = if (editingAddress == null) {
                                    repository.createAddress(request)
                                } else {
                                    repository.updateAddress(editingAddress!!._id, request)
                                }

                                if (result.isSuccess) {
                                    Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                                    showDialog = false
                                    fetchAddresses() // Tải lại list
                                } else {
                                    Toast.makeText(context, "Failed to save address", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF303030))
                    ) {
                        Text("Save", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    onSetDefault: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Dòng Checkbox "Use as the shipping address"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSetDefault() }
                .padding(bottom = 12.dp)
        ) {
            Checkbox(
                checked = address.isDefault,
                onCheckedChange = { onSetDefault() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF303030),
                    uncheckedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Use as the shipping address",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = if (address.isDefault) Color(0xFF303030) else Color.Gray
                )
            )
        }

        // 2. Thẻ chứa thông tin
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0xFF000000).copy(alpha = 0.1f))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = address.RecipientName,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF303030))
                    )

                    // FIX: Đổi từ IconButton sang Icon + clickable giống hệt CheckoutScreen
                    // Kích thước 20.dp, màu xám nhạt (0xFF909090)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF909090),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEditClick() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = address.AddressDetail,
                    style = TextStyle(fontSize = 14.sp, color = Color(0xFF909090), lineHeight = 24.sp)
                )
            }
        }
    }
}