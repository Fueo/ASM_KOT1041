package com.example.kot1041_asm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.data.api.RetrofitClient
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import RegisterRequest // Đảm bảo import đúng đường dẫn chứa class RegisterRequest của bạn

@Composable
fun SignUp(
    onClickLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Tạo scope để chạy Coroutine

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Thêm state loading
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            RenderLogo() // Tái sử dụng hàm đã khai báo trong LoginScreen.kt

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "WELCOME",
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                style = TextStyle(
                    fontFamily = MerriweatherBold,
                    fontSize = 24.sp,
                    lineHeight = 45.sp,
                    letterSpacing = 0.05.em,
                    color = BlackFont
                )
            )

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .dropShadow(
                        shape = RoundedCornerShape(2.dp),
                        shadow = Shadow(
                            radius = 30.dp,
                            spread = 0.dp,
                            color = Color(0x338A959E),
                            offset = DpOffset(0.dp, 7.dp)
                        )
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RenderTextInput(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    error = nameError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderTextInput(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    error = emailError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderPasswordInput(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    error = passwordError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderPasswordInput(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = null },
                    error = confirmPasswordError
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        nameError = null; emailError = null; passwordError = null; confirmPasswordError = null

                        // 1. Validate dữ liệu
                        if (name.isBlank()) {
                            nameError = "Name không được để trống"
                            Toast.makeText(context, nameError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (name.length < 2) {
                            nameError = "Name phải có ít nhất 2 ký tự"
                            Toast.makeText(context, nameError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (email.isBlank()) {
                            emailError = "Email không được để trống"
                            Toast.makeText(context, emailError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Email không hợp lệ"
                            Toast.makeText(context, emailError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password.isBlank()) {
                            passwordError = "Password không được để trống"
                            Toast.makeText(context, passwordError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password.length < 6) {
                            passwordError = "Password phải có ít nhất 6 ký tự"
                            Toast.makeText(context, passwordError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (confirmPassword.isBlank()) {
                            confirmPasswordError = "Confirm Password không được để trống"
                            Toast.makeText(context, confirmPasswordError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (confirmPassword != password) {
                            confirmPasswordError = "Confirm Password không khớp"
                            Toast.makeText(context, confirmPasswordError, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 2. Gọi API đăng ký
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val request = RegisterRequest(
                                    Email = email,
                                    Password = password,
                                    FullName = name,
                                )

                                val response = withContext(Dispatchers.IO) {
                                    RetrofitClient.instance.register(request)
                                }

                                if (response.isSuccessful && response.body() != null) {
                                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                    // Chuyển hướng về màn hình Login
                                    onClickLogin()
                                } else {
                                    Toast.makeText(context, "Đăng ký thất bại: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi kết nối: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading, // Disable nút khi đang load
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(8.dp),
                            shadow = Shadow(
                                radius = 16.dp,
                                spread = 0.dp,
                                color = Color.Black.copy(alpha = 0.18f),
                                offset = DpOffset(0.dp, 6.dp)
                            )
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF222222),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "SIGN UP",
                            style = TextStyle(
                                fontFamily = NunitoSansSemiBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have account? ",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 14.sp,
                            color = Grey
                        )
                    )

                    TextButton(
                        onClick = { onClickLogin() },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = BlackFont)
                    ) {
                        Text(
                            text = "SIGN IN",
                            style = TextStyle(
                                fontFamily = NunitoSansBold,
                                fontSize = 14.sp,
                                color = BlackFont
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    KOT1041_ASMTheme {
        SignUp()
    }
}