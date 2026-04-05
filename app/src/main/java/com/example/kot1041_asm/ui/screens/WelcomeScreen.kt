package com.example.kot1041_asm.ui.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.BlackFont
import com.example.kot1041_asm.ui.theme.GelasioBold
import com.example.kot1041_asm.ui.theme.GelasioSemiBold
import com.example.kot1041_asm.ui.theme.Grey
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme
import com.example.kot1041_asm.ui.theme.NunitoSansRegular
import com.example.kot1041_asm.ui.theme.Primary

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit // THÊM CALLBACK Ở ĐÂY
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BackgroundImage(
            imageRes = R.drawable.background_welcome,
            modifier = Modifier.fillMaxSize()
        )

        WelcomeContent(
            modifier = Modifier.fillMaxSize(),
            onGetStartedClick = onGetStartedClick // GỌI CALLBACK THAY VÌ INTENT
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
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(231.dp))

        WelcomeTitle(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(35.dp))

        WelcomeDescription(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryActionButton(
            text = "Get Started",
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(54.dp)
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
private fun WelcomeTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "MAKE YOUR",
            style = TextStyle(
                fontFamily = GelasioSemiBold,
                fontSize = 24.sp,
                letterSpacing = 0.05.em
            ),
            color = Color(0xFF606060)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "HOME BEAUTIFUL",
            style = TextStyle(
                fontFamily = GelasioBold,
                fontSize = 30.sp,
                letterSpacing = 0.sp
            ),
            color = BlackFont,
            maxLines =  1,
            softWrap = false
        )
    }
}

@Composable
private fun WelcomeDescription(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val padding10Percent = configuration.screenWidthDp.dp * 0.05f
    Text(
        text = "The best simple place where you discover most wonderful furnitures and make your home beautiful",
        modifier = Modifier.padding(start = padding10Percent),
        style = TextStyle(
            fontFamily = NunitoSansRegular,
            fontSize = 18.sp,
            lineHeight = 35.sp,
            letterSpacing = 0.sp
        ),
        color = Grey,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            style = TextStyle(
                fontFamily = GelasioSemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp,
                color = Color.White
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    KOT1041_ASMTheme {
        // Truyền một hàm rỗng để Preview không báo lỗi
        WelcomeScreen(onGetStartedClick = {})
    }
}