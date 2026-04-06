package com.example.kot1041_asm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.R
import com.example.kot1041_asm.ui.theme.*

@Composable
fun SuccessScreen(
    onTrackOrderClick: () -> Unit = {},
    onBackHomeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Tiêu đề
        Text(
            text = "SUCCESS!",
            style = TextStyle(
                fontFamily = MerriweatherBold,
                fontSize = 36.sp,
                color = Color(0xFF303030),
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 2. Vùng chứa Hình ảnh (Dùng Box để đè icon tick xanh lên hình chính)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Hình đồ nội thất
            Image(
                painter = painterResource(id = R.drawable.ic_order_success),
                contentDescription = "Order Success",
                modifier = Modifier
                    .size(260.dp)
                    .padding(bottom = 20.dp), // Chừa không gian phía dưới cho dấu tick
                contentScale = ContentScale.Fit
            )

            // Dấu tick xanh đè lên ở chính giữa cạnh dưới
            Image(
                painter = painterResource(id = R.drawable.ic_success),
                contentDescription = "Success Icon",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(50.dp)
                    .offset(y = 10.dp) // Kéo dịch xuống một chút để tạo hiệu ứng đè góc
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 3. Câu thông báo
        Text(
            text = "Your order will be delivered soon.\nThank you for choosing our app!",
            style = TextStyle(
                fontFamily = NunitoSansRegular,
                fontSize = 16.sp,
                color = Color(0xFF606060),
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 4. Nút Track Orders (Nền đen chữ trắng)
        Button(
            onClick = onTrackOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0xFF303030)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF303030),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Track your orders",
                style = TextStyle(
                    fontFamily = NunitoSansSemiBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Nút Back To Home (Nền trắng viền đen)
        OutlinedButton(
            onClick = onBackHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF303030),
                containerColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF303030))
        ) {
            Text(
                text = "BACK TO HOME",
                style = TextStyle(
                    fontFamily = NunitoSansSemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF303030)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    KOT1041_ASMTheme {
        SuccessScreen()
    }
}