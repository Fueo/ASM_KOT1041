package com.example.kot1041_asm.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.*

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onOrderHistoryClick: () -> Unit = {},
) {
    val context = LocalContext.current

    // --- LẤY DỮ LIỆU TỪ SHAREDPREFERENCES ---
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val userName = sharedPref.getString("user_name", "Guest") ?: "Guest"
    val userEmail = sharedPref.getString("user_email", "No Email") ?: "No Email"

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Confirm Logout",
                    style = TextStyle(fontFamily = MerriweatherBold, fontSize = 20.sp, color = Color(0xFF303030))
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 16.sp, color = Color(0xFF606060))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // --- XOÁ DỮ LIỆU KHI ĐĂNG XUẤT (TUỲ CHỌN) ---
                        // sharedPref.edit().clear().apply()

                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        onLogoutClick()
                    }
                ) {
                    Text("Yes", style = TextStyle(fontFamily = NunitoSansBold, color = Color(0xFFE53935)))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel", style = TextStyle(fontFamily = NunitoSansBold, color = Color(0xFF303030)))
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = Color(0xFF303030),
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Profile",
                style = TextStyle(fontFamily = MerriweatherBold, fontSize = 18.sp, color = Color(0xFF303030))
            )

            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color(0xFF303030),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // 2. Thông tin User (DỮ LIỆU THẬT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BIỂU TƯỢNG AVATAR TRỐNG TƯỢNG TRƯNG
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF909090)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = userName, // Lấy từ SharedPreferences
                        style = TextStyle(fontFamily = NunitoSansBold, fontSize = 20.sp, color = Color(0xFF303030))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userEmail, // Lấy từ SharedPreferences
                        style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 14.sp, color = Color(0xFF909090))
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Danh sách Menu
            ProfileMenuItem(title = "My orders", subtitle = "Already have 10 orders", onClick = onOrderHistoryClick)

            // --- GẮN SỰ KIỆN CLICK VÀO ĐÂY ---
            ProfileMenuItem(
                title = "Shipping Addresses",
                subtitle = "Manage your addresses",
                onClick = onAddressClick
            )

            ProfileMenuItem(title = "Payment Method", subtitle = "You have 2 cards")
            ProfileMenuItem(title = "My reviews", subtitle = "Reviews for 5 items")
            ProfileMenuItem(title = "Setting", subtitle = "Notification, Password, FAQ, Contact")

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0xFF000000).copy(alpha = 0.05f))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(fontFamily = NunitoSansBold, fontSize = 18.sp, color = Color(0xFF303030))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 12.sp, color = Color(0xFF909090))
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF303030),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}