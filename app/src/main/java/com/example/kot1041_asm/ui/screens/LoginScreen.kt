package com.example.kot1041_asm.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.api.RetrofitClient
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import LoginRequest // Đảm bảo bạn đã import đúng đường dẫn chứa class LoginRequest
import android.content.Context

@Composable
fun Login(
    isLoggedIn: Boolean,
    onSetLoggedIn: (Boolean) -> Unit,
    onNavigateHome: () -> Unit,
    onClickRegister: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    // Lắng nghe trạng thái đăng nhập để chuyển trang
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onNavigateHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            RenderLogo()

            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(start = 12.dp)) {
                Text(
                    text = "Hello !",
                    style = TextStyle(
                        fontFamily = MerriweatherRegular,
                        fontSize = 30.sp,
                        lineHeight = 45.sp,
                        color = Grey2
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "WELCOME BACK",
                    style = TextStyle(
                        fontFamily = MerriweatherBold,
                        fontSize = 24.sp,
                        lineHeight = 45.sp,
                        letterSpacing = 0.05.em,
                        color = BlackFont
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

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
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RenderTextInput(
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    error = emailError
                )

                Spacer(modifier = Modifier.height(18.dp))

                RenderPasswordInput(
                    label = "Password",
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    error = passwordError
                )

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = { },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF3A3A3A))
                ) {
                    Text(
                        text = "Forgot Password",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 18.sp,
                            color = BlackFont
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        emailError = null
                        passwordError = null

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

                        // Gọi API đăng nhập với coroutines
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val loginRequest = LoginRequest(Email = email, Password = password)
                                val response = withContext(Dispatchers.IO) {
                                    RetrofitClient.instance.login(loginRequest)
                                }

                                if (response.isSuccessful && response.body() != null) {
                                    val apiResponse = response.body()
                                    // Dựa vào JSON bạn cung cấp, dữ liệu nằm trong trường "account"
                                    // Nếu model ApiResponse của bạn dùng trường "data" thì hãy đổi apiResponse.account thành apiResponse.data
                                    val account = apiResponse?.data

                                    if (account != null) {
                                        // --- LƯU VÀO SHAREDPREFERENCES ---
                                        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                        sharedPref.edit().apply {
                                            putString("user_id", account.id) // id từ JSON
                                            putString("user_email", account.Email)
                                            putString("user_name", account.FullName)
                                            putBoolean("is_logged_in", true)
                                            apply()
                                        }

                                        Toast.makeText(context, "Login Successfully!", Toast.LENGTH_SHORT).show()
                                        onSetLoggedIn(true)
                                    }
                                } else {
                                    Toast.makeText(context, "Login Failed! Invalid Email or Password!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi kết nối: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
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
                            text = "Log in",
                            style = TextStyle(
                                fontFamily = NunitoSansSemiBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = { onClickRegister() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2F2F2F))
                ) {
                    Text(
                        text = "SIGN UP",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 18.sp,
                            color = BlackFont
                        )
                    )
                }
            }
        }
    }
}

// =================== CÁC COMPONENT DÙNG CHUNG ===================
@Composable
fun RenderLogo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFD0D0D0))
        Spacer(modifier = Modifier.width(18.dp))
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(width = 1.dp, color = Color(0xFF9C9C9C), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize().padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(18.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFD0D0D0))
    }
}

@Composable
fun RenderTextInput(
    label: String, value: String, onValueChange: (String) -> Unit, error: String? = null
) {
    val isError = error != null
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
            style = TextStyle(
                fontFamily = NunitoSansRegular, fontSize = 14.sp, color = if (!isError) Grey2 else Color.Red
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            textStyle = TextStyle(
                fontFamily = NunitoSansRegular, fontSize = 14.sp, color = if (!isError) Grey2 else Color.Red
            ),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 1.dp).heightIn(min = 44.dp),
            colors = inputColors()
        )
    }
}

@Composable
fun RenderPasswordInput(
    label: String, value: String, onValueChange: (String) -> Unit, error: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
            style = TextStyle(
                fontFamily = NunitoSansRegular, fontSize = 14.sp, color = if (!isError) Grey2 else Color.Red
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            textStyle = TextStyle(
                fontFamily = NunitoSansRegular, fontSize = 14.sp, color = if (!isError) Grey2 else Color.Red
            ),
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 1.dp).heightIn(min = 44.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.ic_hide else R.drawable.ic_show),
                        contentDescription = "Toggle password",
                        modifier = Modifier.size(22.dp),
                        tint = if (isError) MaterialTheme.colorScheme.error else Color(0xFF3B3B3B)
                    )
                }
            },
            colors = inputColors()
        )
    }
}

@Composable
fun inputColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedIndicatorColor = Color(0xFFD7D7D7),
    unfocusedIndicatorColor = Color(0xFFE4E4E4),
    errorIndicatorColor = MaterialTheme.colorScheme.error,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    errorTextColor = Color.Black,
    cursorColor = Color.Black
)

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    KOT1041_ASMTheme {
        Login(
            isLoggedIn = false,
            onSetLoggedIn = {},
            onNavigateHome = {},
            onClickRegister = {}
        )
    }
}