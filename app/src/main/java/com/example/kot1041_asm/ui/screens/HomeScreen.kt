package com.example.kot1041_asm.ui.screens

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
import com.example.kot1041_asm.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {} // Vẫn giữ callback này phòng trường hợp bạn cần sau này
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var cartCount by remember { mutableStateOf(0) }

    // --- STATE CHO API ---
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    // selectedCateId mặc định là "" (rỗng) để lấy tất cả sản phẩm ("Popular")
    var selectedCateId by remember { mutableStateOf("") }
    var isLoadingProducts by remember { mutableStateOf(false) }

    // 1. Fetch Categories khi vừa vào màn hình
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) { RetrofitClient.instance.getAllCategories() }
            if (response.isSuccessful && response.body() != null) {
                // BE có thể đã trả về cả mảng trong đó item đầu tiên là "Popular"
                categories = response.body()?.data ?: emptyList()

                // Mặc định chọn phần tử đầu tiên trong mảng
                if (categories.isNotEmpty()) {
                    selectedCateId = categories[0]._id
                }
            } else {
                Toast.makeText(context, "Lỗi lấy danh mục", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Fetch Products mỗi khi selectedCateId thay đổi
    LaunchedEffect(selectedCateId) {
        isLoadingProducts = true
        try {
            // Nếu selectedCateId là ID của thẻ "Popular" (BE thiết lập là chuỗi rỗng hoặc giá trị cụ thể),
            // bạn có thể truyền null hoặc chính selectedCateId đó vào getProducts()
            val cateParam = if (selectedCateId.isBlank() || selectedCateId == "PopularId") null else selectedCateId

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getProducts(cateId = cateParam, page = 1, limit = 20)
            }
            if (response.isSuccessful) {
                products = response.body()?.data ?: emptyList()
            } else {
                products = emptyList() // Trống khi lỗi
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi lấy sản phẩm", Toast.LENGTH_SHORT).show()
            products = emptyList()
        } finally {
            isLoadingProducts = false
        }
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection(cartCount = cartCount, onSearchClick = onSearchClick, onCartClick = onCartClick)

        Spacer(modifier = Modifier.height(18.dp))

        // Render Categories
        RenderCategories(
            categories = categories,
            selectedCateId = selectedCateId,
            onCategorySelected = { newCateId -> selectedCateId = newCateId }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Vùng hiển thị sản phẩm, chiếm toàn bộ không gian còn lại
        Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoadingProducts) {
                CircularProgressIndicator(
                    color = Color(0xFF303030)
                )
            } else if (products.isEmpty()) {
                Text(
                    text = "Không có sản phẩm nào",
                    style = TextStyle(fontFamily = NunitoSansRegular, color = Color(0xFF909090))
                )
            } else {
                // Nếu có sản phẩm thì render grid
                RenderProductGrid(
                    products = products,
                    onAddToCart = {
                        cartCount++
                        Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                        onAddToCart()
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
                    // Dùng ImageRequest để nhúng SvgDecoder vào AsyncImage
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(category.iconUrl)
                            .decoderFactory(SvgDecoder.Factory()) // Quan trọng: Bật bộ giải mã SVG
                            .build(),
                        contentDescription = category.CateName,
                        modifier = Modifier.size(24.dp),
                        // Tint lại màu icon: Trắng nếu chọn, xám nếu không chọn
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
    onAddToCart: () -> Unit,
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
                onAddToCart = onAddToCart,
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