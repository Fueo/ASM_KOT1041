package com.example.kot1041_asm

import android.app.Activity
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme

class RegisterScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                SignUp()
            }
        }
    }
}

@Composable
fun SignUp() {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

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

            RenderLogo()

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "WELCOME",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color(0xFF303030)
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
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    error = nameError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderTextInput(
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    error = emailError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderPasswordInput(
                    label = "Password",
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    error = passwordError
                )

                Spacer(modifier = Modifier.height(6.dp))

                RenderPasswordInput(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    error = confirmPasswordError
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        // Reset các lỗi trước khi check lại
                        nameError = null
                        emailError = null
                        passwordError = null
                        confirmPasswordError = null

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

                        // Không có lỗi -> Đăng ký
                        handleSignUp()
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
                        text = "SIGN UP",
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have account? ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF909090)
                        )
                    )

                    TextButton(
                        onClick = {(context as? Activity)?.finish()},
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF303030)
                        )
                    ) {
                        Text(
                            text = "SIGN IN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF303030)
                            )
                        )
                    }
                }
            }
        }
    }
}

fun handleSignUp() {
    println("Handle sign up!")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    KOT1041_ASMTheme {
        SignUp()
    }
}