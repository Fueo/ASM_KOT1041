package com.example.kot1041_asm.ui.screens

import AddToCartRequest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.kot1041_asm.R
import com.example.kot1041_asm.data.api.RetrofitClient
import com.example.kot1041_asm.data.model.Category
import com.example.kot1041_asm.data.model.Product
import com.example.kot1041_asm.data.repository.AppRepository
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AppRepository() }

    // --- 1. LẤY ACCOUNT ID TỪ SHAREDPREFERENCES ---
    val sharedPref = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val currentAccountId = sharedPref.getString("user_id", "") ?: ""

    // --- STATE CHO UI ---
    var cartCount by remember { mutableStateOf(0) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedCateId by remember { mutableStateOf("") }
    var isLoadingProducts by remember { mutableStateOf(false) }

    // --- 2. FETCH DỮ LIỆU BAN ĐẦU (CATEGORIES & GIỎ HÀNG) ---
    LaunchedEffect(Unit) {
        // Lấy số lượng giỏ hàng thật từ API
        if (currentAccountId.isNotEmpty()) {
            val cartResult = repository.getCart(currentAccountId)
            if (cartResult.isSuccess) {
                val cartItems = cartResult.getOrNull() ?: emptyList()
                cartCount = cartItems.sumOf { it.Quantity }
            }
        }

        // Lấy danh mục sản phẩm
        try {
            val response = withContext(Dispatchers.IO) { RetrofitClient.instance.getAllCategories() }
            if (response.isSuccessful && response.body() != null) {
                categories = response.body()?.data ?: emptyList()
                // Mặc định chọn tab đầu tiên (thường là Popular)
                if (categories.isNotEmpty()) {
                    selectedCateId = categories[0]._id
                }
            } else {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // --- 3. FETCH SẢN PHẨM (XỬ LÝ TAB POPULAR) ---
    LaunchedEffect(selectedCateId, categories) {
        if (selectedCateId.isBlank()) return@LaunchedEffect

        isLoadingProducts = true
        try {
            val selectedCategoryName = categories.find { it._id == selectedCateId }?.CateName ?: ""

            // Kiểm tra xem tab hiện tại có phải là tab phổ biến không
            val isPopularTab = selectedCateId == "PopularId" ||
                    selectedCategoryName.contains("Popular", ignoreCase = true) ||
                    (categories.isNotEmpty() && selectedCateId == categories[0]._id)

            val response = withContext(Dispatchers.IO) {
                if (isPopularTab) {
                    RetrofitClient.instance.getProductPopular() // Gọi API Popular (Rating cao)
                } else {
                    RetrofitClient.instance.getProducts(cateId = selectedCateId, page = 1, limit = 20)
                }
            }

            if (response.isSuccessful) {
                products = response.body()?.data ?: emptyList()
            } else {
                products = emptyList()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
            products = emptyList()
        } finally {
            isLoadingProducts = false
        }
    }

    // --- GIAO DIỆN ---
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        Spacer(modifier = Modifier.height(16.dp))

        HeaderSection(cartCount = cartCount, onSearchClick = onSearchClick, onCartClick = onCartClick)

        Spacer(modifier = Modifier.height(18.dp))

        RenderCategories(
            categories = categories,
            selectedCateId = selectedCateId,
            onCategorySelected = { newCateId -> selectedCateId = newCateId }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoadingProducts) {
                CircularProgressIndicator(color = Color(0xFF303030))
            } else if (products.isEmpty()) {
                Text(
                    text = "No products available",
                    style = TextStyle(fontFamily = NunitoSansRegular, color = Color(0xFF909090))
                )
            } else {
                RenderProductGrid(
                    products = products,
                    onAddToCart = { product ->
                        if (currentAccountId.isEmpty()) {
                            Toast.makeText(context, "Please login to add items", Toast.LENGTH_SHORT).show()
                            return@RenderProductGrid
                        }

                        coroutineScope.launch {
                            val request = AddToCartRequest(
                                AccountID = currentAccountId,
                                ProductID = product._id,
                                Quantity = 1
                            )
                            val result = repository.addToCart(request)

                            if (result.isSuccess) {
                                cartCount++ // Cập nhật Badge ngay lập tức
                                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onProductClick = onProductClick
                )
            }
        }
    }
}

@Composable
fun HeaderSection(cartCount: Int, onSearchClick: () -> Unit, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search", tint = Color(0xFF909090), modifier = Modifier.size(24.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Make home", style = TextStyle(fontFamily = MerriweatherRegular, fontSize = 18.sp, color = Color(0xFF909090)))
            Text("BEAUTIFUL", style = TextStyle(fontFamily = MerriweatherBold, fontSize = 20.sp, color = Color(0xFF303030)))
        }
        IconButton(onClick = onCartClick) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(painter = painterResource(id = R.drawable.ic_cart), contentDescription = "Cart", tint = Color(0xFF909090), modifier = Modifier.padding(4.dp).size(24.dp))
                if (cartCount > 0) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFE53935)).align(Alignment.TopEnd), contentAlignment = Alignment.Center) {
                        Text(text = if (cartCount > 99) "99+" else cartCount.toString(), style = TextStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 9.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)))
                    }
                }
            }
        }
    }
}

@Composable
fun RenderCategories(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCateId: String,
    onCategorySelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = (screenWidth - 96.dp) / 5

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(categories) { _, category ->
            val isSelected = category._id == selectedCateId

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(itemWidth)
                    .clickable { onCategorySelected(category._id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF303030) else Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(category.iconUrl)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = category.CateName,
                        modifier = Modifier.size(24.dp),
                        colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(Color(0xFF909090))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.CateName,
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = NunitoSansSemiBold,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF303030) else Color(0xFF909090)
                    )
                )
            }
        }
    }
}

@Composable
fun RenderProductGrid(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    onProductClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onAddToCart = { onAddToCart(product) },
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onProductClick(product._id) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            val imageUrl = product.productImage.firstOrNull()?.url ?: ""

            AsyncImage(
                model = imageUrl,
                contentDescription = product.ProductName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF909090).copy(alpha = 0.6f))
                    .clickable { onAddToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bag),
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.ProductName,
            maxLines = 1,
            style = TextStyle(fontFamily = NunitoSansRegular, fontSize = 14.sp, color = Color(0xFF606060))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$ ${String.format("%.2f", product.Price)}",
            style = TextStyle(fontFamily = NunitoSansBold, fontSize = 14.sp, color = Color(0xFF303030))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    KOT1041_ASMTheme {
        HomeScreen()
    }
}