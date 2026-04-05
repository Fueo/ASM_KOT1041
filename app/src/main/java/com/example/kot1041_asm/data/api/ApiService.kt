package com.example.kot1041_asm.data.api

import AddToCartRequest
import BookmarkRequest
import LoginRequest
import PlaceOrderRequest
import RegisterRequest
import com.example.kot1041_asm.data.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Tạo thêm một request nhỏ cho việc update số lượng
data class UpdateCartQtyRequest(val Quantity: Int)

interface ApiService {

    // 1. AUTH ROUTES (/auth)
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Account>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<Account>>

    // 2. CATEGORY ROUTES (/categories)
    @GET("categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<Category>>>

    @POST("categories")
    suspend fun createCategory(@Body category: Category): Response<ApiResponse<Category>>

    // ==========================================
    // 3. PRODUCT ROUTES (/products) - ĐÃ CẬP NHẬT CHUẨN BE
    // ==========================================

    // Đã gộp chung Search, Filter theo Category và Pagination thành 1 API duy nhất
    @GET("products")
    suspend fun getProducts(
        @Query("cateId") cateId: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 6
    ): Response<ApiResponse<List<Product>>>

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") productId: String): Response<ApiResponse<Product>>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<ApiResponse<Product>>

    @GET("products/popular")
    suspend fun getProductPopular(): Response<ApiResponse<List<Product>>>

    // ==========================================
    // 4. BOOKMARK ROUTES (/bookmarks)
    // ==========================================
    @GET("bookmarks/account/{accountId}")
    suspend fun getBookmarksByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<Bookmark>>>

    // Thay thế POST (create) và DELETE (delete) thành 1 hàm Toggle duy nhất
    @POST("bookmarks/toggle")
    suspend fun toggleBookmark(@Body request: BookmarkRequest): Response<ApiResponse<Bookmark>>
    // ==========================================
    // 5. BILL / ORDER ROUTES (/bills) - ĐÃ CẬP NHẬT CHUẨN BE
    // ==========================================
    @POST("bills")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<ApiResponse<Bill>>

    // Lấy lịch sử đặt hàng theo email (Trả về 1 MẢNG các OrderResponse)
    @GET("bills/history/{email}")
    suspend fun getOrderHistoryByEmail(@Path("email") email: String): Response<ApiResponse<List<OrderResponse>>>

    // Lấy chi tiết 1 đơn hàng theo billId (Trả về 1 OBJECT OrderResponse duy nhất)
    @GET("bills/{billId}")
    suspend fun getBillDetail(@Path("billId") billId: String): Response<ApiResponse<OrderResponse>>

    // ==========================================
    // 6. CART ROUTES (/cart)
    // ==========================================
    @GET("cart/account/{accountId}")
    suspend fun getCartByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<CartItem>>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartItem>>

    @PUT("cart/{id}")
    suspend fun updateCartItemQuantity(@Path("id") cartItemId: String, @Body request: UpdateCartQtyRequest): Response<ApiResponse<CartItem>>

    @DELETE("cart/{id}")
    suspend fun removeFromCart(@Path("id") cartItemId: String): Response<ApiResponse<CartItem>>
}

// ==========================================
// RETROFIT CLIENT (SINGLETON)
// ==========================================
object RetrofitClient {
    // Nhớ update link ngrok mới mỗi khi chạy lại ngrok nhé
    private const val BASE_URL = "https://unfitting-cyclicly-dell.ngrok-free.dev/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}