package com.example.kot1041_asm.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kot1041_asm.LoginScreen // Nhớ kiểm tra lại đường dẫn import này cho đúng với file LoginScreen của bạn
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme
import com.example.kot1041_asm.ui.theme.Primary
import com.example.kot1041_asm.ui.theme.TextSecondary
import com.example.kot1041_asm.ui.theme.blackFont

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BackgroundImage(
            imageRes = R.drawable.background_welcome,
            modifier = Modifier.fillMaxSize()
        )

        WelcomeContent(
            modifier = Modifier.fillMaxSize(),
            onGetStartedClick = {
                // 1. Mở màn hình Login
                val intent = Intent(context, LoginScreen::class.java)
                context.startActivity(intent)

                // 2. Kết thúc màn hình Welcome hiện tại (Đóng hoàn toàn)
                (context as? Activity)?.finish()
            }
        )
    }
}

@Composable
private fun BackgroundImage(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun WelcomeContent(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(260.dp))

        WelcomeTitle(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(26.dp))

        WelcomeDescription(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryActionButton(
            text = "Get Started",
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(54.dp)
        )

        // Tăng chiều cao Spacer này từ 36.dp lên 100.dp để đẩy nút Get Started lên cao hơn
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun WelcomeTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "MAKE YOUR",
            style = MaterialTheme.typography.displayMedium,
            color = TextSecondary
        )

        Text(
            text = "HOME BEAUTIFUL",
            style = MaterialTheme.typography.displayLarge,
            color = blackFont
        )
    }
}

@Composable
private fun WelcomeDescription(
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = "The best simple place where you\ndiscover most wonderful furnitures\nand make your home beautiful",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Justify,
        )
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.padding(start = 12.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall.copy(
                color = Color.White
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    KOT1041_ASMTheme {
        WelcomeScreen()
    }
}