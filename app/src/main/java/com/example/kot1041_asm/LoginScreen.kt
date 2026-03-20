package com.example.kot1041_asm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.ui.theme.BlackFont
import com.example.kot1041_asm.ui.theme.Grey2
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme
import com.example.kot1041_asm.ui.theme.MerriweatherBold
import com.example.kot1041_asm.ui.theme.MerriweatherRegular
import com.example.kot1041_asm.ui.theme.NunitoSansRegular
import com.example.kot1041_asm.ui.theme.NunitoSansSemiBold

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                Login()
            }
        }
    }
}

@Composable
fun Login() {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Khôi phục lại biến State giữ trạng thái lỗi để làm đỏ viền
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 26.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            RenderLogo()

            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hello !",
                    style = TextStyle(
                        fontFamily = MerriweatherRegular,
                        fontSize =  30.sp,
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
                        emailError = null // Tắt đỏ khi user gõ lại
                    },
                    error = emailError
                )

                Spacer(modifier = Modifier.height(18.dp))

                RenderPasswordInput(
                    label = "Password",
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null // Tắt đỏ khi user gõ lại
                    },
                    error = passwordError
                )

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = { },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF3A3A3A)
                    )
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
                        if (handleLogin(email, password)) {
                            context.startActivity(Intent(context, HomeScreen::class.java))
                            return@Button
                        }

                        // Reset các lỗi trước khi check lại
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

//                         Không có lỗi -> Đăng nhập
                        if (handleLogin(email, password)) {
                            context.startActivity(Intent(context, HomeScreen::class.java))
                        }
                    },
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
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Log in",
                        style = TextStyle(
                            fontFamily = NunitoSansSemiBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = {context.startActivity(Intent(context, RegisterScreen::class.java))},
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF2F2F2F)
                    )
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

@Composable
fun RenderLogo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFD0D0D0)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF9C9C9C),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize().padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFD0D0D0)
        )
    }
}

@Composable
fun RenderTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp),
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                color = if (!isError) Grey2 else Color.Red
            )
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            textStyle = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                color = if (!isError) Grey2 else Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 1.dp)
                .heightIn(min = 44.dp),
            colors = inputColors(),
            supportingText = {}
        )
    }
}

@Composable
fun RenderPasswordInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp),
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                color = if (!isError) Grey2 else Color.Red
            )
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            isError = isError,
            textStyle = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 14.sp,
                color = if (!isError) Grey2 else Color.Red
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 1.dp)
                .heightIn(min = 44.dp),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.ic_hide else R.drawable.ic_show
                        ),
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        modifier = Modifier.size(22.dp),
                        tint = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color(0xFF3B3B3B)
                        }
                    )
                }
            },
            colors = inputColors(),
            supportingText = {}
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

fun handleLogin (email: String, password: String) : Boolean {
    return email.isNotEmpty() && password.isNotEmpty()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    KOT1041_ASMTheme {
        Login()
    }
}