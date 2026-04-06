package com.example.kot1041_asm.data.api

import com.example.kot1041_asm.data.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    // 1. AUTH ROUTES
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Account>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<Account>>

    // 2. CATEGORY ROUTES
    @GET("categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<Category>>>

    @POST("categories")
    suspend fun createCategory(@Body category: Category): Response<ApiResponse<Category>>

    // 3. PRODUCT ROUTES
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

    // 4. BOOKMARK ROUTES
    @GET("bookmarks/account/{accountId}")
    suspend fun getBookmarksByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<Bookmark>>>

    @POST("bookmarks/toggle")
    suspend fun toggleBookmark(@Body request: BookmarkRequest): Response<ApiResponse<Bookmark>>

    // ==========================================
    // 5. BILL / ORDER ROUTES
    // ==========================================
    @POST("bills")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<ApiResponse<Bill>>

    // Đã thay đổi email thành accountId
    @GET("bills/history/{accountId}")
    suspend fun getOrderHistoryByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<OrderResponse>>>

    @GET("bills/{billId}")
    suspend fun getBillDetail(@Path("billId") billId: String): Response<ApiResponse<OrderResponse>>

    // 6. CART ROUTES
    @GET("cart/account/{accountId}")
    suspend fun getCartByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<CartItem>>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartItem>>

    @PUT("cart/{id}")
    suspend fun updateCartItemQuantity(@Path("id") cartItemId: String, @Body request: UpdateCartQtyRequest): Response<ApiResponse<CartItem>>

    @DELETE("cart/{id}")
    suspend fun removeFromCart(@Path("id") cartItemId: String): Response<ApiResponse<CartItem>>

    // 7. ADDRESS ROUTES
    @GET("addresses/account/{accountId}")
    suspend fun getAddressesByAccount(@Path("accountId") accountId: String): Response<ApiResponse<List<Address>>>

    @POST("addresses")
    suspend fun createAddress(@Body request: AddressRequest): Response<ApiResponse<Address>>

    @PUT("addresses/{id}")
    suspend fun updateAddress(@Path("id") addressId: String, @Body request: AddressRequest): Response<ApiResponse<Address>>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") addressId: String): Response<ApiResponse<Any>>
}

object RetrofitClient {
    private const val BASE_URL = "https://unfitting-cyclicly-dell.ngrok-free.dev/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}