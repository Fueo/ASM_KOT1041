package com.example.kot1041_asm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kot1041_asm.ui.theme.KOT1041_ASMTheme
class HomeScreen : ComponentActivity() {
    // Đã sửa lại thành onCreate có 1 tham số Bundle?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KOT1041_ASMTheme {
                Home(Modifier)
            }
        }
    }
}

//jetpack compose
// row, column, box

data class Category (val id : String, var name : String, var icon : String)

@Composable
fun Home(modifier: Modifier) {
    RenderCategories(Modifier)
}

@Composable
fun RenderCategories(modifier: Modifier) {
    val categories = listOf(
        Category("1", "Food", "123"),
        Category("2", "Drink", "456"),
        Category("3", "Cake", "789")
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(categories) {category ->
            Surface (
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.clickable {  }
            ) {
                Text(text = category.icon, fontSize = 34.sp)
                Spacer(Modifier.height(12.dp))
                Text(text = category.name, fontSize = 14.sp)
            }
        }
    }
}

@Composable
@Preview(showBackground = true) // Thêm showBackground để Preview dễ nhìn hơn
fun HomeReview() {
    KOT1041_ASMTheme {
        RenderCategories(Modifier)
    }
}